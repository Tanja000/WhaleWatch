import java.util.Properties

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.whaleradar"
    compileSdk = 36
    defaultConfig {
        applicationId = "com.whaleradar"
        minSdk = 24
        targetSdk = 36
        versionCode = 6
        versionName = "1.3.0"
        val isOpen = (project.findProperty("openBuild")?.toString() ?: "false").toBoolean()
        buildConfigField("boolean", "IS_OPEN_TEST_BUILD", isOpen.toString())
    }

    signingConfigs {
        create("release") {
            // Secrets live in android-app/keystore.properties (not committed to git)
            val props = Properties()
            val propsFile = rootProject.file("keystore.properties")
            if (propsFile.exists()) propsFile.inputStream().use { props.load(it) }
            storeFile = file(props.getProperty("storeFile", "whaleradar-release.jks"))
            storePassword = props.getProperty("storePassword")
            keyAlias = props.getProperty("keyAlias", "whaleradar")
            keyPassword = props.getProperty("keyPassword")
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            ndk {
                debugSymbolLevel = "FULL"
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
      compose = true
      aidl = false
      buildConfig = true
      shaders = false
    }

    packaging {
      resources {
        excludes += "/META-INF/{AL2.0,LGPL2.1}"
      }
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
  val composeBom = platform(libs.androidx.compose.bom)
  implementation(composeBom)
  androidTestImplementation(composeBom)

  // Official Google Play Billing for in-app purchases & subscriptions
  implementation("com.android.billingclient:billing-ktx:8.0.0")

  // PDF text extraction for official U.S. House Clerk Periodic Transaction Reports
  implementation("com.tom-roush:pdfbox-android:2.0.27.0")

  // Core Android dependencies
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)

  // Arch Components
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.viewmodel.compose)

  // Compose
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.compose.material3)
  implementation("androidx.compose.material:material-icons-core")
  implementation("androidx.compose.material:material-icons-extended")
  // Tooling
  debugImplementation(libs.androidx.compose.ui.tooling)
  // Instrumented tests
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  debugImplementation(libs.androidx.compose.ui.test.manifest)

  // Local tests: jUnit, coroutines, Android runner
  testImplementation(libs.junit)
  testImplementation("org.apache.pdfbox:pdfbox:2.0.30")
  testImplementation("org.json:json:20240303")
  testImplementation(libs.kotlinx.coroutines.test)

  // Instrumented tests: jUnit rules and runners
  androidTestImplementation(libs.androidx.test.core)
  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.androidx.test.runner)
  androidTestImplementation(libs.androidx.test.espresso.core)
}
