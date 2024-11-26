package com.galixo.autClicker.feature.backup.data

import com.galixo.autoClicker.core.scenarios.data.database.ScenarioWithActions

/**
 * Notifies for a backup progress and state.
 *
 * @param onProgressChanged called for each scenario processed.
 * @param onCompleted called when the backup is completed.
 * @param onError called when the backup has encountered an error.
 * @param onVerification called when the backup is verifying the imported data.
 */
class BackupProgress(
    val onProgressChanged: suspend (current: Int?) -> Unit,
    val onCompleted: suspend (
        scenario: List<ScenarioWithActions>,
        failureCount: Int,
        compatWarning: Boolean,
    ) -> Unit,
    val onError: suspend () -> Unit,
    val onVerification: (suspend () -> Unit)? = null,
)