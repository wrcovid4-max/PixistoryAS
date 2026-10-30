@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.pixistory.ui.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pixistory.R
import com.pixistory.analysis.Analytics
import com.pixistory.analysis.CoverageRow
import com.pixistory.ui.AppViewModel
import com.pixistory.ui.ExportKind
import com.pixistory.ui.common.*
import com.pixistory.util.formatMoney

@Composable
fun AnalysisScreen(vm: AppViewModel, fold: FoldState) {
    val devices by vm.devices.collectAsStateWithLifecycle()
    val settings by vm.settings.collectAsStateWithLifecycle()
    var page by rememberSaveable { mutableStateOf(0) }
    val stats = remember(devices, settings.currency) { Analytics.compute(devices, settings.currency) }

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(stringResource(R.string.tab_analysis)) },
            actions = {
                IconButton(onClick = { vm.export(ExportKind.CARD) }) { Icon(Icons.Filled.Share, stringResource(R.string.share_card)) }
            },
        )
        Row(Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = page == 0, onClick = { page = 0 }, label = { Text(stringResource(R.string.analysis_charts)) })
            FilterChip(selected = page == 1, onClick = { page = 1 }, label = { Text(stringResource(R.string.analysis_coverage)) })
        }
        if (devices.isEmpty()) {
            EmptyState(Icons.Filled.Analytics, stringResource(R.string.empty_title), stringResource(R.string.analysis_empty))
        } else {
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).navigationBarsPadding(), horizontalAlignment = Alignment.CenterHorizontally) {
                Column(Modifier.widthIn(max = 760.dp).padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    if (page == 0) Charts(stats, settings.currency) else Coverage(stats.coverage.rows, stats.coverage.covered, stats.coverage.span, stats.coverage.missing.map { it.major })
                }
            }
        }
    }
}

@Composable
private fun Charts(stats: com.pixistory.analysis.Stats, currency: String) {
    SectionCard(stringResource(R.string.analysis_overview)) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Big(stats.owned.toString(), stringResource(R.string.status_owned))
            Big(stats.wishlist.toString(), stringResource(R.string.status_wishlist))
            Big(stats.sold.toString(), stringResource(R.string.status_sold))
            Big(stats.upgradeCount.toString(), stringResource(R.string.section_upgrades))
            Big(stats.favorites.toString(), stringResource(R.string.filter_favorites))
        }
        stats.spentByCurrency.forEach { (cur, total) ->
            Text(stringResource(R.string.analysis_spent, formatMoney(total, cur)), style = MaterialTheme.typography.bodyMedium)
        }
        stats.avgAgeYears?.let { Text(stringResource(R.string.analysis_avg_age, String.format("%.1f", it)), style = MaterialTheme.typography.bodyMedium) }
        if (stats.totalStorageGb > 0 || stats.totalRamGb > 0) {
            Text(stringResource(R.string.analysis_memory, stats.totalStorageGb, stats.totalRamGb), style = MaterialTheme.typography.bodyMedium)
        }
    }
    SectionCard(stringResource(R.string.analysis_insights)) {
        stats.insights.forEach { Text("•  $it", style = MaterialTheme.typography.bodyMedium) }
    }
    if (stats.byBrand.isNotEmpty()) SectionCard(stringResource(R.string.analysis_by_brand)) { HBarChart(stats.byBrand.take(8), Modifier.fillMaxWidth()) }
    if (stats.byType.isNotEmpty()) SectionCard(stringResource(R.string.analysis_by_type)) {
        DonutChart(stats.byType.mapIndexed { i, (l, c) -> Triple(l, c, ChartColors[i % ChartColors.size]) }, centerLabel = stats.owned.toString())
    }
    if (stats.byAndroid.isNotEmpty()) SectionCard(stringResource(R.string.analysis_by_android)) { HBarChart(stats.byAndroid, Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.tertiary) }
    if (stats.byReleaseYear.isNotEmpty()) SectionCard(stringResource(R.string.analysis_by_year)) {
        ColumnChart(stats.byReleaseYear.map { it.first.toString() to it.second.toDouble() })
    }
    if (stats.spendByYear.isNotEmpty()) SectionCard(stringResource(R.string.analysis_spend_year, currency)) {
        ColumnChart(stats.spendByYear.map { it.first.toString() to it.second }, color = MaterialTheme.colorScheme.secondary, valueLabel = { v -> if (v >= 1000) "${(v / 1000).toInt()}k" else v.toInt().toString() })
    }
    SectionCard(stringResource(R.string.analysis_patch_health)) {
        DonutChart(stats.patchBuckets.filter { it.second > 0 }.map { (s, c) -> Triple(s.text(), c, s.color) }, centerLabel = stats.owned.toString())
    }
    if (stats.palette.isNotEmpty()) SectionCard(stringResource(R.string.analysis_palette)) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            stats.palette.forEach { (hex, n) ->
                val c = parseColorOrNull(hex) ?: return@forEach
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(26.dp).clip(CircleShape).background(c))
                    Spacer(Modifier.width(6.dp))
                    Text("×$n", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
    stats.oldest?.let { o -> stats.newest?.let { n ->
        SectionCard(stringResource(R.string.analysis_extremes)) {
            Text(stringResource(R.string.analysis_oldest, o.name, o.releaseYear ?: 0), style = MaterialTheme.typography.bodyMedium)
            Text(stringResource(R.string.analysis_newest, n.name, n.releaseYear ?: 0), style = MaterialTheme.typography.bodyMedium)
        }
    } }
}

@Composable
private fun Big(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineMedium)
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun Coverage(rows: List<CoverageRow>, covered: Int, span: Int, missing: List<String>) {
    var filter by rememberSaveable { mutableStateOf(0) }
    SectionCard(stringResource(R.string.analysis_coverage)) {
        if (span == 0) {
            Text(stringResource(R.string.coverage_none), style = MaterialTheme.typography.bodyMedium)
        } else {
            Text(stringResource(R.string.coverage_summary, covered, span), style = MaterialTheme.typography.titleMedium)
            LinearProgressIndicator(progress = { covered.toFloat() / span }, modifier = Modifier.fillMaxWidth())
            if (missing.isNotEmpty()) Text(stringResource(R.string.coverage_missing, missing.joinToString(", ")), style = MaterialTheme.typography.bodyMedium)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = filter == 0, onClick = { filter = 0 }, label = { Text(stringResource(R.string.filter_all)) })
            FilterChip(selected = filter == 1, onClick = { filter = 1 }, label = { Text(stringResource(R.string.coverage_covered)) })
            FilterChip(selected = filter == 2, onClick = { filter = 2 }, label = { Text(stringResource(R.string.coverage_gaps)) })
        }
    }
    val firstIdx = rows.indexOfFirst { it.covered }
    val lastIdx = rows.indexOfLast { it.covered }
    rows.forEachIndexed { i, row ->
        val inSpan = i in firstIdx..lastIdx && firstIdx >= 0
        val visible = when (filter) { 1 -> row.covered; 2 -> !row.covered && inSpan; else -> true }
        if (visible) {
            ListItem(
                headlineContent = { Text(row.major.name) },
                supportingContent = {
                    if (row.covered) Text(row.devices.joinToString(", ") { it.name }, maxLines = 2)
                    else Text(stringResource(if (inSpan) R.string.coverage_gap else R.string.coverage_not_owned))
                },
                trailingContent = { Text(if (row.covered) "✓ ${row.devices.size}" else "—") },
                colors = ListItemDefaults.colors(containerColor = if (row.covered) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface),
            )
        }
    }
}
