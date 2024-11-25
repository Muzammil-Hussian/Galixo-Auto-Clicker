package com.galixo.ai.gradle.convention

import androidx.room.gradle.RoomExtension
import com.galixo.ai.gradle.convention.utils.getLibs
import com.galixo.ai.gradle.convention.utils.implementation
import com.galixo.ai.gradle.convention.utils.ksp
import com.galixo.ai.gradle.convention.utils.plugins
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidRoomConventionPlugin : Plugin<Project> {

    override fun apply(target: Project): Unit = with(target) {
        val libs = getLibs()

        plugins {
            apply(libs.plugins.androidxRoom)
            apply(libs.plugins.googleKsp)
        }

        extensions.configure<RoomExtension> {
            schemaDirectory("$projectDir/schemas")
        }

        dependencies {
            implementation(libs.getLibrary("androidx.room.runtime"))
            implementation(libs.getLibrary("androidx.room.ktx"))
            ksp(libs.getLibrary("androidx.room.compiler"))
        }
    }
}