@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.pixistory.ui.devices

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pixistory.R
import com.pixistory.data.model.DeviceEntity
import com.pixistory.data.model.DeviceStatus
import com.pixistory.data.model.DeviceType
import com.pixistory.data.model.statusEnum
import com.pixistory.data.model.typeEnum
import com.pixistory.ui.AppViewModel
import com.pixistory.ui.common.*
import com.pixistory.util.AndroidVersions
import com.pixistory.util.CurrentDevice

enum class StatusFilter(@androidx.annotation.StringRes val label: Int) {
    ALL(R.string.filter_all), OWNED(R.string.status_owned), WISHLIST(R.string.status_wishlist),
    SOLD(R.string.status_sold), FAVORITES(R.string.filter_favorites),
}

enum class GroupBy(@androidx.annotation.StringRes val label: Int) {
    NONE(R.string.group_none), GROUP(R.string.group_collection), BRAND(R.string.group_brand), TYPE(R.string.group_type),
}

enum class SortBy(@androidx.annotation.StringRes val label: Int) {
    NAME(R.string.sort_name), NEWEST(R.string.sort_newest), OLDEST(R.string.sort_oldest), ADDED(R.string.sort_added),
}

private sealed interface Entry {
    data class Header(val title: String) : Entry
    data class Item(val device: DeviceEntity) : Entry
}

@Composable
fun DevicesScreen(
    vm: AppViewModel,
    fold: FoldState,
    selectedId: String?,
    onSelect: (String?) -> Unit,
    onOpenShelf: () -> Unit,
) {
    val devices by vm.devices.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var query by rememberSaveable { mutableStateOf("") }
    var status by rememberSaveable { mutableStateOf(StatusFilter.ALL) }
    var type by rememberSaveable { mutableStateOf<DeviceType?>(null) }
    var group by rememberSaveable { mutableStateOf(GroupBy.NONE) }
    var sort by rememberSaveable { mutableStateOf(SortBy.NAME) }
    var addMenu by remember { mutableStateOf(false) }
    val noGroup = stringResource(R.string.group_unassigned)
    val settings by vm.settings.collectAsStateWithLifecycle()

    val filtered = remember(devices, query, status, type, sort) {
        devices
            .filter {
                when (status) {
                    StatusFilter.ALL -> true
                    StatusFilter.OWNED -> it.statusEnum == DeviceStatus.OWNED
                    StatusFilter.WISHLIST -> it.statusEnum == DeviceStatus.WISHLIST
                    StatusFilter.SOLD -> it.statusEnum == DeviceStatus.SOLD
                    StatusFilter.FAVORITES -> it.favorite
                }
            }
            .filter { type == null || it.typeEnum == type }
            .filter { d ->
                query.isBlank() || listOf(d.name, d.brand, d.model, d.serial, d.assetTag, d.groupName, d.notes, d.androidVersion)
                    .any { it.contains(query, ignoreCase = true) }
            }
            .let { list ->
                when (sort) {
                    SortBy.NAME -> list
                    SortBy.NEWEST -> list.sortedByDescending { it.releaseYear ?: 0 }
                    SortBy.OLDEST -> list.sortedBy { it.releaseYear ?: Int.MAX_VALUE }
                    SortBy.ADDED -> list.sortedByDescending { it.createdAt }
                }
            }
    }
    val entries = remember(filtered, group, noGroup) {
        if (group == GroupBy.NONE) {
            filtered.map { Entry.Item(it) }
        } else {
            filtered.groupBy {
                when (group) {
                    GroupBy.GROUP -> it.groupName.ifBlank { noGroup }
                    GroupBy.BRAND -> it.brand.ifBlank { noGroup }
                    else -> it.type
                }
            }.toSortedMap().flatMap { (k, list) ->
                val title = if (group == GroupBy.TYPE) DeviceType.of(k).label else k
                listOf<Entry>(Entry.Header(title)) + list.map { Entry.Item(it) }
            }
        }
    }
    val selected = devices.firstOrNull { it.id == selectedId }

    val listPane: @Composable (Modifier) -> Unit = { mod ->
        Column(mod) {
            PixiTopBar(
                title = { Text(stringResource(R.string.tab_devices)) },
                actions = {
                    IconButton(onClick = onOpenShelf) { Icon(Icons.Filled.ViewCarousel, stringResource(R.string.shelf_mode)) }
                },
            )
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                singleLine = true,
                leadingIcon = { Icon(Icons.Filled.Search, null) },
                placeholder = { Text(stringResource(R.string.search_hint)) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(StatusFilter.entries.toList()) { f ->
                    FilterChip(selected = status == f, onClick = { status = f }, label = { Text(stringResource(f.label)) })
                }
            }
            Row(Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MenuChip(
                    label = stringResource(R.string.menu_type, type?.text() ?: stringResource(R.string.filter_all)),
                    options = listOf<DeviceType?>(null) + DeviceType.entries,
                    optionLabel = { it?.text() ?: stringResource(R.string.filter_all) },
                    onSelect = { type = it },
                )
                MenuChip(
                    label = stringResource(R.string.menu_group, stringResource(group.label)),
                    options = GroupBy.entries.toList(),
                    optionLabel = { stringResource(it.label) },
                    onSelect = { group = it },
                )
                MenuChip(
                    label = stringResource(R.string.menu_sort, stringResource(sort.label)),
                    options = SortBy.entries.toList(),
                    optionLabel = { stringResource(it.label) },
                    onSelect = { sort = it },
                )
            }
            if (filtered.isEmpty()) {
                EmptyState(
                    Icons.Filled.Devices,
                    stringResource(if (devices.isEmpty()) R.string.empty_title else R.string.no_results_title),
                    stringResource(if (devices.isEmpty()) R.string.empty_body else R.string.no_results_body),
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(entries, key = { if (it is Entry.Item) it.device.id else (it as Entry.Header).title }) { e ->
                        when (e) {
                            is Entry.Header -> Text(
                                e.title,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 10.dp),
                            )
                            is Entry.Item -> DeviceRow(
                                e.device,
                                selected = fold.isWide && e.device.id == selectedId,
                                vm = vm,
                                onClick = { onSelect(e.device.id) },
                            )
                        }
                    }
                }
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        if (fold.isWide) {
            // Unfolded Pixel Fold / tablet: list and details side by side. In book posture the two panes
            // are separated by the width of the hinge so nothing sits on the crease.
            val gap = if (fold.isBook) (fold.hinge?.width ?: 0f).dp else 1.dp
            Row(Modifier.fillMaxSize()) {
                listPane(Modifier.weight(0.42f).fillMaxHeight())
                Spacer(Modifier.width(gap))
                Box(Modifier.weight(0.58f).fillMaxHeight()) {
                    if (selected != null) {
                        DeviceDetailScreen(selected, vm, fold, onBack = null, onDeleted = { onSelect(null) })
                    } else {
                        EmptyState(Icons.Filled.Devices, stringResource(R.string.select_device), stringResource(R.string.select_device_body))
                    }
                }
            }
        } else if (selected != null) {
            BackHandler { onSelect(null) }
            DeviceDetailScreen(selected, vm, fold, onBack = { onSelect(null) }, onDeleted = { onSelect(null) })
        } else {
            listPane(Modifier.fillMaxSize())
        }

        if (!(selected != null && !fold.isWide)) {
            Box(Modifier.align(Alignment.BottomEnd).padding(20.dp)) {
                FloatingActionButton(onClick = { addMenu = true }) { Icon(Icons.Filled.Add, stringResource(R.string.add_device)) }
                DropdownMenu(expanded = addMenu, onDismissRequest = { addMenu = false }) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.add_catalog)) },
                        onClick = { addMenu = false; vm.startNew(catalogFirst = true) },
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.add_this_device)) },
                        onClick = { addMenu = false; vm.startNew(CurrentDevice.snapshot(context, settings.currency)) },
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.add_manual)) },
                        onClick = { addMenu = false; vm.startNew() },
                    )
                }
            }
        }
    }
}

