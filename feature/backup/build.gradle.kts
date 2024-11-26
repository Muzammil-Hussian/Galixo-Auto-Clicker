plugins {
    alias(libs.plugins.galixo.androidLibrary)
    alias(libs.plugins.galixo.kotlinSerialization)
    alias(libs.plugins.galixo.hilt)
}

android {
    namespace = "com.galixo.autClicker.feature.backup"
    buildFeatures.viewBinding = true
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.androidx.annotation)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)

    implementation(libs.google.material)

    implementation(project(":core:common:base"))
    implementation(project(":core:common:display"))
    implementation(project(":core:common:ui"))

    implementation(project(":core:scenarios"))

}