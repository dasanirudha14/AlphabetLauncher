package com.anirudha.alphabetlauncher.data

import android.content.Intent
import androidx.compose.ui.graphics.ImageBitmap

/**
 * Data representation of an installed launchable application.
 *
 * @property name Clean display name of the application (e.g. "Chrome")
 * @property packageName Unique package name (e.g. "com.android.chrome")
 * @property iconBitmap Pre-converted ImageBitmap icon for high-performance Compose rendering
 * @property launchIntent Intent used to start the application
 */
data class AppInfo(
    val name: String,
    val packageName: String,
    val iconBitmap: ImageBitmap?,
    val launchIntent: Intent?
)