@Composable
private fun <T> MenuChip(label: String, options: List<T>, optionLabel: @Composable (T) -> String, onSelect: (T) -> Unit) {
    var menuOpen by remember { mutableStateOf(false) }
    Box {
        AssistChip(onClick = { menuOpen = true }, label = { Text(label, maxLines = 1, overflow = TextOverflow.Ellipsis) })
        DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
            options.forEach { o ->
                DropdownMenuItem(text = { Text(optionLabel(o)) }, onClick = { onSelect(o); menuOpen = false })
            }
        }
    }
}

@Composable
private fun DeviceRow(device: DeviceEntity, selected: Boolean, vm: AppViewModel, onClick: () -> Unit) {
    val files = vm.container.files
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceContainerLow,
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            DeviceAvatar(device, files, 52.dp)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(device.name.ifBlank { device.model }, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                val sub = listOfNotNull(
                    device.brand.takeIf { it.isNotBlank() },
                    if (device.apiLevel != null || device.androidVersion.isNotBlank()) AndroidVersions.label(device.apiLevel, device.androidVersion) else null,
                    device.releaseYear?.toString(),
                ).joinToString(" • ")
                if (sub.isNotBlank()) Text(sub, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    StatusPill(device.statusEnum.text(), statusColor(device.statusEnum))
                    if (device.statusEnum == DeviceStatus.OWNED && device.securityPatch.isNotBlank()) PatchChip(device.securityPatch)
                }
            }
            IconButton(onClick = { vm.toggleFavorite(device.id) }) {
                Icon(
                    if (device.favorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = stringResource(R.string.favorite),
                    tint = if (device.favorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                )
            }
        }
    }
}
