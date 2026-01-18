plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.davik.adbtools"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.davik.adbtools"
        minSdk = 24
        targetSdk = 36
        versionCode = 3
        versionName = "adb_tools_free_v2026001"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        // 【关键】开启 Compose
        compose = true
    }
    applicationVariants.all {
        outputs.all {
            val output = this as com.android.build.gradle.internal.api.ApkVariantOutputImpl
            val versionName = defaultConfig.versionName // 获取 adb_tools_free_v20261
            val fileName = "AdbTools_${versionName}.apk"
            output.outputFileName = fileName
        }
    }
    sourceSets {
        getByName("main") {
            java.srcDirs("libs")
            jniLibs.srcDirs("libs")
        }
    }
    buildTypes {
        release {
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
    kotlinOptions {
        jvmTarget = "11"
    }
    packaging {
        resources {
            // 遇到这个冲突文件时，只取第一个找到的，忽略后面的
            pickFirst("META-INF/versions/9/OSGI-INF/MANIFEST.MF")

            // 如果还有其他类似的 LICENSE 文件冲突，也可以在这里添加
            // pickFirst("META-INF/LICENSE")
        }
    }
}

dependencies {
//    implementation("com.flyfishxu:kadb:1.3.0")
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
    api("com.squareup.okio:okio:3.6.0")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(files("libs/kadb-release"))
    implementation(files("libs/spake2-java-1.0.5"))
//    implementation(files("libs/dadb.jar"))
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
//    implementation("dev.mobile:dadb:1.2.10")
//-----------compose-------------------------------------------
    // 1. 引入 Compose BOM (Bill of Materials) - 推荐做法
    // 这行决定了下面所有 Compose 库的版本，下面不需要再写版本号
    val composeBom = platform("androidx.compose:compose-bom:2024.02.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // 2. 基础 UI 库
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.navigation:navigation-compose:2.7.1")
    // 3. Android Studio 预览支持
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation(libs.spake2)
    // 4. Material Design 3 (如果不使用 MD3，可以用 material)
    implementation("androidx.compose.material3:material3")

    // 5. 与 Activity 集成
    implementation("androidx.activity:activity-compose:1.8.2") // 这个通常需要独立版本号

    // 6. Debug 工具 (必须用 debugImplementation)
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
//-----------compose-------------------------------------------


    // 用于生成 X509Certificate 的库。你也可以使用 BouncyCastle 来实现此功能。
    implementation ("com.github.MuntashirAkon:sun-security-android:1.1")

// 如果你想使用 Android 默认的 conscrypt，需要绕过隐藏 API。这还需要额外的步骤。
// 访问 https://github.com/LSPosed/AndroidHiddenApiBypass 了解更多相关信息。
// 如果你不需要此功能，请注释掉下面这一行。
    implementation ("org.lsposed.hiddenapibypass:hiddenapibypass:6.1")
    implementation ("com.github.MuntashirAkon.spake2-java:spake2-android:2.2.1")
    implementation ("org.bouncycastle:bcprov-jdk18on:1.79")
    implementation("org.bouncycastle:bcpkix-jdk18on:1.79")
//    api(project(":dadbTools"))
    implementation ("cafe.cryptography:ed25519-elisabeth:0.1.0")
    implementation ("cafe.cryptography:curve25519-elisabeth:0.1.0")
}