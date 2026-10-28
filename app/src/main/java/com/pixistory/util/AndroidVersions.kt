package com.pixistory.util

data class AndroidRelease(val api: Int, val version: String, val name: String, val year: Int)

/** The Android versions the coverage tab and the version pickers know about. Edit to add new ones. */
object AndroidVersions {
    val releases: List<AndroidRelease> = listOf(
        AndroidRelease(19, "4.4", "KitKat", 2013),
        AndroidRelease(21, "5.0", "Lollipop", 2014),
        AndroidRelease(22, "5.1", "Lollipop", 2015),
        AndroidRelease(23, "6.0", "Marshmallow", 2015),
        AndroidRelease(24, "7.0", "Nougat", 2016),
        AndroidRelease(25, "7.1", "Nougat", 2016),
        AndroidRelease(26, "8.0", "Oreo", 2017),
        AndroidRelease(27, "8.1", "Oreo", 2017),
        AndroidRelease(28, "9", "Pie", 2018),
        AndroidRelease(29, "10", "Android 10", 2019),
        AndroidRelease(30, "11", "Android 11", 2020),
        AndroidRelease(31, "12", "Android 12", 2021),
        AndroidRelease(32, "12L", "Android 12L", 2022),
        AndroidRelease(33, "13", "Android 13", 2022),
        AndroidRelease(34, "14", "Android 14", 2023),
        AndroidRelease(35, "15", "Android 15", 2024),
        AndroidRelease(36, "16", "Android 16", 2025),
    )

    /** Major generations for the coverage view: one row per major Android version. */
    val majors: List<AndroidMajor> = listOf(
        AndroidMajor("4", "KitKat", listOf(19)),
        AndroidMajor("5", "Lollipop", listOf(21, 22)),
        AndroidMajor("6", "Marshmallow", listOf(23)),
        AndroidMajor("7", "Nougat", listOf(24, 25)),
        AndroidMajor("8", "Oreo", listOf(26, 27)),
        AndroidMajor("9", "Pie", listOf(28)),
        AndroidMajor("10", "Android 10", listOf(29)),
        AndroidMajor("11", "Android 11", listOf(30)),
        AndroidMajor("12", "Android 12 / 12L", listOf(31, 32)),
        AndroidMajor("13", "Android 13", listOf(33)),
        AndroidMajor("14", "Android 14", listOf(34)),
        AndroidMajor("15", "Android 15", listOf(35)),
        AndroidMajor("16", "Android 16", listOf(36)),
    )

    fun byApi(api: Int?): AndroidRelease? = releases.firstOrNull { it.api == api }

    fun majorOf(api: Int?): AndroidMajor? = majors.firstOrNull { api != null && api in it.apis }

    /** "Android 15" style label for an API level, or the raw version string when unknown. */
    fun label(api: Int?, fallback: String = ""): String {
        val r = byApi(api)
        return when {
            r != null -> "Android ${r.version}"
            fallback.isNotBlank() -> "Android $fallback"
            api != null -> "API $api"
            else -> "Unknown"
        }
    }

    fun majorLabel(api: Int?, fallback: String = ""): String =
        majorOf(api)?.let { if (it.major == "4") "Android 4.4" else "Android ${it.major}" }
            ?: if (fallback.isNotBlank()) "Android ${fallback.substringBefore('.')}" else "Unknown"

    /** Best-guess API level from a version string such as "13" or "12L". */
    fun apiFromVersion(version: String): Int? {
        val v = version.trim()
        releases.firstOrNull { it.version.equals(v, ignoreCase = true) }?.let { return it.api }
        val major = v.substringBefore('.').filter { it.isDigit() }
        return majors.firstOrNull { it.major == major }?.apis?.first()
    }
}

data class AndroidMajor(val major: String, val name: String, val apis: List<Int>)
