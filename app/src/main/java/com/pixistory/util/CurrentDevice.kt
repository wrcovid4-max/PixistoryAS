package com.pixistory.util

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import com.pixistory.data.model.DeviceEntity
import com.pixistory.data.model.DeviceStatus
import com.pixistory.data.model.DeviceType
import java.util.UUID

/** Builds a ready-to-save entry from the phone the app is running on. */
object CurrentDevice {
    fun snapshot(context: Context, currency: String): DeviceEntity {
        val now = System.currentTimeMillis()
        val brand = Build.MANUFACTURER.replaceFirstChar { it.uppercase() }
        val model = Build.MODEL
        val name = if (model.startsWith(brand, ignoreCase = true)) model else "$brand $model"
        val pm = context.packageManager
        val isFoldable = pm.hasSystemFeature("android.hardware.sensor.hinge_angle")
        val isTablet = context.resources.configuration.smallestScreenWidthDp >= 600 && !isFoldable
        val type = when {
            isFoldable -> DeviceType.FOLDABLE
            isTablet -> DeviceType.TABLET
            pm.hasSystemFeature(PackageManager.FEATURE_WATCH) -> DeviceType.WATCH
            else -> DeviceType.PHONE
        }
        val patch = if (Build.VERSION.SDK_INT >= 23) Build.VERSION.SECURITY_PATCH else ""
        return DeviceEntity(
            id = UUID.randomUUID().toString(),
            name = name,
            brand = brand,
            model = model,
            type = type.name,
            status = DeviceStatus.OWNED.name,
            apiLevel = Build.VERSION.SDK_INT,
            androidVersion = Build.VERSION.RELEASE,
            securityPatch = patch,
            buildNumber = Build.DISPLAY,
            storageGb = storageGb(),
            ramGb = ramGb(context),
            currency = currency,
            createdAt = now,
            updatedAt = now,
        )
    }

    private fun ramGb(context: Context): Int? = try {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val info = ActivityManager.MemoryInfo()
        am.getMemoryInfo(info)
        val gb = info.totalMem / 1_000_000_000.0
        listOf(1, 2, 3, 4, 6, 8, 10, 12, 16, 24).firstOrNull { it >= gb - 0.3 } ?: gb.toInt()
    } catch (e: Exception) {
        null
    }

    private fun storageGb(): Int? = try {
        val stat = StatFs(Environment.getDataDirectory().path)
        val gb = stat.totalBytes / 1_000_000_000.0
        listOf(16, 32, 64, 128, 256, 512, 1024, 2048).firstOrNull { it >= gb } ?: gb.toInt()
    } catch (e: Exception) {
        null
    }
}
