import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services) apply false
}

if (file("google-services.json").exists()) {
    apply(plugin = "com.google.gms.google-services")
}

// local.properties(버전관리 제외)에서 백엔드 주소를 읽는다. 없으면 기본값 사용.
val localProperties = Properties().apply {
    rootProject.file("local.properties").takeIf { it.exists() }
        ?.inputStream()?.use { load(it) }
}
val baseUrl: String = localProperties.getProperty("base.url") ?: "http://10.0.2.2:8080/"

// 릴리스 서명 정보: local.properties 우선, 없으면 환경변수(CI 시크릿). 키스토어 자체는 커밋하지 않는다.
fun signingValue(key: String, env: String): String? =
    localProperties.getProperty(key) ?: System.getenv(env)

val keystorePath: String? = signingValue("keystore.path", "KEYSTORE_PATH")

android {
    namespace = "com.example.ptmanageremployee"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.ptmanageremployee"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
    }

    buildFeatures {
        buildConfig = true
    }

    signingConfigs {
        // 키스토어가 없으면 서명 설정 없이 빌드한다(디버그·CI 유닛테스트에는 영향 없음).
        if (keystorePath != null) {
            create("release") {
                storeFile = file(keystorePath)
                storePassword = signingValue("keystore.password", "KEYSTORE_PASSWORD")
                keyAlias = signingValue("key.alias", "KEY_ALIAS")
                keyPassword = signingValue("key.password", "KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.findByName("release")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging)
    implementation(libs.zxing.android.embedded)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
    implementation(libs.androidx.security.crypto)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}
