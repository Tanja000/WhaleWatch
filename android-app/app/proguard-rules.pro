# ProGuard / R8 configuration for WhaleRadar

# Keep data models used with JSON / serialization / reflection
-keep class com.whaleradar.data.model.** { *; }

# Keep WhaleRadar repository & billing
-keep class com.whaleradar.data.billing.** { *; }

# Keep PDFBox for House Clerk PTR PDFs
-keep class com.tom_roush.pdfbox.** { *; }
-dontwarn com.tom_roush.pdfbox.**

# Keep Google Play Billing
-keep class com.android.billingclient.api.** { *; }
-dontwarn com.android.billingclient.api.**

# Keep Kotlin Coroutines & Flow
-keepclassmembers class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

# Compose runtime rules are bundled automatically with compose-compiler,
# but ensure ViewModel reflection / factories remain intact:
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
-keepclassmembers class * extends androidx.lifecycle.AndroidViewModel {
    <init>(...);
}
