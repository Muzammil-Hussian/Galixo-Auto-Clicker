package com.galixo.ai.gradle.convention.utils

import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.DependencyHandlerScope

internal fun DependencyHandlerScope.implementation(dependency: Provider<MinimalExternalModuleDependency>) =
    add("implementation", dependency)

internal fun DependencyHandlerScope.ksp(dependency: Provider<MinimalExternalModuleDependency>) =
    add("ksp", dependency)


internal fun DependencyHandlerScope.kspTest(dependency: Provider<MinimalExternalModuleDependency>) =
    add("kspTest", dependency)

internal fun DependencyHandlerScope.testImplementation(dependency: Provider<MinimalExternalModuleDependency>) =
    add("testImplementation", dependency)