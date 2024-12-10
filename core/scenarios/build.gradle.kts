plugins {
    alias(libs.plugins.galixo.androidLibrary)
    alias(libs.plugins.galixo.androidRoom)
    alias(libs.plugins.galixo.kotlinSerialization)
    alias(libs.plugins.galixo.hilt)
}

android {
    namespace = "com.galixo.autoClicker.core.scenarios"

    sourceSets {
        getByName("test") {
            // Adds exported schema location as test app assets.
            assets.srcDirs("$projectDir/schemas")
        }
    }
}

dependencies {
    implementation(project(":core:common:base"))
    implementation(project(":core:common:ui"))
}