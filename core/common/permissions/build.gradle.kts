plugins {
    alias(libs.plugins.galixo.androidLibrary)
    alias(libs.plugins.galixo.hilt)
}

android {
    namespace = "com.galixo.autoClicker.core.common.permissions"
    buildFeatures.viewBinding = true
}


dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.google.material)

    implementation(libs.airbnb.lottie)

    implementation(project(":core:common:base"))
    implementation(project(":core:common:overlays"))
    implementation(project(":core:common:ui"))
}