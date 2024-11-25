plugins {
    alias(libs.plugins.galixo.androidLibrary)
    alias(libs.plugins.galixo.hilt)
}

android {
    namespace = "com.galixo.autoClicker.core.common.display"
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.androidx.annotation)
    implementation(libs.androidx.core.ktx)

    implementation(project(":core:common:base"))
}