plugins {
    alias(libs.plugins.galixo.androidApplication)
    alias(libs.plugins.galixo.hilt)
}

android {
    namespace = "com.galixo.autoClicker"
    buildFeatures.viewBinding = true

    defaultConfig {
        applicationId = "com.galixo.autoClicker"

        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {

    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.recyclerView)
    implementation(libs.androidx.fragment.ktx)

    implementation(libs.androidx.lifecycle.extensions)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.common.java8)

    implementation(libs.airbnb.lottie)
    implementation(libs.google.material)

    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    //Timber for logging info
    implementation(libs.timber)


    // Library modules
    implementation(project(":core:common:base"))
    implementation(project(":core:common:display"))
    implementation(project(":core:common:overlays"))
    implementation(project(":core:common:ui"))

    implementation(project(":core:scenarios"))

    implementation(project(":feature:backup"))
    implementation(project(":feature:notifications"))
    implementation(project(":core:common:permissions"))
    implementation(project(":feature:config"))
}