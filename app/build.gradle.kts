plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.meteoercilla"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.meteoercilla"
        minSdk = 33
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.play.services.location)
    testImplementation(libs.junit)
    implementation(libs.annotation)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    androidTestImplementation(libs.ext.junit)
    implementation(libs.postgresql)
    implementation(libs.jcraft)
    implementation(libs.lottie)
    androidTestImplementation(libs.espresso.core)
}