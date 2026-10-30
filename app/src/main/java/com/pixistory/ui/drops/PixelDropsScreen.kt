@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.pixistory.ui.drops

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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

/** History of Pixel Drops from 2024, with the exact Pixels that received each one. */
@Composable
fun PixelDropsScreen(vm: AppViewModel, fold: FoldState) {
    val knowledge = vm.container.knowledge
    val devices by vm.devices.collectAsStateWithLifecycle()
    val lang = currentLang()
    var year by rememberSaveable { mutableStateOf<Int?>(null) }
    var modelId by rememberSaveable { mutableStateOf<String?>(null) }

    val mine: Set<String> = remember(devices) {
        devices.mapNotNull { knowledge.match(it.name, it.model)?.id }.toSet()
    }
    val years = remember { knowledge.drops.map { it.date.take(4).toInt() }.distinct().sortedDescending() }
    val shown = remember(year, modelId) {
        knowledge.drops.filter { (year == null || it.date.startsWith(year.toString())) && (modelId == null || modelId in it.devices) }
    }
    val models = remember { knowledge.models.sortedBy { it.name } }

    Column(Modifier.fillMaxSize()) {
        TopAppBar(title = { Text(stringResource(R.string.tab_drops)) })
        Column(Modifier.widthIn(max = 760.dp).align(Alignment.CenterHorizontally)) {
            Text(
                stringResource(R.string.drops_intro),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            LazyRow(contentPadding = PaddingValues(16.dp, 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item { FilterChip(selected = year == null, onClick = { year = null }, label = { Text(stringResource(R.string.filter_all)) }) }
                items(years) { y -> FilterChip(selected = year == y, onClick = { year = y }, label = { Text(y.toString()) }) }
            }
            DropdownField(
                label = stringResource(R.string.drops_for_pixel),
                selected = models.firstOrNull { it.id == modelId },
                options = listOf<PixelModel?>(null) + models,
                optionLabel = { m -> m?.name?.let { n -> if (m.id in mine) "★ $n" else n } ?: stringResource(R.string.filter_all) },
                onSelect = { modelId = it?.id },
                modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
            )
            if (shown.isEmpty()) {
                EmptyState(Icons.Filled.ExpandMore, stringResource(R.string.no_results_title), stringResource(R.string.drops_none))
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp, 12.dp, 16.dp, 32.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(shown, key = { it.id }) { drop -> DropCard(drop, knowledge, mine, lang) }
                }
            }
        }
    }
}

@Composable
private fun DropCard(drop: PixelDrop, knowledge: PixelKnowledge, mine: Set<String>, lang: String) {
    var isOpen by rememberSaveable(drop.id) { mutableStateOf(false) }
    val eligibleMine = drop.devices.count { it in mine }
    val date = remember(drop.date, lang) {
        runCatching { DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).format(LocalDate.parse(drop.date)) }.getOrDefault(drop.date)
    }
    Card(onClick = { isOpen = !isOpen }, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(drop.title.get(lang), style = MaterialTheme.typography.titleMedium)
                    Text(date + "  •  " + drop.tie.get(lang), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Icon(if (isOpen) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore, null)
            }
            Text(drop.summary.get(lang), style = MaterialTheme.typography.bodyMedium)
            if (mine.isNotEmpty()) {
                val good = eligibleMine > 0
                Text(
                    stringResource(if (good) R.string.drops_yours_got else R.string.drops_yours_none, eligibleMine),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (good) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                )
            }
            if (isOpen) {
                drop.note?.let { Text(it.get(lang), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                if (drop.features.isNotEmpty()) {
                    Text(stringResource(R.string.drops_highlights), style = MaterialTheme.typography.titleSmall)
                    drop.features.forEach { Text("•  " + it.get(lang), style = MaterialTheme.typography.bodyMedium) }
                }
                if (drop.details.isNotEmpty()) {
                    Text(stringResource(R.string.drops_per_feature), style = MaterialTheme.typography.titleSmall)
                    Text(stringResource(R.string.drops_english_only), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    drop.details.forEach { DetailRow(it, knowledge, mine) }
                }
                Text(stringResource(R.string.drops_exact_devices, drop.devices.size), style = MaterialTheme.typography.titleSmall)
                DeviceChips(drop.devices, knowledge, mine)
            }
        }
    }
}

@Composable
private fun DetailRow(detail: DropDetail, knowledge: PixelKnowledge, mine: Set<String>) {
    var show by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(detail.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        Text(detail.desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        TextButton(onClick = { show = !show }, contentPadding = PaddingValues(0.dp)) {
            Text(stringResource(if (show) R.string.hide_devices else R.string.show_devices, detail.devices.size))
        }
        if (show) DeviceChips(detail.devices, knowledge, mine)
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
