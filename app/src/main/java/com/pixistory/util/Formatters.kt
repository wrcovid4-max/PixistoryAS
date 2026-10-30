package com.pixistory.util

import androidx.compose.ui.graphics.Color
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit

fun formatDate(millis: Long): String =
    DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        .format(Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()))

fun formatDateTime(millis: Long): String =
    DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)
        .format(Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()))

fun yearOf(millis: Long): Int = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).year

/** The amount with thousands separators. (Currency was removed from the app; the argument is ignored.) */
fun formatMoney(amount: Double, @Suppress("UNUSED_PARAMETER") currency: String = ""): String {
    val rounded = if (amount % 1.0 == 0.0) amount.toLong().toString() else String.format("%.2f", amount)
    val grouped = rounded.replace(Regex("(\\d)(?=(\\d{3})+(?!\\d))"), "$1,")
    return grouped
}

fun parsePatchDate(patch: String): LocalDate? = try {
    if (patch.isBlank()) null else LocalDate.parse(patch.trim())
} catch (e: Exception) {
    null
}

/** Days since the Android security patch level of a device, or null when unknown. */
fun patchAgeDays(patch: String, today: LocalDate = LocalDate.now()): Long? =
    parsePatchDate(patch)?.let { ChronoUnit.DAYS.between(it, today) }

enum class PatchStatus(val label: String, val color: Color) {
    CURRENT("Up to date", Color(0xFF2E9E5B)),
    AGING("Ageing", Color(0xFFE0A100)),
    OLD("Old patch", Color(0xFFE2702A)),
    STALE("Out of support?", Color(0xFFD64545)),
    UNKNOWN("Patch unknown", Color(0xFF8A94A6)),
}

fun patchStatus(patch: String, today: LocalDate = LocalDate.now()): PatchStatus {
    val days = patchAgeDays(patch, today) ?: return PatchStatus.UNKNOWN
    return when {
        days <= 90 -> PatchStatus.CURRENT
        days <= 180 -> PatchStatus.AGING
        days <= 365 -> PatchStatus.OLD
        else -> PatchStatus.STALE
    }
}
