@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.pixistory.ui.devices

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pixistory.R
import com.pixistory.data.model.DeviceEntity
import com.pixistory.data.model.DeviceStatus
import com.pixistory.data.model.conditionEnum
import com.pixistory.data.model.customFields
import com.pixistory.data.model.statusEnum
import com.pixistory.data.model.typeEnum
import com.pixistory.data.model.upgrades
import com.pixistory.data.repo.AssetTags
import com.pixistory.export.shareText
import com.pixistory.ui.AppViewModel
import com.pixistory.ui.common.*
import com.pixistory.util.AndroidVersions
import com.pixistory.util.formatDate
import com.pixistory.util.formatMoney
import com.pixistory.util.patchAgeDays

@Composable
fun DeviceDetailScreen(
    device: DeviceEntity,
    vm: AppViewModel,
    fold: FoldState,
    onBack: (() -> Unit)?,
    onDeleted: () -> Unit,
) {
    val context = LocalContext.current
    val files by vm.filesFor(device.id).collectAsStateWithLifecycle(initialValue = emptyList())
    val knowledge = vm.container.knowledge
    val pixel = remember(device.name, device.model) { knowledge.match(device.name, device.model) }
    var confirmDelete by remember { mutableStateOf(false) }
    var logUpdate by remember { mutableStateOf(false) }
    val lang = currentLang()

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(device.name.ifBlank { device.model }, maxLines = 1) },
            navigationIcon = {
                if (onBack != null) IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back)) }
            },
            actions = {
                IconButton(onClick = { vm.toggleFavorite(device.id) }) {
                    Icon(if (device.favorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder, stringResource(R.string.favorite))
                }
                IconButton(onClick = { vm.startEdit(device) }) { Icon(Icons.Filled.Edit, stringResource(R.string.edit)) }
                IconButton(onClick = { shareText(context, summaryText(device), device.name) }) { Icon(Icons.Filled.Share, stringResource(R.string.share)) }
                IconButton(onClick = { confirmDelete = true }) { Icon(Icons.Filled.Delete, stringResource(R.string.delete)) }
            },
        )
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                Modifier.widthIn(max = 720.dp).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    DeviceAvatar(device, vm.container.files, 96.dp)
                    Spacer(Modifier.width(16.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(listOf(device.brand, device.model).filter { it.isNotBlank() }.joinToString(" "), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            AssistChip(onClick = {}, label = { Text(device.statusEnum.text()) })
                            AssistChip(onClick = {}, label = { Text(device.typeEnum.text()) })
                            if (device.statusEnum == DeviceStatus.OWNED && device.securityPatch.isNotBlank()) PatchChip(device.securityPatch)
                        }
                    }
                }

                SectionCard(stringResource(R.string.section_software)) {
                    InfoRow(stringResource(R.string.f_android), if (device.apiLevel != null || device.androidVersion.isNotBlank()) AndroidVersions.label(device.apiLevel, device.androidVersion) else "")
                    InfoRow(stringResource(R.string.f_api), device.apiLevel?.toString().orEmpty())
                    InfoRow(stringResource(R.string.f_patch), device.securityPatch.let { p ->
                        val age = patchAgeDays(p)
                        if (p.isBlank()) "" else if (age != null) stringResource(R.string.patch_with_age, p, age.toInt()) else p
                    })
                    InfoRow(stringResource(R.string.f_build), device.buildNumber)
                    if (device.statusEnum == DeviceStatus.OWNED) {
                        OutlinedButton(onClick = { logUpdate = true }, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Filled.SystemUpdate, null)
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.log_update))
                        }
                    }
                }

                SectionCard(stringResource(R.string.section_details)) {
                    InfoRow(stringResource(R.string.f_released), device.releaseYear?.toString().orEmpty())
                    InfoRow(stringResource(R.string.f_serial), device.serial)
                    InfoRow(stringResource(R.string.f_color), device.colorName)
                    InfoRow(stringResource(R.string.f_storage), device.storageGb?.let { "$it GB" }.orEmpty())
                    InfoRow(stringResource(R.string.f_ram), device.ramGb?.let { "$it GB" }.orEmpty())
                    InfoRow(stringResource(R.string.f_condition), device.conditionEnum.text())
                    InfoRow(stringResource(R.string.f_group), device.groupName)
                    InfoRow(stringResource(R.string.f_purchased), device.purchaseDate?.let { formatDate(it) }.orEmpty())
                    InfoRow(stringResource(R.string.f_price), device.price?.let { formatMoney(it, device.currency) }.orEmpty())
                    if (device.notes.isNotBlank()) {
                        Text(stringResource(R.string.f_notes), style = MaterialTheme.typography.labelLarge)
                        Text(device.notes, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                if (device.assetTag.isNotBlank()) {
                    SectionCard(stringResource(R.string.section_tag)) {
                        val bmp: Bitmap? = remember(device.assetTag) { runCatching { AssetTags.barcode(device.assetTag, 600, 140) }.getOrNull() }
                        if (bmp != null) {
                            Image(bmp.asImageBitmap(), contentDescription = device.assetTag, modifier = Modifier.fillMaxWidth().height(70.dp))
                        }
                        Text(device.assetTag, style = MaterialTheme.typography.titleMedium)
                    }
                }

                if (pixel != null) PixelInsights(pixel, vm.container.knowledge, lang)

                val custom = device.customFields()
                if (custom.isNotEmpty()) {
                    SectionCard(stringResource(R.string.section_custom)) {
                        custom.forEach { (k, v) -> InfoRow(k, v) }
                    }
                }
                val ups = device.upgrades()
                if (ups.isNotEmpty()) {
                    SectionCard(stringResource(R.string.section_upgrades)) {
                        ups.sortedByDescending { it.date }.forEach { u ->
                            Column(Modifier.padding(vertical = 2.dp)) {
                                Text(stringResource(R.string.upgrade_line, u.from, u.to), style = MaterialTheme.typography.bodyMedium)
                                Text(formatDate(u.date) + if (u.note.isNotBlank()) "  •  ${u.note}" else "", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
                if (files.isNotEmpty()) {
                    SectionCard(stringResource(R.string.section_files)) {
                        files.forEach { f ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                TextButton(onClick = { vm.openDocument(f) }, modifier = Modifier.weight(1f)) { Text(f.displayName, maxLines = 1) }
                                IconButton(onClick = { vm.removeDocument(f) }) { Icon(Icons.Filled.Delete, stringResource(R.string.delete)) }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text(stringResource(R.string.delete_title)) },
            text = { Text(stringResource(R.string.delete_body, device.name)) },
            confirmButton = {
                TextButton(onClick = { confirmDelete = false; vm.delete(device.id); onDeleted() }) { Text(stringResource(R.string.delete)) }
            },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text(stringResource(R.string.cancel)) } },
        )
    }
    if (logUpdate) LogUpdateDialog(device, vm) { logUpdate = false }
}

@Composable
private fun LogUpdateDialog(device: DeviceEntity, vm: AppViewModel, onDismiss: () -> Unit) {
    val releases = AndroidVersions.releases
    var version by remember { mutableStateOf(releases.firstOrNull { it.api == (device.apiLevel ?: 0) + 1 } ?: releases.last()) }
    var patch by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.log_update)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                DropdownField(
                    label = stringResource(R.string.f_android),
                    selected = version,
                    options = releases,
                    optionLabel = { "Android ${it.version}" },
                    onSelect = { version = it },
                )
                OutlinedTextField(patch, { patch = it }, singleLine = true, label = { Text(stringResource(R.string.f_patch_hint)) })
                OutlinedTextField(note, { note = it }, label = { Text(stringResource(R.string.f_notes)) })
            }
        },
        confirmButton = {
            TextButton(onClick = { vm.logUpgrade(device, version.version, patch.trim(), note.trim()); onDismiss() }) { Text(stringResource(R.string.save)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } },
    )
}

private fun summaryText(d: DeviceEntity): String = buildString {
    append(d.name)
    if (d.apiLevel != null || d.androidVersion.isNotBlank()) append("\n${AndroidVersions.label(d.apiLevel, d.androidVersion)}")
    if (d.securityPatch.isNotBlank()) append("\nSecurity patch: ${d.securityPatch}")
    if (d.releaseYear != null) append("\nReleased: ${d.releaseYear}")
    if (d.assetTag.isNotBlank()) append("\nTag: ${d.assetTag}")
    append("\n\nPixistory")
}
