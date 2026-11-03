@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.pixistory.ui.drops

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pixistory.R
import com.pixistory.data.pixel.DropDetail
import com.pixistory.data.pixel.PixelDrop
import com.pixistory.data.pixel.PixelKnowledge
import com.pixistory.data.pixel.PixelModel
import com.pixistory.ui.AppViewModel
import com.pixistory.ui.common.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.Locale

/** History of Pixel Drops from 2024: searchable, filterable, with the exact Pixels that received each one. */
@Composable
fun PixelDropsScreen(vm: AppViewModel, fold: FoldState) {
    val knowledge = vm.container.knowledge
    val devices by vm.devices.collectAsStateWithLifecycle()
    val lang = currentLang()
    var query by rememberSaveable { mutableStateOf("") }
    var year by rememberSaveable { mutableStateOf<Int?>(null) }
    var modelId by rememberSaveable { mutableStateOf<String?>(null) }
    var mineOnly by rememberSaveable { mutableStateOf(false) }
    var selectedId by rememberSaveable { mutableStateOf<String?>(null) }

    val mine: Set<String> = remember(devices) { devices.mapNotNull { knowledge.match(it.name, it.model)?.id }.toSet() }
    val years = remember { knowledge.drops.map { it.date.take(4).toInt() }.distinct().sortedDescending() }
    val models = remember { knowledge.models.filter { it.id.startsWith("pixel") }.sortedBy { it.name } }
    val shown = remember(query, year, modelId, mineOnly, mine, lang) {
        knowledge.drops.filter { d ->
            (year == null || d.date.startsWith(year.toString())) &&
                (modelId == null || modelId in d.devices) &&
                (!mineOnly || d.devices.any { it in mine }) &&
                matches(d, query, lang, knowledge)
        }
    }
    val featureTotal = remember { knowledge.drops.sumOf { it.features.size } }
    val selected = shown.firstOrNull { it.id == selectedId } ?: if (fold.isWide) shown.firstOrNull() else null

    Column(Modifier.fillMaxSize()) {
        PixiTopBar(title = { Text(stringResource(R.string.tab_drops)) })
        // search + filters
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Column(Modifier.widthIn(max = 860.dp).fillMaxWidth()) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    singleLine = true,
                    shape = RoundedCornerShape(28.dp),
                    leadingIcon = { Icon(Icons.Filled.Search, null) },
                    placeholder = { Text(stringResource(R.string.drops_search)) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                )
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item { FilterChip(selected = year == null, onClick = { year = null }, label = { Text(stringResource(R.string.filter_all)) }) }
                    items(years) { y -> FilterChip(selected = year == y, onClick = { year = y }, label = { Text(y.toString()) }) }
                    if (mine.isNotEmpty()) item {
                        FilterChip(
                            selected = mineOnly, onClick = { mineOnly = !mineOnly },
                            leadingIcon = { Icon(Icons.Filled.Star, null, Modifier.size(16.dp)) },
                            label = { Text(stringResource(R.string.drops_my_pixels)) },
                        )
                    }
                }
                DropdownField(
                    label = stringResource(R.string.drops_for_pixel),
                    selected = models.firstOrNull { it.id == modelId },
                    options = listOf<PixelModel?>(null) + models,
                    optionLabel = { m -> m?.name?.let { n -> if (m.id in mine) "★ $n" else n } ?: stringResource(R.string.filter_all) },
                    onSelect = { modelId = it?.id },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
                )
                Text(
                    stringResource(R.string.drops_count, shown.size, featureTotal),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 2.dp),
                )
            }
        }
        if (shown.isEmpty()) {
            EmptyState(Icons.Filled.NewReleases, stringResource(R.string.no_results_title), stringResource(R.string.drops_none))
        } else if (fold.isWide) {
            // Unfolded Pixel Fold / tablet: list on one side, the selected drop on the other.
            val gap = if (fold.isBook) (fold.hinge?.width ?: 0f).dp else 1.dp
            Row(Modifier.fillMaxSize()) {
                LazyColumn(Modifier.weight(0.42f).fillMaxHeight(), contentPadding = PaddingValues(16.dp, 8.dp, 8.dp, 24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(shown, key = { it.id }) { d -> DropRow(d, d.id == selected?.id, lang) { selectedId = d.id } }
                }
                Spacer(Modifier.width(gap))
                Column(Modifier.weight(0.58f).fillMaxHeight().verticalScroll(rememberScrollState()).padding(16.dp, 8.dp, 16.dp, 24.dp)) {
                    if (selected != null) {
                        Text(selected.title.get(lang), style = MaterialTheme.typography.headlineSmall)
                        Spacer(Modifier.height(8.dp))
                        DropBody(selected, knowledge, mine, lang, query)
                    } else {
                        EmptyState(Icons.Filled.NewReleases, stringResource(R.string.drops_select), stringResource(R.string.drops_select_body))
                    }
                }
            }
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                LazyColumn(
                    Modifier.widthIn(max = 760.dp),
                    contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 32.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(shown, key = { it.id }) { d -> DropCard(d, knowledge, mine, lang, query) }
                }
            }
        }
    }
}

private fun matches(d: PixelDrop, q: String, lang: String, knowledge: PixelKnowledge): Boolean {
    if (q.isBlank()) return true
    val t = q.trim()
    return listOf(d.title.get(lang), d.title.en, d.summary.get(lang), d.summary.en, d.tie.get(lang), d.date).any { it.contains(t, true) } ||
        d.features.any { it.get(lang).contains(t, true) || it.en.contains(t, true) } ||
        d.details.any { it.title.contains(t, true) || it.desc.contains(t, true) } ||
        d.devices.any { knowledge.model(it)?.name?.contains(t, true) == true }
}

