package com.galixo.ai.gradle.convention

import com.galixo.ai.gradle.convention.utils.androidApp
import com.galixo.ai.gradle.convention.utils.getLibs
import com.galixo.ai.gradle.convention.utils.plugins
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidApplicationConventionPlugin : Plugin<Project> {

    override fun apply(target: Project): Unit = with(target) {
        val libs = getLibs()

        plugins {
            apply(libs.plugins.androidApplication)
            apply(libs.plugins.jetbrainsKotlinAndroid)
        }

        androidApp {
            compileSdk = libs.versions.androidCompileSdk

            defaultConfig.apply {
                targetSdk = libs.versions.androidCompileSdk
                minSdk = libs.versions.androidMinSdk
            }

            compileOptions.apply {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
            }

            buildTypes {
                release {
                    isMinifyEnabled = true
                    isShrinkResources = true
                    proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
                }
            }
        }
    }
}
