package com.galixo.autoClicker.core.common.base.extensions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


/** Returns a flow containing the results of applying the given transform function to each value of the original flow. */
inline fun <T, R> Flow<List<T>>.mapList(crossinline transform: suspend (value: T) -> R): Flow<List<R>> =
    map { list ->
        list.map { mapValue ->
            transform(mapValue)
        }
    }

/** Returns a flow containing the results of applying the given transform function to each value of the original flow. */
inline fun <T, R> Flow<List<T>>.mapListIndexed(crossinline transform: suspend (index: Int, value: T) -> R): Flow<List<R>> =
    map { list ->
        list.mapIndexed { index, mapValue ->
            transform(index, mapValue)
        }
    }