private fun yearColor(year: Int, c: ColorScheme): Color = when (year % 3) { 0 -> c.primary; 1 -> c.secondary; else -> c.tertiary }

@Composable
private fun DateBadge(drop: PixelDrop) {
    val c = MaterialTheme.colorScheme
    val date = remember(drop.date) { runCatching { LocalDate.parse(drop.date) }.getOrNull() }
    val color = yearColor(drop.date.take(4).toIntOrNull() ?: 0, c)
    Column(
        Modifier.size(56.dp).clip(RoundedCornerShape(16.dp)).background(color.copy(alpha = 0.16f)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(date?.month?.getDisplayName(TextStyle.SHORT, Locale.getDefault()) ?: "", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
        Text(drop.date.take(4), fontSize = 12.sp, color = color)
    }
}

@Composable
private fun DropRow(drop: PixelDrop, selected: Boolean, lang: String, onClick: () -> Unit) {
    val c = MaterialTheme.colorScheme
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = if (selected) c.primaryContainer else c.surfaceContainerLow),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            DateBadge(drop)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(drop.title.get(lang), style = MaterialTheme.typography.titleSmall, maxLines = 2)
                Text(drop.tie.get(lang), style = MaterialTheme.typography.bodySmall, color = c.onSurfaceVariant, maxLines = 1)
            }
        }
    }
}

@Composable
private fun DropCard(drop: PixelDrop, knowledge: PixelKnowledge, mine: Set<String>, lang: String, query: String) {
    val c = MaterialTheme.colorScheme
    var expanded by rememberSaveable(drop.id) { mutableStateOf(false) }
    val show = expanded || query.isNotBlank()
    Card(onClick = { expanded = !expanded }, colors = CardDefaults.cardColors(containerColor = c.surfaceContainerLow), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                DateBadge(drop)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(drop.title.get(lang), style = MaterialTheme.typography.titleMedium)
                    Text(drop.tie.get(lang), style = MaterialTheme.typography.bodySmall, color = c.onSurfaceVariant)
                }
                Icon(if (show) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore, null)
            }
            Text(drop.summary.get(lang), style = MaterialTheme.typography.bodyMedium)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                StatusPill(stringResource(R.string.drops_features_n, drop.features.size), c.primary)
                StatusPill(stringResource(R.string.drops_phones_n, drop.devices.size), c.secondary)
                val got = drop.devices.count { it in mine }
                if (mine.isNotEmpty()) StatusPill(stringResource(if (got > 0) R.string.drops_yours_got else R.string.drops_yours_none, got), if (got > 0) c.tertiary else c.outline)
            }
            AnimatedVisibility(visible = show) { DropBody(drop, knowledge, mine, lang, query) }
        }
    }
}

@Composable
private fun DropBody(drop: PixelDrop, knowledge: PixelKnowledge, mine: Set<String>, lang: String, query: String) {
    val c = MaterialTheme.colorScheme
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        drop.note?.let { Text(it.get(lang), style = MaterialTheme.typography.bodySmall, color = c.onSurfaceVariant) }
        if (drop.features.isNotEmpty()) {
            Text(stringResource(R.string.drops_highlights), style = MaterialTheme.typography.titleSmall, color = c.primary)
            drop.features.forEach { Text("•  " + it.get(lang), style = MaterialTheme.typography.bodyMedium) }
        }
        val q = query.trim()
        val details = if (q.isBlank()) drop.details else drop.details.filter { it.title.contains(q, true) || it.desc.contains(q, true) }.ifEmpty { drop.details }
        if (details.isNotEmpty()) {
            Text(stringResource(R.string.drops_per_feature), style = MaterialTheme.typography.titleSmall, color = c.secondary)
            Text(stringResource(R.string.drops_english_only), style = MaterialTheme.typography.bodySmall, color = c.onSurfaceVariant)
            details.forEach { DetailRow(it, knowledge, mine) }
        }
        Text(stringResource(R.string.drops_exact_devices, drop.devices.size), style = MaterialTheme.typography.titleSmall, color = c.tertiary)
        DeviceChips(drop.devices, knowledge, mine)
    }
}

@Composable
private fun DetailRow(detail: DropDetail, knowledge: PixelKnowledge, mine: Set<String>) {
    var show by remember { mutableStateOf(false) }
    val c = MaterialTheme.colorScheme
    Column(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(c.surfaceContainerHigh.copy(alpha = 0.55f)).padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(detail.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        Text(detail.desc, style = MaterialTheme.typography.bodySmall, color = c.onSurfaceVariant)
        TextButton(onClick = { show = !show }, contentPadding = PaddingValues(0.dp)) {
            Text(stringResource(if (show) R.string.hide_devices else R.string.show_devices, detail.devices.size))
        }
        AnimatedVisibility(visible = show) { DeviceChips(detail.devices, knowledge, mine) }
    }
}

@Composable
private fun DeviceChips(ids: List<String>, knowledge: PixelKnowledge, mine: Set<String>) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        ids.forEach { id ->
            val name = knowledge.model(id)?.name ?: id
            val own = id in mine
            Text(
                (if (own) "★ " else "") + name,
                style = MaterialTheme.typography.labelMedium,
                color = if (own) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(if (own) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            )
        }
    }
}
