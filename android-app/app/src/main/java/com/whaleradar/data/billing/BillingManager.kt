package com.whaleradar.data.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Manages Google Play Billing v7 interactions for WhaleRadar:
 * - Subscriptions: whaleradar_sub_monthly, whaleradar_sub_yearly
 * - In-App One-Time Product: whaleradar_lifetime
 */
class BillingManager(
    private val context: Context,
    private val scope: CoroutineScope,
    private val onProStatusChanged: (Boolean, String?) -> Unit
) : PurchasesUpdatedListener {

    companion object {
        private const val TAG = "WhaleBilling"

        const val PRODUCT_SUB_MONTHLY = "whaleradar_sub_monthly"
        const val PRODUCT_SUB_YEARLY = "whaleradar_sub_yearly"
        const val PRODUCT_INAPP_LIFETIME = "whaleradar_lifetime"

        val ALL_SUB_IDS = listOf(PRODUCT_SUB_MONTHLY, PRODUCT_SUB_YEARLY)
        val ALL_INAPP_IDS = listOf(PRODUCT_INAPP_LIFETIME)

        fun mapPlanToProductId(planId: String): String = when (planId.lowercase()) {
            "monthly", PRODUCT_SUB_MONTHLY -> PRODUCT_SUB_MONTHLY
            "yearly", PRODUCT_SUB_YEARLY -> PRODUCT_SUB_YEARLY
            "lifetime", PRODUCT_INAPP_LIFETIME -> PRODUCT_INAPP_LIFETIME
            else -> PRODUCT_SUB_YEARLY
        }
    }

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _productDetailsMap = MutableStateFlow<Map<String, ProductDetails>>(emptyMap())
    val productDetailsMap: StateFlow<Map<String, ProductDetails>> = _productDetailsMap.asStateFlow()

    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build()
        )
        .build()

    fun startConnection(onConnected: (() -> Unit)? = null) {
        if (billingClient.isReady) {
            _isConnected.value = true
            queryProducts()
            queryPurchases()
            onConnected?.invoke()
            return
        }

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.i(TAG, "Google Play Billing setup successful")
                    _isConnected.value = true
                    queryProducts()
                    queryPurchases()
                    onConnected?.invoke()
                } else {
                    Log.w(TAG, "Billing setup failed: code ${billingResult.responseCode} - ${billingResult.debugMessage}")
                    _isConnected.value = false
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.w(TAG, "Google Play Billing service disconnected")
                _isConnected.value = false
            }
        })
    }

    fun queryProducts() {
        if (!billingClient.isReady) return

        // 1. Query Subscriptions
        val subProductList = ALL_SUB_IDS.map { id ->
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(id)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        }
        val subParams = QueryProductDetailsParams.newBuilder()
            .setProductList(subProductList)
            .build()

        billingClient.queryProductDetailsAsync(subParams) { result, detailsResult ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                val detailsList = detailsResult.productDetailsList
                val current = _productDetailsMap.value.toMutableMap()
                detailsList.forEach { details ->
                    current[details.productId] = details
                }
                _productDetailsMap.value = current
                Log.d(TAG, "Loaded subscription details: ${detailsList.map { it.productId }}")
            }
        }

        // 2. Query In-App One-Time (Lifetime)
        val inAppProductList = ALL_INAPP_IDS.map { id ->
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(id)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        }
        val inAppParams = QueryProductDetailsParams.newBuilder()
            .setProductList(inAppProductList)
            .build()

        billingClient.queryProductDetailsAsync(inAppParams) { result, detailsResult ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                val detailsList = detailsResult.productDetailsList
                val current = _productDetailsMap.value.toMutableMap()
                detailsList.forEach { details ->
                    current[details.productId] = details
                }
                _productDetailsMap.value = current
                Log.d(TAG, "Loaded in-app product details: ${detailsList.map { it.productId }}")
            }
        }
    }

    fun launchPurchaseFlow(
        activity: Activity,
        planId: String,
        onFailure: ((String) -> Unit)? = null
    ) {
        if (!billingClient.isReady) {
            startConnection {
                launchPurchaseFlow(activity, planId, onFailure)
            }
            return
        }

        val targetProductId = mapPlanToProductId(planId)
        val productDetails = _productDetailsMap.value[targetProductId]

        if (productDetails == null) {
            val msg = "Product details for $targetProductId not yet loaded from Google Play Store."
            Log.w(TAG, msg)
            onFailure?.invoke(msg)
            return
        }

        val productDetailsParamsBuilder = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(productDetails)

        if (productDetails.productType == BillingClient.ProductType.SUBS) {
            val offerToken = productDetails.subscriptionOfferDetails?.firstOrNull()?.offerToken
            if (offerToken != null) {
                productDetailsParamsBuilder.setOfferToken(offerToken)
            }
        }

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productDetailsParamsBuilder.build()))
            .build()

        val response = billingClient.launchBillingFlow(activity, billingFlowParams)
        if (response.responseCode != BillingClient.BillingResponseCode.OK) {
            val err = "Purchase flow launch failed (${response.responseCode}): ${response.debugMessage}"
            Log.e(TAG, err)
            onFailure?.invoke(err)
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.i(TAG, "Purchase cancelled by user")
        } else {
            Log.w(TAG, "Purchases update failed: code ${billingResult.responseCode} - ${billingResult.debugMessage}")
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            val isAcknowledged = purchase.isAcknowledged
            val productId = purchase.products.firstOrNull() ?: "pro"

            if (!isAcknowledged) {
                val ackParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                billingClient.acknowledgePurchase(ackParams) { result ->
                    if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                        Log.i(TAG, "Purchase acknowledged successfully: $productId")
                        onProStatusChanged(true, productId)
                    } else {
                        Log.e(TAG, "Failed to acknowledge purchase: ${result.debugMessage}")
                    }
                }
            } else {
                onProStatusChanged(true, productId)
            }
        }
    }

    fun queryPurchases(onComplete: ((Boolean) -> Unit)? = null) {
        if (!billingClient.isReady) {
            startConnection { queryPurchases(onComplete) }
            return
        }

        scope.launch {
            var hasActivePro = false
            var activePlanId: String? = null

            // 1. Check Subscriptions
            val subResult = withContext(Dispatchers.IO) {
                billingClient.queryPurchasesAsync(
                    QueryPurchasesParams.newBuilder()
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
                )
            }
            if (subResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                for (p in subResult.purchasesList) {
                    if (p.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        hasActivePro = true
                        activePlanId = p.products.firstOrNull()
                        if (!p.isAcknowledged) handlePurchase(p)
                    }
                }
            }

            // 2. Check In-App (Lifetime)
            val inAppResult = withContext(Dispatchers.IO) {
                billingClient.queryPurchasesAsync(
                    QueryPurchasesParams.newBuilder()
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                )
            }
            if (inAppResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                for (p in inAppResult.purchasesList) {
                    if (p.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        hasActivePro = true
                        activePlanId = p.products.firstOrNull()
                        if (!p.isAcknowledged) handlePurchase(p)
                    }
                }
            }

            if (hasActivePro) {
                onProStatusChanged(true, activePlanId)
            }
            onComplete?.invoke(hasActivePro)
        }
    }

    fun endConnection() {
        if (billingClient.isReady) {
            billingClient.endConnection()
        }
    }
}
