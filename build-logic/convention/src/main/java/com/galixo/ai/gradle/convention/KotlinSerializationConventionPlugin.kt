package com.galixo.ai.gradle.convention

import com.galixo.ai.gradle.convention.utils.getLibs
import com.galixo.ai.gradle.convention.utils.implementation
import com.galixo.ai.gradle.convention.utils.plugins
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies


class KotlinSerializationConventionPlugin : Plugin<Project> {

    override fun apply(target: Project): Unit = with(target) {
        val libs = getLibs()

        plugins {
            apply(libs.plugins.jetbrainsKotlinSerialization)
        }

        dependencies {
            implementation(libs.getLibrary("kotlinx.serialization.json"))
        }
    }
}