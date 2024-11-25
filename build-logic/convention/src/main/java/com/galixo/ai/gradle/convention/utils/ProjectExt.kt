package com.galixo.ai.gradle.convention.utils

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.Project
import org.gradle.api.plugins.PluginManager
import org.gradle.kotlin.dsl.configure

internal inline fun Project.plugins(closure: PluginManager.() -> Unit) =
    closure(pluginManager)

internal inline fun Project.androidApp(crossinline closure: BaseAppModuleExtension.() -> Unit) =
    extensions.configure<BaseAppModuleExtension> { closure() }

internal inline fun Project.androidLib(crossinline closure: LibraryExtension.() -> Unit) =
    extensions.configure<LibraryExtension> { closure() }

internal inline fun Project.android(crossinline closure: BaseExtension.() -> Unit) =
    extensions.configure<BaseExtension> { closure() }