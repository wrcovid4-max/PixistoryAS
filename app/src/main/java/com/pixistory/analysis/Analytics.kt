package com.pixistory.analysis

import com.pixistory.data.model.DeviceEntity
import com.pixistory.data.model.DeviceStatus
import com.pixistory.data.model.statusEnum
import com.pixistory.data.model.typeEnum
import com.pixistory.data.model.upgrades
import com.pixistory.util.AndroidMajor
import com.pixistory.util.AndroidVersions
import com.pixistory.util.PatchStatus
import com.pixistory.util.formatMoney
import com.pixistory.util.patchStatus
import com.pixistory.util.yearOf
import java.time.LocalDate

data class CoverageRow(val major: AndroidMajor, val devices: List<DeviceEntity>) {
    val covered: Boolean get() = devices.isNotEmpty()
}

data class CoverageSummary(
    val rows: List<CoverageRow>,
    val covered: Int,
    val span: Int,
    val missing: List<AndroidMajor>,
)

data class Stats(
    val owned: Int,
    val wishlist: Int,
    val sold: Int,
    val spentByCurrency: Map<String, Double>,
    val byBrand: List<Pair<String, Int>>,
    val byType: List<Pair<String, Int>>,
    val byAndroid: List<Pair<String, Int>>,
    val byReleaseYear: List<Pair<Int, Int>>,
    val spendByYear: List<Pair<Int, Double>>,
    val patchBuckets: List<Pair<PatchStatus, Int>>,
    val oldest: DeviceEntity?,
    val newest: DeviceEntity?,
    val avgAgeYears: Double?,
    val totalStorageGb: Int,
    val totalRamGb: Int,
    val palette: List<Pair<String, Int>>,
    val upgradeCount: Int,
    val favorites: Int,
    val coverage: CoverageSummary,
    val insights: List<String>,
)

object Analytics {
    /** Coverage counts only devices you currently own that report an Android version. */
    fun coverage(devices: List<DeviceEntity>): CoverageSummary {
        val owned = devices.filter { it.statusEnum == DeviceStatus.OWNED && it.apiLevel != null }
        val byMajor = owned.groupBy { AndroidVersions.majorOf(it.apiLevel)?.major }
        val rows = AndroidVersions.majors.map { CoverageRow(it, byMajor[it.major].orEmpty()) }
        val coveredIdx = rows.withIndex().filter { it.value.covered }.map { it.index }
        if (coveredIdx.isEmpty()) return CoverageSummary(rows, 0, 0, emptyList())
        val lo = coveredIdx.min()
        val hi = coveredIdx.max()
        val inSpan = rows.subList(lo, hi + 1)
        return CoverageSummary(rows, coveredIdx.size, inSpan.size, inSpan.filter { !it.covered }.map { it.major })
    }

