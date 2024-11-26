package com.galixo.autClicker.feature.backup.domain

import android.content.Context
import android.graphics.Point
import android.net.Uri
import com.galixo.autClicker.feature.backup.data.BackupEngine
import com.galixo.autClicker.feature.backup.data.BackupProgress
import com.galixo.autoClicker.core.scenarios.data.database.AutoClickerDb
import com.galixo.autoClicker.core.scenarios.domain.IMainRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupRepository @Inject constructor(
    @ApplicationContext context: Context,
    private val autoClickerDb: AutoClickerDb,
    private val mainRepository: IMainRepository,
) {

    private val backupEngine: BackupEngine = BackupEngine(
        appDataDir = context.filesDir,
        contentResolver = context.contentResolver,
    )

    /**
     * Create a backup of the provided scenario into the provided file.
     *
     * @param zipFileUri the uri of the file to write the backup into. Must be retrieved using the DocumentProvider.
     * @param scenarios the scenarios to backup.
     * @param screenSize the size of this device screen.
     *
     * @return a flow on the backup creation progress.
     */
    fun createScenarioBackup(
        zipFileUri: Uri,
        scenarios: List<Long>,
        screenSize: Point,
    ) = channelFlow {
        launch {
            backupEngine.createBackup(
                zipFileUri = zipFileUri,
                scenarios = scenarios.mapNotNull {
                    autoClickerDb.scenarioDao().getScenariosWithAction(it)
                },
                screenSize = screenSize,
                progress = BackupProgress(
                    onError = { send(Backup.Error) },
                    onProgressChanged = { current -> send(Backup.Loading(current)) },
                    onCompleted = { scenarios, failureCount, compatWarning ->
                        send(
                            Backup.Completed(
                                successCount = scenarios.size,
                                failureCount = failureCount,
                                compatWarning = compatWarning,
                            )
                        )
                    }
                )
            )
        }
    }

    /**
     * Restore a backup of scenarios from the provided file.
     *
     * @param zipFileUri the uri of the file to read the backup from. Must be retrieved using the DocumentProvider.
     * @param screenSize the size of this device screen.
     *
     * @return a flow on the backup import progress.
     */
    fun restoreScenarioBackup(zipFileUri: Uri, screenSize: Point) = channelFlow {
        launch {
            backupEngine.loadBackup(
                zipFileUri,
                screenSize,
                BackupProgress(
                    onError = { send(Backup.Error) },
                    onProgressChanged = { current -> send(Backup.Loading(current)) },
                    onVerification = { send(Backup.Verification) },
                    onCompleted = { scenarios, failureCount, compatWarning ->
                        var totalFailures = failureCount

                        val scenariosSuccess = scenarios.toMutableList()
                        scenarios.forEach { completeScenario ->
                            if (mainRepository.addScenarioCopy(completeScenario) == null) {
                                scenariosSuccess.remove(completeScenario)
                                totalFailures++
                            }
                        }

                        send(
                            Backup.Completed(
                                successCount = scenariosSuccess.size,
                                failureCount = totalFailures,
                                compatWarning = compatWarning,
                            )
                        )
                    }
                )
            )
        }
    }
}