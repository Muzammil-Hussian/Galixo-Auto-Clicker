package com.galixo.ai.gradle.convention

import com.galixo.ai.gradle.convention.utils.android
import com.galixo.ai.gradle.convention.utils.getLibs
import com.galixo.ai.gradle.convention.utils.playStoreImplementation
import com.galixo.ai.gradle.convention.utils.plugins
import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class CrashlyticsConventionPlugin : Plugin<Project> {

    override fun apply(target: Project): Unit = with(target) {
        val libs = getLibs()

        plugins {
            apply(libs.plugins.googleCrashlytics)
            apply(libs.plugins.googleGms)
        }

        android {
            buildTypes {
                getByName("release") {
                    configure<CrashlyticsExtension> {
                        nativeSymbolUploadEnabled = true
                    }
                }
            }
        }

        dependencies {
            playStoreImplementation(platform(libs.getLibrary("google.firebase.bom")))
            playStoreImplementation(libs.getLibrary("google.firebase.crashlytics.ktx"))
            playStoreImplementation(libs.getLibrary("google.firebase.crashlytics.ndk"))
        }
    }
}