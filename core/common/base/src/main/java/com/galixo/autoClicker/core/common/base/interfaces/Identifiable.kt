package com.galixo.autoClicker.core.common.base.interfaces

import com.galixo.autoClicker.core.common.base.identifier.Identifier

interface Identifiable {
    val id: Identifier

    fun getDatabaseId(): Long = id.databaseId
    fun getDomainId(): Long? = id.tempId

    fun getValidId(): Long = if (isInDatabase()) id.databaseId
    else id.tempId ?: throw IllegalStateException("Identifier is invalid")

    fun isInDatabase(): Boolean = id.isInDatabase()
}

fun List<Identifiable>.containsIdentifiable(id: Identifier): Boolean =
    find { it.id == id } != null