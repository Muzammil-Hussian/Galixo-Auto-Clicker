package com.galixo.ai.gradle.parameters

import org.gradle.api.Project
import org.gradle.kotlin.dsl.support.uppercaseFirstChar


internal fun Project.isBuildForVariant(variantName: String): Boolean {
    val normalizedName = variantName.uppercaseFirstChar()

    return project.gradle.startParameter.taskRequests.find { taskExecRequest ->
        taskExecRequest.args.find { taskName -> taskName.contains(normalizedName) } != null
    } != null
}