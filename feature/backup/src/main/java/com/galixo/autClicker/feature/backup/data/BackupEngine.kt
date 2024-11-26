package com.galixo.autClicker.feature.backup.data

import android.content.ContentResolver
import android.graphics.Point
import android.net.Uri
import android.util.Log
import com.galixo.autClicker.feature.backup.data.source.BackupDataSource
import com.galixo.autoClicker.core.scenarios.data.database.ScenarioWithActions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/** [BackupEngine] internal implementation. */
internal class BackupEngine(appDataDir: File, private val contentResolver: ContentResolver) {

    private val scenarioBackupDataSource: BackupDataSource = BackupDataSource(appDataDir)

    /**
     * Creates a new backup file.
     *
     * @param zipFileUri the uri of the file to write the backup into. Must be retrieved using the DocumentProvider.
     * @param screenSize the size of this device screen.
     * @param progress the object notified about the backup progress.
     */
    suspend fun createBackup(
        zipFileUri: Uri,
        scenarios: List<ScenarioWithActions>,
        screenSize: Point,
        progress: BackupProgress,
    ) {
        scenarioBackupDataSource.reset()

        var currentProgress = 0
        progress.onProgressChanged(currentProgress)

        // Create the zip file containing the scenarios and their events conditions.
        withContext(Dispatchers.IO) {
            try {
                ZipOutputStream(contentResolver.openOutputStream(zipFileUri)).use { zipStream ->
                    scenarios.forEach { scenario ->
                        Log.d(TAG, "Backup scenario ${scenario.scenario.id}")

                        scenarioBackupDataSource.addScenarioToZipFile(
                            zipStream,
                            scenario,
                            screenSize
                        )

                        currentProgress++
                        progress.onProgressChanged(currentProgress)
                    }



                    progress.onCompleted(scenarios, 0, false)
                }
            } catch (ioEx: IOException) {
                Log.e(TAG, "Error while creating backup archive.")
                progress.onError()
            } catch (isEx: IllegalStateException) {
                Log.e(TAG, "Error while creating backup archive, target folder can't be written")
                progress.onError()
            } catch (secEx: SecurityException) {
                Log.e(TAG, "Error while creating backup archive, permission is denied")
                progress.onError()
            }
        }
    }

    /**
     * Loads a backup file.
     *
     * @param zipFileUri the uri of the file to load the backup from. Must be retrieved using the DocumentProvider.
     * @param screenSize the size of this device screen.
     * @param progress the object notified about the backup import progress.
     */
    suspend fun loadBackup(zipFileUri: Uri, screenSize: Point, progress: BackupProgress) {
        Log.d(TAG, "Load backup: $zipFileUri")
        scenarioBackupDataSource.reset()

        var currentProgress = 0
        progress.onProgressChanged(currentProgress)

        withContext(Dispatchers.IO) {
            try {
                ZipInputStream(contentResolver.openInputStream(zipFileUri)).use { zipStream ->
                    generateSequence { zipStream.nextEntry }
                        .forEach { zipEntry ->
                            if (zipEntry.isDirectory) return@forEach

                            Log.d(TAG, "Extracting file ${zipEntry.name}")
                            when {
                                scenarioBackupDataSource.extractFromZip(zipStream, zipEntry.name) -> {
                                    Log.d(TAG, "Scenario file ${zipEntry.name} extracted.")

                                    currentProgress++
                                    progress.onProgressChanged(currentProgress)
                                }


                                else -> Log.i(
                                    TAG,
                                    "Nothing found to handle zip entry ${zipEntry.name}."
                                )
                            }
                        }
                }

                progress.onVerification?.invoke()
                scenarioBackupDataSource.verifyExtractedScenarios(screenSize)

                progress.onCompleted(
                    scenarioBackupDataSource.validBackups,
                    scenarioBackupDataSource.failureCount,
                    false
                )
            } catch (ioEx: IOException) {
                Log.e(TAG, "Error while reading backup archive.")
                progress.onError()
            } catch (secEx: SecurityException) {
                Log.e(TAG, "Error while reading backup archive, permission is denied")
                progress.onError()
            } catch (iaEx: IllegalArgumentException) {
                Log.e(TAG, "Error while reading backup archive, file is invalid")
                progress.onError()
            } catch (npEx: NullPointerException) {
                Log.e(TAG, "Error while reading backup archive, file path is null")
                progress.onError()
            }
        }
    }
}

/** Tag for logs. */
private const val TAG = "BackupEngine"