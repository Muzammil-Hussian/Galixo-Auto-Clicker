package com.galixo.autClicker.feature.backup.data.source

import android.graphics.Point
import android.util.Log
import com.galixo.autClicker.feature.backup.data.base.SCENARIO_BACKUP_EXTENSION
import com.galixo.autClicker.feature.backup.data.base.ScenarioBackupDataSource
import com.galixo.autClicker.feature.backup.data.base.ScenarioBackupSerializer
import com.galixo.autClicker.feature.backup.data.scenario.ScenarioBackup
import com.galixo.autClicker.feature.backup.data.scenario.ScenarioSerializer
import com.galixo.autoClicker.core.scenarios.data.database.DATABASE_VERSION
import com.galixo.autoClicker.core.scenarios.data.database.ScenarioWithActions
import java.io.File

internal class BackupDataSource(
    appDataDir: File,
) : ScenarioBackupDataSource<ScenarioBackup, ScenarioWithActions>(appDataDir) {

    /**
     * Regex matching a condition file into its folder in a backup archive.
     * Will match any file like "scenarioId/Condition_randomNumber".
     *
     * You can try it out here: https://regex101.com
     */
    private val scenarioUnzipMatchRegex = """scenario-[0-9]+/[0-9]+$SCENARIO_BACKUP_EXTENSION"""
        .toRegex()

    override val serializer: ScenarioBackupSerializer<ScenarioBackup> = ScenarioSerializer()

    override fun isScenarioBackupFileZipEntry(fileName: String): Boolean =
        fileName.matches(scenarioUnzipMatchRegex)

    override fun isScenarioBackupAdditionalFileZipEntry(fileName: String): Boolean =
        false

    override fun getBackupZipFolderName(scenario: ScenarioWithActions): String =
        "scenario-${scenario.scenario.id}"

    override fun getBackupFileName(scenario: ScenarioWithActions): String =
        "${scenario.scenario.id}$SCENARIO_BACKUP_EXTENSION"

    override fun createBackupFromScenario(
        scenario: ScenarioWithActions,
        screenSize: Point
    ): ScenarioBackup =
        ScenarioBackup(
            scenario = scenario,
            screenWidth = screenSize.x,
            screenHeight = screenSize.y,
            version = DATABASE_VERSION,
        )

    override fun verifyExtractedBackup(
        backup: ScenarioBackup,
        screenSize: Point
    ): ScenarioWithActions? {
        if (backup.scenario.actions.isEmpty()) {
            Log.w(TAG, "Invalid scenario, action list is empty.")
            return null
        }

        return backup.scenario
    }

    override fun getBackupAdditionalFilesPaths(scenario: ScenarioWithActions): Set<String> =
        emptySet()
}

/** Tag for logs. */
private const val TAG = "BackupEngine"