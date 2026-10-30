@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.pixistory.ui.devices

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.pixistory.R
import com.pixistory.data.catalog.CatalogItem
import com.pixistory.data.model.*
import com.pixistory.ui.AppViewModel
import com.pixistory.ui.common.*
import com.pixistory.util.AndroidVersions
import com.pixistory.util.formatDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun DeviceEditorScreen(
    initial: DeviceEntity,
    isNew: Boolean,
    catalogFirst: Boolean,
    vm: AppViewModel,
    onSaved: (DeviceEntity) -> Unit,
    onClose: () -> Unit,
) {
    BackHandler(onClose = onClose)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val settings by vm.settings.collectAsStateWithLifecycle()
    val devices by vm.devices.collectAsStateWithLifecycle()
    val files by vm.filesFor(initial.id).collectAsStateWithLifecycle(initialValue = emptyList())
    var d by remember(initial.id) { mutableStateOf(initial) }
    var priceText by remember(initial.id) { mutableStateOf(initial.price?.let { if (it % 1.0 == 0.0) it.toLong().toString() else it.toString() }.orEmpty()) }
    var yearText by remember(initial.id) { mutableStateOf(initial.releaseYear?.toString().orEmpty()) }
    var catalogOpen by remember { mutableStateOf(catalogFirst) }
    var datePicker by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf(false) }
    val custom = remember(initial.id) { mutableStateListOf<Pair<String, String>>().apply { addAll(initial.customFields().toList()) } }
    val upgrades = remember(initial.id) { mutableStateListOf<Upgrade>().apply { addAll(initial.upgrades()) } }
    var upgradeDialog by remember { mutableStateOf(false) }

    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) scope.launch {
            val rel = withContext(Dispatchers.IO) { vm.container.files.savePhoto(uri, d.id) }
            if (rel != null) d = d.copy(photoPath = rel)
        }
    }
    val docPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) vm.addDocument(d.id, uri)
    }
    val groups = remember(devices) { devices.map { it.groupName }.filter { it.isNotBlank() }.distinct() }

    fun scanInto(apply: (String) -> Unit) {
        GmsBarcodeScanning.getClient(context).startScan().addOnSuccessListener { b -> b.rawValue?.let(apply) }
    }

    fun save() {
        val name = d.name.trim().ifBlank { listOf(d.brand, d.model).filter { it.isNotBlank() }.joinToString(" ") }
        if (name.isBlank()) { nameError = true; return }
        val fields = LinkedHashMap<String, String>()
        custom.filter { it.first.isNotBlank() }.forEach { fields[it.first.trim()] = it.second.trim() }
        val out = d.copy(
            name = name,
            releaseYear = yearText.toIntOrNull(),
            price = priceText.replace(',', '.').toDoubleOrNull(),
            customFieldsJson = fields.toJsonString(),
            upgradesJson = upgrades.toList().toUpgradesJson(),
        )
        vm.save(out) { saved -> onSaved(saved); onClose() }
    }

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(stringResource(if (isNew) R.string.add_device else R.string.edit)) },
            navigationIcon = { IconButton(onClick = onClose) { Icon(Icons.Filled.Close, stringResource(R.string.cancel)) } },
            actions = { TextButton(onClick = { save() }) { Text(stringResource(R.string.save)) } },
        )
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).imePadding().navigationBarsPadding(), horizontalAlignment = Alignment.CenterHorizontally) {
            Column(Modifier.widthIn(max = 640.dp).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    DeviceAvatar(d, vm.container.files, 88.dp)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) {
                            Icon(Icons.Filled.PhotoCamera, null); Spacer(Modifier.width(8.dp)); Text(stringResource(R.string.pick_photo))
                        }
                        if (isNew) OutlinedButton(onClick = { catalogOpen = true }) {
                            Icon(Icons.Filled.Search, null); Spacer(Modifier.width(8.dp)); Text(stringResource(R.string.pick_catalog))
                        }
                    }
                }

                OutlinedTextField(d.name, { d = d.copy(name = it); nameError = false }, label = { Text(stringResource(R.string.f_name)) }, isError = nameError, singleLine = true, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(d.brand, { d = d.copy(brand = it) }, label = { Text(stringResource(R.string.f_brand)) }, singleLine = true, modifier = Modifier.weight(1f))
                    OutlinedTextField(d.model, { d = d.copy(model = it) }, label = { Text(stringResource(R.string.f_model)) }, singleLine = true, modifier = Modifier.weight(1f))
                }
                DropdownField(stringResource(R.string.f_type), d.typeEnum, DeviceType.entries.toList(), { it.text() }, { d = d.copy(type = it.name) }, Modifier.fillMaxWidth())
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DeviceStatus.entries.forEach { s ->
                        FilterChip(selected = d.statusEnum == s, onClick = { d = d.copy(status = s.name) }, label = { Text(s.text()) })
                    }
                }

                Text(stringResource(R.string.section_software), style = MaterialTheme.typography.titleSmall)
                DropdownField(
                    stringResource(R.string.f_android),
                    AndroidVersions.byApi(d.apiLevel),
                    listOf<com.pixistory.util.AndroidRelease?>(null) + AndroidVersions.releases,
                    { it?.let { r -> "Android ${r.version}" } ?: stringResource(R.string.unknown) },
                    { r -> d = d.copy(apiLevel = r?.api, androidVersion = r?.version.orEmpty()) },
                    Modifier.fillMaxWidth(),
                )
                OutlinedTextField(d.securityPatch, { d = d.copy(securityPatch = it.trim()) }, label = { Text(stringResource(R.string.f_patch_hint)) }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(d.buildNumber, { d = d.copy(buildNumber = it) }, label = { Text(stringResource(R.string.f_build)) }, singleLine = true, modifier = Modifier.fillMaxWidth())

                Text(stringResource(R.string.section_details), style = MaterialTheme.typography.titleSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(yearText, { yearText = it.filter(Char::isDigit).take(4) }, label = { Text(stringResource(R.string.f_released)) }, singleLine = true, keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                    OutlinedTextField(d.storageGb?.toString().orEmpty(), { d = d.copy(storageGb = it.filter(Char::isDigit).take(5).toIntOrNull()) }, label = { Text(stringResource(R.string.f_storage)) }, singleLine = true, keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                    OutlinedTextField(d.ramGb?.toString().orEmpty(), { d = d.copy(ramGb = it.filter(Char::isDigit).take(3).toIntOrNull()) }, label = { Text(stringResource(R.string.f_ram)) }, singleLine = true, keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                }
                Text(stringResource(R.string.f_color), style = MaterialTheme.typography.labelLarge)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    DeviceColors.forEach { (hex, name) ->
                        val c = parseColorOrNull(hex)!!
                        Box(
                            Modifier.size(36.dp).clip(CircleShape).background(c)
                                .then(if (d.colorHex == hex) Modifier.background(c.copy(alpha = 0.5f)) else Modifier)
                                .clickable { d = d.copy(colorHex = hex, colorName = name) },
                            contentAlignment = Alignment.Center,
                        ) { if (d.colorHex == hex) Icon(Icons.Filled.Check, null, tint = if (hex == "#F2F2F7" || hex == "#FACC15" || hex == "#A7F3D0") androidx.compose.ui.graphics.Color.Black else androidx.compose.ui.graphics.Color.White) }
                    }
                }
                OutlinedTextField(d.colorName, { d = d.copy(colorName = it) }, label = { Text(stringResource(R.string.f_color_name)) }, singleLine = true, modifier = Modifier.fillMaxWidth())
                DropdownField(stringResource(R.string.f_condition), d.conditionEnum, Condition.entries.toList(), { it.text() }, { d = d.copy(condition = it.name) }, Modifier.fillMaxWidth())

                Text(stringResource(R.string.section_purchase), style = MaterialTheme.typography.titleSmall)
                OutlinedButton(onClick = { datePicker = true }, modifier = Modifier.fillMaxWidth()) {
                    Text(d.purchaseDate?.let { formatDate(it) } ?: stringResource(R.string.f_purchased))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(priceText, { priceText = it.filter { c -> c.isDigit() || c == '.' || c == ',' } }, label = { Text(stringResource(R.string.f_price)) }, singleLine = true, keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.weight(1f))
                    OutlinedTextField(d.currency, { d = d.copy(currency = it.uppercase().take(4)) }, label = { Text(stringResource(R.string.f_currency)) }, singleLine = true, modifier = Modifier.weight(0.6f))
                }

                Text(stringResource(R.string.section_organise), style = MaterialTheme.typography.titleSmall)
                OutlinedTextField(d.groupName, { d = d.copy(groupName = it) }, label = { Text(stringResource(R.string.f_group)) }, singleLine = true, modifier = Modifier.fillMaxWidth())
                if (groups.isNotEmpty()) ChipRow(groups, { d = d.copy(groupName = it) })
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(d.assetTag, { d = d.copy(assetTag = it.trim()) }, label = { Text(stringResource(R.string.f_asset_tag)) }, singleLine = true, modifier = Modifier.weight(1f))
                    OutlinedButton(onClick = { vm.newTag(d.typeEnum) { tag -> d = d.copy(assetTag = tag) } }) { Text(stringResource(R.string.generate)) }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(d.serial, { d = d.copy(serial = it.trim()) }, label = { Text(stringResource(R.string.f_serial)) }, singleLine = true, modifier = Modifier.weight(1f))
                    IconButton(onClick = { scanInto { code -> d = d.copy(serial = code) } }) { Icon(Icons.Filled.QrCodeScanner, stringResource(R.string.scan_tag)) }
                }
                OutlinedTextField(d.notes, { d = d.copy(notes = it) }, label = { Text(stringResource(R.string.f_notes)) }, minLines = 2, modifier = Modifier.fillMaxWidth())

                Text(stringResource(R.string.section_custom), style = MaterialTheme.typography.titleSmall)
                custom.forEachIndexed { i, (k, v) ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(k, { custom[i] = it to v }, label = { Text(stringResource(R.string.field_name)) }, singleLine = true, modifier = Modifier.weight(1f))
                        OutlinedTextField(v, { custom[i] = k to it }, label = { Text(stringResource(R.string.field_value)) }, singleLine = true, modifier = Modifier.weight(1f))
                        IconButton(onClick = { custom.removeAt(i) }) { Icon(Icons.Filled.Close, stringResource(R.string.delete)) }
                    }
                }
                OutlinedButton(onClick = { custom.add("" to "") }) { Icon(Icons.Filled.Add, null); Spacer(Modifier.width(8.dp)); Text(stringResource(R.string.add_field)) }

                Text(stringResource(R.string.section_upgrades), style = MaterialTheme.typography.titleSmall)
                upgrades.forEachIndexed { i, u ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(R.string.upgrade_line, u.from, u.to) + "  •  " + formatDate(u.date), modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                        IconButton(onClick = { upgrades.removeAt(i) }) { Icon(Icons.Filled.Close, stringResource(R.string.delete)) }
                    }
                }
                OutlinedButton(onClick = { upgradeDialog = true }) { Icon(Icons.Filled.Add, null); Spacer(Modifier.width(8.dp)); Text(stringResource(R.string.add_upgrade)) }

                Text(stringResource(R.string.section_files), style = MaterialTheme.typography.titleSmall)
                files.forEach { f ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = { vm.openDocument(f) }, modifier = Modifier.weight(1f)) { Text(f.displayName, maxLines = 1) }
                        IconButton(onClick = { vm.removeDocument(f) }) { Icon(Icons.Filled.Close, stringResource(R.string.delete)) }
                    }
                }
                OutlinedButton(onClick = { docPicker.launch(arrayOf("*/*")) }) { Icon(Icons.Filled.AttachFile, null); Spacer(Modifier.width(8.dp)); Text(stringResource(R.string.attach_file)) }
                Spacer(Modifier.height(32.dp))
            }
        }
    }

    if (datePicker) {
        val state = rememberDatePickerState(initialSelectedDateMillis = d.purchaseDate)
        DatePickerDialog(
            onDismissRequest = { datePicker = false },
            confirmButton = { TextButton(onClick = { d = d.copy(purchaseDate = state.selectedDateMillis); datePicker = false }) { Text(stringResource(R.string.save)) } },
            dismissButton = { TextButton(onClick = { d = d.copy(purchaseDate = null); datePicker = false }) { Text(stringResource(R.string.clear)) } },
        ) { DatePicker(state) }
    }
    if (catalogOpen) {
        CatalogPicker(vm, onDismiss = { catalogOpen = false }) { item ->
            val pixel = vm.container.knowledge.match(item.title)
            d = d.copy(
                name = item.title, brand = item.brand, model = item.name, type = item.type.name,
                apiLevel = item.api, androidVersion = AndroidVersions.byApi(item.api)?.version.orEmpty(),
            )
            yearText = item.year?.toString().orEmpty()
            if (pixel != null) {
                if (pixel.specs["ram"] != null) d = d.copy(ramGb = Regex("\\d+").find(pixel.specs["ram"]!!)?.value?.toIntOrNull())
            }
            catalogOpen = false
        }
    }
    if (upgradeDialog) {
        var from by remember { mutableStateOf(AndroidVersions.label(d.apiLevel, d.androidVersion).removePrefix("Android ")) }
        var to by remember { mutableStateOf("") }
        var note by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { upgradeDialog = false },
            title = { Text(stringResource(R.string.add_upgrade)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(from, { from = it }, label = { Text(stringResource(R.string.upgrade_from)) }, singleLine = true)
                    OutlinedTextField(to, { to = it }, label = { Text(stringResource(R.string.upgrade_to)) }, singleLine = true)
                    OutlinedTextField(note, { note = it }, label = { Text(stringResource(R.string.f_notes)) })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (to.isNotBlank()) upgrades.add(Upgrade(System.currentTimeMillis(), from.trim(), to.trim(), note.trim()))
                    upgradeDialog = false
                }) { Text(stringResource(R.string.save)) }
            },
            dismissButton = { TextButton(onClick = { upgradeDialog = false }) { Text(stringResource(R.string.cancel)) } },
        )
    }
}

@Composable
private fun CatalogPicker(vm: AppViewModel, onDismiss: () -> Unit, onPick: (CatalogItem) -> Unit) {
    var q by remember { mutableStateOf("") }
    val results = remember(q) { vm.container.catalog.search(q) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.pick_catalog)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(q, { q = it }, singleLine = true, placeholder = { Text(stringResource(R.string.search_hint)) }, modifier = Modifier.fillMaxWidth())
                Column(Modifier.heightIn(max = 360.dp).verticalScroll(rememberScrollState())) {
                    results.forEach { item ->
                        ListItem(
                            headlineContent = { Text(item.title) },
                            supportingContent = { Text(listOfNotNull(item.type.text(), item.year?.toString()).joinToString(" • ")) },
                            modifier = Modifier.clickable { onPick(item) },
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } },
    )
}
