plugins {
    alias(libs.plugins.galixo.androidLibrary)
    alias(libs.plugins.galixo.hilt)
}

android {
    namespace = "com.galixo.autoClicker.feature.notifications"
    buildFeatures.viewBinding = true
}

dependencies {
    implementation(libs.androidx.appcompat)

    implementation(project(":core:common:base"))
    implementation(project(":core:common:permissions"))
    implementation(project(":core:common:ui"))
}