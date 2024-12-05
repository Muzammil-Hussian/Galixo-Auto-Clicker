import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

group = "com.galixo.ai.gradle.buildlogic.convention"

// Configure the build-logic plugins to target JDK 17
// This matches the JDK used to build the project, and is not related to what is running on device.
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.androidx.room.gradlePlugin)
    compileOnly(libs.google.firebase.crashlytics.gradlePlugin)
    compileOnly(libs.google.gms.gradlePlugin)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "com.galixo.ai.gradle.android.application"
            implementationClass = "com.galixo.ai.gradle.convention.AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "com.galixo.ai.gradle.android.library"
            implementationClass = "com.galixo.ai.gradle.convention.AndroidLibraryConventionPlugin"
        }

        register("androidRoom") {
            id = "com.galixo.ai.gradle.android.room"
            implementationClass = "com.galixo.ai.gradle.convention.AndroidRoomConventionPlugin"
        }

        register("androidSigning") {
            id = "com.galixo.ai.gradle.android.signing"
            implementationClass = "com.galixo.ai.gradle.convention.AndroidSigningConvention"
        }

        register("crashlytics") {
            id = "com.galixo.ai.gradle.crashlytics"
            implementationClass = "com.galixo.ai.gradle.convention.CrashlyticsConventionPlugin"
        }

        register("androidHilt") {
            id = "com.galixo.ai.gradle.android.hilt"
            implementationClass = "com.galixo.ai.gradle.convention.HiltConventionPlugin"
        }

        register("kotlinSerialization") {
            id = "com.galixo.ai.gradle.android.kotlin.serialization"
            implementationClass =
                "com.galixo.ai.gradle.convention.KotlinSerializationConventionPlugin"
        }
    }
}
