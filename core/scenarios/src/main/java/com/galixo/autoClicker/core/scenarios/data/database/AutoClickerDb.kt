package com.galixo.autoClicker.core.scenarios.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import javax.inject.Singleton

@Database(
    entities = [
        ScenarioEntity::class,
        ActionEntity::class,
    ],
    version = DATABASE_VERSION,
    exportSchema = true,
)
@TypeConverters(
    ActionTypeStringConverter::class,
)
@Singleton
abstract class AutoClickerDb : RoomDatabase() {

    /** The data access object for the dumb scenario in the database. */
    abstract fun scenarioDao(): ScenarioDao
}

/** Current version of the database. */
const val DATABASE_VERSION = 1