    fun compute(devices: List<DeviceEntity>, currency: String, today: LocalDate = LocalDate.now()): Stats {
        val owned = devices.filter { it.statusEnum == DeviceStatus.OWNED }
        val wishlist = devices.count { it.statusEnum == DeviceStatus.WISHLIST }
        val sold = devices.count { it.statusEnum == DeviceStatus.SOLD }
        val kept = devices.filter { it.statusEnum != DeviceStatus.WISHLIST }

        val spent = LinkedHashMap<String, Double>()
        kept.forEach { d -> d.price?.let { spent[""] = (spent[""] ?: 0.0) + it } }

        val byBrand = owned.filter { it.brand.isNotBlank() }.groupingBy { it.brand }.eachCount()
            .toList().sortedByDescending { it.second }
        val byType = owned.groupingBy { it.typeEnum.label }.eachCount().toList().sortedByDescending { it.second }
        val byAndroid = owned.filter { it.apiLevel != null || it.androidVersion.isNotBlank() }
            .groupBy { it.apiLevel?.let { api -> AndroidVersions.majorOf(api)?.major?.toIntOrNull() } ?: Int.MAX_VALUE }
            .toSortedMap()
            .map { (_, list) -> AndroidVersions.majorLabel(list.first().apiLevel, list.first().androidVersion) to list.size }
        val byYear = owned.mapNotNull { it.releaseYear }.groupingBy { it }.eachCount().toList().sortedBy { it.first }

        val spendByYear = kept.filter { it.price != null && it.purchaseDate != null }
            .groupBy { yearOf(it.purchaseDate!!) }
            .map { (y, l) -> y to l.sumOf { it.price ?: 0.0 } }
            .sortedBy { it.first }

        val buckets = owned.groupingBy { patchStatus(it.securityPatch, today) }.eachCount()
        val patchBuckets = PatchStatus.entries.map { it to (buckets[it] ?: 0) }

        val withYear = owned.filter { it.releaseYear != null }
        val oldest = withYear.minByOrNull { it.releaseYear!! }
        val newest = withYear.maxByOrNull { it.releaseYear!! }
        val avgAge = if (withYear.isEmpty()) null else withYear.map { today.year - it.releaseYear!! }.average()

        val palette = owned.filter { it.colorHex.isNotBlank() }.groupingBy { it.colorHex }.eachCount()
            .toList().sortedByDescending { it.second }

        val coverage = coverage(devices)
        val insights = buildInsights(owned, wishlist, spent, byBrand, patchBuckets, oldest, coverage, currency)

        return Stats(
            owned = owned.size,
            wishlist = wishlist,
            sold = sold,
            spentByCurrency = spent,
            byBrand = byBrand,
            byType = byType,
            byAndroid = byAndroid,
            byReleaseYear = byYear,
            spendByYear = spendByYear,
            patchBuckets = patchBuckets,
            oldest = oldest,
            newest = newest,
            avgAgeYears = avgAge,
            totalStorageGb = owned.sumOf { it.storageGb ?: 0 },
            totalRamGb = owned.sumOf { it.ramGb ?: 0 },
            palette = palette,
            upgradeCount = devices.sumOf { it.upgrades().size },
            favorites = devices.count { it.favorite },
            coverage = coverage,
            insights = insights,
        )
    }

    private fun buildInsights(
        owned: List<DeviceEntity>,
        wishlist: Int,
        spent: Map<String, Double>,
        byBrand: List<Pair<String, Int>>,
        patchBuckets: List<Pair<PatchStatus, Int>>,
        oldest: DeviceEntity?,
        coverage: CoverageSummary,
        currency: String,
    ): List<String> {
        val out = ArrayList<String>()
        if (owned.isEmpty()) return listOf("Add your first device to see insights here.")
        out += "You own ${owned.size} device${if (owned.size == 1) "" else "s"} from ${byBrand.size.coerceAtLeast(1)} brand${if (byBrand.size > 1) "s" else ""}."
        byBrand.firstOrNull()?.let { if (byBrand.size > 1) out += "${it.first} is your biggest brand with ${it.second}." }
        oldest?.let { out += "Oldest device: ${it.name} (${it.releaseYear})." }
        val stale = patchBuckets.firstOrNull { it.first == PatchStatus.STALE }?.second ?: 0
        val old = patchBuckets.firstOrNull { it.first == PatchStatus.OLD }?.second ?: 0
        if (stale + old > 0) out += "${stale + old} device${if (stale + old == 1) "" else "s"} ${if (stale + old == 1) "has" else "have"} a security patch older than 6 months."
        val total = spent[currency] ?: spent.values.firstOrNull()
        val cur = if (spent.containsKey(currency)) currency else spent.keys.firstOrNull()
        if (total != null && cur != null) out += "Tracked purchases add up to ${formatMoney(total, cur)}."
        if (coverage.span > 0) out += "Android coverage: ${coverage.covered} of ${coverage.span} generations between your oldest and newest."
        if (wishlist > 0) out += "$wishlist item${if (wishlist == 1) "" else "s"} on your wishlist."
        return out
    }
}
