package com.galixo.autoClicker.core.common.base

import android.util.Log
import com.galixo.autoClicker.core.common.base.identifier.DATABASE_ID_INSERTION
import com.galixo.autoClicker.core.common.base.interfaces.EntityWithId
import com.galixo.autoClicker.core.common.base.interfaces.Identifiable
import org.jetbrains.annotations.VisibleForTesting

/**
 * Helper class to update a list in the database.
 *
 * When updating a complete list of entities in a database, you have the find which items will be added, updated or
 * removed. This class provides this information using the method [refreshUpdateValues] to parse the list, and the
 * [toBeAdded], [toBeUpdated] and [toBeRemoved] list to get the information.
 *
 * @param Item type of the items in the list to be updated.
 * @param Entity type of the entities in the database.
 */
class DatabaseListUpdater<Item : Identifiable, Entity : EntityWithId> {

    /** The list of items to be added. */

    private val toBeAdded = UpdateList<Item, Entity>()


    private val toBeUpdated = UpdateList<Item, Entity>()

    /** The list of items to be removed. */
    private val toBeRemoved = mutableListOf<Entity>()

    suspend fun refreshUpdateValues(currentEntities: Collection<Entity>, newItems: Collection<Item>, mappingClosure: suspend (Item) -> Entity) {
        // Clear previous use values and init entities to be removed with all current entities
        toBeAdded.clear()
        toBeUpdated.clear()
        toBeRemoved.apply {
            clear()
            addAll(currentEntities)
        }

        // New items with the default primary key should be added, others should be updated.
        // Updated items are removed from toBeRemoved list.
        newItems.forEach { newItem ->
            val newEntity = mappingClosure(newItem)
            if (newEntity.id == DATABASE_ID_INSERTION) toBeAdded.add(newItem, newEntity)
            else {
                val oldItemIndex = toBeRemoved.indexOfFirst { it.id == newItem.id.databaseId }
                if (oldItemIndex != -1) {
                    toBeUpdated.add(newItem, newEntity)
                    toBeRemoved.removeAt(oldItemIndex)
                }
            }
        }
        Log.i(
            TAG,
            "refreshUpdateValues: toBeAdded: ${toBeAdded.items}\n" + "toBeUpdated: ${toBeUpdated.items}\n" + "toBeRemoved: ${toBeRemoved}\n"
        )
    }

    suspend fun executeUpdate(
        addList: suspend (List<Entity>) -> List<Long>,
        updateList: suspend (List<Entity>) -> Unit,
        removeList: suspend (List<Entity>) -> Unit,
        onSuccess: (suspend (newIds: Map<Long, Long>, added: List<Item>, updated: List<Item>, removed: List<Entity>) -> Unit)? = null,
    ) {
        val newIdsMapping = mutableMapOf<Long, Long>()
        addList(toBeAdded.entities).forEachIndexed { index, dbId ->
            toBeAdded.items[index].let { item ->
                item.getDomainId()?.let { domainId -> newIdsMapping[domainId] = dbId }
            }
        }

        updateList(toBeUpdated.entities)
        removeList(toBeRemoved)

        Log.i(TAG, "executeUpdate: newIdsMapping: $newIdsMapping\ntoBeAdded: ${toBeAdded.items}\ntoBeUpdated: ${toBeUpdated.items}\ntoBeRemoved: ${toBeRemoved}\n ")

        onSuccess?.invoke(newIdsMapping, toBeAdded.items, toBeUpdated.items, toBeRemoved)
    }

    fun clear() {
        toBeAdded.clear()
        toBeUpdated.clear()
        toBeRemoved.clear()
    }

    override fun toString(): String =
        "DatabaseListUpdater[toBeAdded=${toBeAdded.size}; toBeUpdated=${toBeUpdated.size}; teBeRemoved=${toBeRemoved.size}]"
}

@VisibleForTesting
internal class UpdateList<Item, Entity> {

    private val _items = mutableListOf<Item>()
    val items: List<Item> = _items

    private val _entities = mutableListOf<Entity>()
    val entities: List<Entity> = _entities

    val size: Int get() = _items.size

    fun isEmpty(): Boolean = size == 0

    fun add(item: Item, entity: Entity) {
        _items.add(item)
        _entities.add(entity)
    }

    fun clear() {
        _items.clear()
        _entities.clear()
    }

    fun forEach(closure: (Item, Entity) -> Unit): Unit =
        items.forEachIndexed { index, item -> closure(item, entities[index]) }
}

private const val TAG = "DatabaseListUpdater"