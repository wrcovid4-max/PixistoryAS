@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.pixistory.ui.features

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pixistory.R
import com.pixistory.data.features.Avail
import com.pixistory.data.features.AvailFeature
import com.pixistory.data.features.Country
import com.pixistory.data.features.FeatureData
import com.pixistory.data.features.FoldApp
import com.pixistory.data.features.GraveItem
import com.pixistory.ui.AppViewModel
import com.pixistory.ui.common.*
import java.time.LocalDate
import java.util.Locale

/** Features tab: where Google features work (by country), what Google has killed, and apps made for the Pixel Fold. */
@Composable
fun FeaturesScreen(vm: AppViewModel, fold: FoldState) {
    val data = vm.container.features
    var section by rememberSaveable { mutableIntStateOf(0) }
    Column(Modifier.fillMaxSize()) {
        PixiTopBar(title = { Text(stringResource(R.string.tab_features)) })
        Row(Modifier.padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = section == 0, onClick = { section = 0 }, label = { Text(stringResource(R.string.feat_availability)) })
            FilterChip(selected = section == 1, onClick = { section = 1 }, label = { Text(stringResource(R.string.feat_graveyard)) })
            FilterChip(selected = section == 2, onClick = { section = 2 }, label = { Text(stringResource(R.string.feat_fold)) })
        }
        Box(Modifier.weight(1f).fillMaxWidth()) {
            when (section) {
                0 -> AvailabilitySection(data, fold)
                1 -> GraveyardSection(data)
                else -> FoldAppsSection(data)
            }
        }
    }
}

// ---------------------------------------------------------------- availability by country

