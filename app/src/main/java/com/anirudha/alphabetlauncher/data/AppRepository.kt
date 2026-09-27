package com.anirudha.alphabetlauncher.data

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository responsible for retrieving installed launchable applications from Android PackageManager.
 */
class AppRepository(private val context: Context) {

    /**
     * Retrieves all launchable applications installed on the device sorted alphabetically by name.
     * Executes asynchronously on Dispatchers.IO to prevent main thread blocking.
     */
    suspend fun getLaunchableApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        val packageManager = context.packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolveInfos: List<ResolveInfo> = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.queryIntentActivities(
                    mainIntent,
                    PackageManager.ResolveInfoFlags.of(0)
                )
            } else {
                @Suppress("DEPRECATION")
                packageManager.queryIntentActivities(mainIntent, 0)
            }
        } catch (e: Exception) {
            emptyList()
        }

        val currentPackageName = context.packageName

        val apps = mutableListOf<AppInfo>()

        for (resolveInfo in resolveInfos) {
            try {
                val packageName = resolveInfo.activityInfo.packageName

                // Skip our own launcher app from app list if desired (or keep it)
                if (packageName == currentPackageName) {
                    continue
                }

                val appName = resolveInfo.loadLabel(packageManager).toString().trim()
                if (appName.isEmpty()) continue

                val iconDrawable = resolveInfo.loadIcon(packageManager)
                val iconBitmap = iconDrawable?.toImageBitmap()

                val launchIntent = packageManager.getLaunchIntentForPackage(packageName)?.apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }

                if (launchIntent != null) {
                    apps.add(
                        AppInfo(
                            name = appName,
                            packageName = packageName,
                            iconBitmap = iconBitmap,
                            launchIntent = launchIntent
                        )
                    )
                }
            } catch (e: Exception) {
                // Ignore corrupt individual app entries safely
            }
        }

        // Sort apps alphabetically by name (case-insensitive)
        apps.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name })
    }

    private fun Drawable.toImageBitmap(): ImageBitmap? {
        return try {
            if (this is BitmapDrawable && bitmap != null) {
                bitmap.asImageBitmap()
            } else {
                val width = if (intrinsicWidth > 0) intrinsicWidth else 128
                val height = if (intrinsicHeight > 0) intrinsicHeight else 128
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                setBounds(0, 0, canvas.width, canvas.height)
                draw(canvas)
                bitmap.asImageBitmap()
            }
        } catch (e: Exception) {
            null
        }
    }
}
