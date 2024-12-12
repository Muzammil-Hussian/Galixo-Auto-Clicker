plugins {
    alias(libs.plugins.galixo.androidLibrary)
    alias(libs.plugins.galixo.hilt)
}

android {
    namespace = "com.galixo.autoClicker.core.common.theme"
    buildFeatures.viewBinding = true
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.datastore)

    implementation(libs.androidx.fragment.ktx)

    implementation(libs.google.material)
}