@Composable
private fun AvailabilitySection(data: FeatureData, fold: FoldState) {
    var selectedSlug by rememberSaveable { mutableStateOf<String?>(null) }
    var query by rememberSaveable { mutableStateOf("") }
    val selected = data.countries.firstOrNull { it.slug == selectedSlug }
    val mine = remember { Locale.getDefault().country }

    val list: @Composable (Modifier) -> Unit = { mod ->
        val shown = remember(query) {
            data.countries.filter { query.isBlank() || it.name.contains(query, ignoreCase = true) }
                .sortedWith(compareByDescending<Country> { it.iso.equals(mine, true) }.thenBy { it.name })
        }
        Column(mod) {
            OutlinedTextField(
                query, { query = it }, singleLine = true,
                leadingIcon = { Icon(Icons.Filled.Search, null) },
                placeholder = { Text(stringResource(R.string.feat_search_country)) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            )
            LazyColumn(
                contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {
                    Text(stringResource(R.string.feat_availability_intro, data.features.size, data.trackedCount), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                items(shown, key = { it.slug }) { c ->
                    val yes = data.features.count { data.statusOf(it, c) == Avail.AVAILABLE }
                    val no = data.features.count { data.statusOf(it, c) == Avail.NOT_AVAILABLE }
                    val isMine = c.iso.equals(mine, true)
                    Card(
                        onClick = { selectedSlug = c.slug },
                        colors = CardDefaults.cardColors(containerColor = if (c.slug == selectedSlug) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceContainerLow),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(c.flag.ifBlank { "\uD83C\uDF10" }, style = MaterialTheme.typography.headlineMedium)
                            Spacer(Modifier.width(14.dp))
                            Column(Modifier.weight(1f)) {
                                Text(c.name, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                if (isMine) Text(stringResource(R.string.feat_your_country), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                            }
                            if (yes + no > 0) {
                                StatusPill("✓ $yes", MaterialTheme.colorScheme.secondary)
                                Spacer(Modifier.width(6.dp))
                                StatusPill("✗ $no", MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }

    if (fold.isWide) {
        val gap = if (fold.isBook) (fold.hinge?.width ?: 0f).dp else 1.dp
        Row(Modifier.fillMaxSize()) {
            list(Modifier.weight(0.4f).fillMaxHeight())
            Spacer(Modifier.width(gap))
            Box(Modifier.weight(0.6f).fillMaxHeight()) {
                if (selected != null) CountryDetail(selected, data, onBack = null)
                else EmptyState(Icons.Filled.Public, stringResource(R.string.feat_pick_country), stringResource(R.string.feat_pick_country_body))
            }
        }
    } else if (selected != null) {
        BackHandler { selectedSlug = null }
        CountryDetail(selected, data, onBack = { selectedSlug = null })
    } else {
        list(Modifier.fillMaxSize())
    }
}

@Composable
private fun CountryDetail(country: Country, data: FeatureData, onBack: (() -> Unit)?) {
    val uri = LocalUriHandler.current
    val c = MaterialTheme.colorScheme
    val groups = remember(country) { data.features.groupBy { data.statusOf(it, country) } }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).navigationBarsPadding(), horizontalAlignment = Alignment.CenterHorizontally) {
        Column(Modifier.widthIn(max = 720.dp).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (onBack != null) IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back)) }
                Text(country.flag.ifBlank { "\uD83C\uDF10" }, style = MaterialTheme.typography.displaySmall)
                Spacer(Modifier.width(12.dp))
                Text(country.name, style = MaterialTheme.typography.headlineSmall)
            }
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusPill(stringResource(R.string.feat_n_available, groups[Avail.AVAILABLE]?.size ?: 0), c.secondary)
                StatusPill(stringResource(R.string.feat_n_not_available, groups[Avail.NOT_AVAILABLE]?.size ?: 0), c.error)
                StatusPill(stringResource(R.string.feat_n_varies, groups[Avail.VARIES]?.size ?: 0), c.tertiary)
            }
            Text(stringResource(R.string.feat_partial_note, data.trackedCount, data.features.size), style = MaterialTheme.typography.bodySmall, color = c.onSurfaceVariant)
            Button(onClick = { uri.openUri(country.url) }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.AutoMirrored.Filled.OpenInNew, null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.feat_open_live, country.name))
            }
            FeatureGroup(stringResource(R.string.feat_group_available), groups[Avail.AVAILABLE].orEmpty(), c.secondary)
            FeatureGroup(stringResource(R.string.feat_group_not_available), groups[Avail.NOT_AVAILABLE].orEmpty(), c.error)
            FeatureGroup(stringResource(R.string.feat_group_varies), groups[Avail.VARIES].orEmpty(), c.tertiary)
            Text(stringResource(R.string.feat_source, data.availabilityDate), style = MaterialTheme.typography.bodySmall, color = c.onSurfaceVariant)
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun FeatureGroup(title: String, features: List<AvailFeature>, color: Color) {
    if (features.isEmpty()) return
    SectionCard("$title (${features.size})", accent = color) {
        features.forEach { f ->
            Column(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(f.name, style = MaterialTheme.typography.titleSmall)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (f.service.isNotBlank()) StatusPill(f.service, MaterialTheme.colorScheme.primary)
                    if (f.fromPixel.isNotBlank()) StatusPill(stringResource(R.string.feat_from, f.fromPixel), MaterialTheme.colorScheme.secondary)
                    if (f.pixelExclusive) StatusPill(stringResource(R.string.feat_pixel_only), MaterialTheme.colorScheme.error)
                    if (f.locationBased) StatusPill(stringResource(R.string.feat_location), MaterialTheme.colorScheme.tertiary)
                    if (f.languageBased) StatusPill(stringResource(R.string.feat_language), MaterialTheme.colorScheme.tertiary)
                }
                if (f.desc.isNotBlank()) Text(f.desc, style = MaterialTheme.typography.bodySmall)
                if (f.note.isNotBlank()) Text(f.note, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

// ---------------------------------------------------------------- Killed by Google

@Composable
private fun GraveyardSection(data: FeatureData) {
    var query by rememberSaveable { mutableStateOf("") }
    var filter by rememberSaveable { mutableIntStateOf(0) }
    val uri = LocalUriHandler.current
    val today = remember { LocalDate.now().toString() }
    val c = MaterialTheme.colorScheme
    val shown = remember(query, filter) {
        data.graveyard.filter { g ->
            (query.isBlank() || g.name.contains(query, true) || g.description.contains(query, true)) &&
                when (filter) {
                    1 -> g.type == "app"
                    2 -> g.type == "service"
                    3 -> g.type == "hardware"
                    4 -> g.closed > today
                    else -> true
                }
        }
    }
    Column(Modifier.fillMaxSize()) {
        OutlinedTextField(
            query, { query = it }, singleLine = true,
            leadingIcon = { Icon(Icons.Filled.Search, null) },
            placeholder = { Text(stringResource(R.string.search_hint_short)) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        )
        Row(Modifier.padding(16.dp, 8.dp).horizontalScrollRow(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(R.string.filter_all, R.string.grave_apps, R.string.grave_services, R.string.grave_hardware, R.string.grave_soon).forEachIndexed { i, r ->
                FilterChip(selected = filter == i, onClick = { filter = i }, label = { Text(stringResource(r)) })
            }
        }
        LazyColumn(contentPadding = PaddingValues(16.dp, 4.dp, 16.dp, 24.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item { Text(stringResource(R.string.grave_count, shown.size), style = MaterialTheme.typography.titleSmall, color = c.onSurfaceVariant) }
            items(shown, key = { it.name + it.closed }) { g -> GraveCard(g, today) { if (g.link.isNotBlank()) uri.openUri(g.link) } }
            item { Text(stringResource(R.string.grave_source), style = MaterialTheme.typography.bodySmall, color = c.onSurfaceVariant) }
        }
    }
}

@Composable
private fun GraveCard(g: GraveItem, today: String, onOpen: () -> Unit) {
    val c = MaterialTheme.colorScheme
    val upcoming = g.closed > today
    val typeColor = when (g.type) { "app" -> c.primary; "hardware" -> c.tertiary; else -> c.secondary }
    Card(colors = CardDefaults.cardColors(containerColor = c.surfaceContainerLow), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(g.name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                StatusPill(g.type.replaceFirstChar { it.uppercase() }, typeColor)
            }
            val years = (g.opened.take(4).ifBlank { "?" }) + " – " + g.closed.take(4)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(years, style = MaterialTheme.typography.labelLarge, color = c.onSurfaceVariant)
                if (upcoming) StatusPill(stringResource(R.string.grave_shutting_down, g.closed), c.error)
            }
            if (g.description.isNotBlank()) Text(g.description, style = MaterialTheme.typography.bodySmall)
            if (g.link.isNotBlank()) TextButton(onClick = onOpen, contentPadding = PaddingValues(0.dp)) { Text(stringResource(R.string.grave_read_more)) }
        }
    }
}

// ---------------------------------------------------------------- Pixel Fold apps

@Composable
private fun FoldAppsSection(data: FeatureData) {
    val uri = LocalUriHandler.current
    val c = MaterialTheme.colorScheme
    LazyColumn(contentPadding = PaddingValues(16.dp, 4.dp, 16.dp, 24.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = c.primaryContainer), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(stringResource(R.string.fold_title), style = MaterialTheme.typography.titleMedium, color = c.onPrimaryContainer)
                    Text(stringResource(R.string.fold_intro), style = MaterialTheme.typography.bodyMedium, color = c.onPrimaryContainer)
                }
            }
        }
        item { Text(stringResource(R.string.fold_this_app), style = MaterialTheme.typography.bodySmall, color = c.onSurfaceVariant) }
        items(data.foldApps, key = { it.pkg }) { a -> FoldAppCard(a) { uri.openUri(a.url) } }
        item { Text(stringResource(R.string.fold_source), style = MaterialTheme.typography.bodySmall, color = c.onSurfaceVariant) }
    }
}

@Composable
private fun FoldAppCard(app: FoldApp, onOpen: () -> Unit) {
    val c = MaterialTheme.colorScheme
    Card(colors = CardDefaults.cardColors(containerColor = c.surfaceContainerLow), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(app.name, style = MaterialTheme.typography.titleMedium)
                    Text(app.publisher, style = MaterialTheme.typography.bodySmall, color = c.onSurfaceVariant)
                }
                StatusPill(app.category, c.secondary)
            }
            if (app.note.isNotBlank()) Text(app.note, style = MaterialTheme.typography.bodyMedium)
            Text(stringResource(R.string.fold_source_line, app.source), style = MaterialTheme.typography.labelSmall, color = c.onSurfaceVariant)
            OutlinedButton(onClick = onOpen) {
                Icon(Icons.AutoMirrored.Filled.OpenInNew, null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.fold_open_play))
            }
        }
    }
}

@Composable
private fun Modifier.horizontalScrollRow(): Modifier = this.then(Modifier.horizontalScroll(rememberScrollState()))
