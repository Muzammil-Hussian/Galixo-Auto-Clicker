package com.galixo.ai.gradle.convention.utils

import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.getByType

internal fun Project.getLibs(): VersionCatalogWrapper =
    VersionCatalogWrapper(extensions.getByType<VersionCatalogsExtension>().named("libs"))

internal class VersionCatalogWrapper(private val libs: VersionCatalog) {

    val plugins = Plugins()
    val versions = Versions()

    internal inner class Plugins {
        val androidApplication: String
            get() = libs.getPluginId("androidApplication")
        val androidLibrary: String
            get() = libs.getPluginId("androidLibrary")
        val androidxRoom: String
            get() = libs.getPluginId("androidxRoom")
        val jetbrainsKotlinAndroid: String
            get() = libs.getPluginId("jetbrainsKotlinAndroid")
        val jetbrainsKotlinSerialization: String
            get() = libs.getPluginId("jetbrainsKotlinSerialization")
        val googleKsp: String
            get() = libs.getPluginId("googleKsp")
        val googleDaggerHiltAndroid: String
            get() = libs.getPluginId("googleDaggerHiltAndroid")
        val googleCrashlytics: String
            get() = libs.getPluginId("googleCrashlytics")
        val googleGms: String
            get() = libs.getPluginId("googleGms")

        private fun VersionCatalog.getPluginId(alias: String): String =
            findPlugin(alias).get().get().pluginId
    }

    internal inner class Versions {
        val androidCompileSdk: Int
            get() = libs.getVersion("androidCompileSdk")
        val androidMinSdk: Int
            get() = libs.getVersion("androidMinSdk")

        private fun VersionCatalog.getVersion(alias: String): Int =
            findVersion(alias).get().requiredVersion.toInt()
    }

    fun getLibrary(alias: String): Provider<MinimalExternalModuleDependency> =
        libs.findLibrary(alias).get()
}