@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.pixistory.ui.settings

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pixistory.R
import com.pixistory.sync.SyncState
import com.pixistory.ui.AppViewModel
import com.pixistory.ui.ExportKind
import com.pixistory.ui.common.SectionCard
import com.pixistory.util.AppLanguage
import com.pixistory.util.formatDateTime

@Composable
fun SettingsScreen(vm: AppViewModel, onOpenShelf: () -> Unit, onOpenAbout: () -> Unit) {
    val s by vm.settings.collectAsStateWithLifecycle()
    val account by vm.account.collectAsStateWithLifecycle()
    val sync by vm.syncState.collectAsStateWithLifecycle()
    val saved = remember { AppLanguage.saved() }

    val signIn = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { vm.completeSignIn(it.data) }
    val restore = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri -> uri?.let { vm.restore(it) } }
    val notifPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }

    Column(Modifier.fillMaxSize()) {
        TopAppBar(title = { Text(stringResource(R.string.tab_settings)) })
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).navigationBarsPadding(), horizontalAlignment = Alignment.CenterHorizontally) {
            Column(Modifier.widthIn(max = 640.dp).padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {

                SectionCard(stringResource(R.string.set_language)) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(selected = saved == "en", onClick = { AppLanguage.set("en") }, label = { Text("English") })
                        FilterChip(selected = saved == "ur", onClick = { AppLanguage.set("ur") }, label = { Text("اردو") })
                        FilterChip(selected = saved.isBlank(), onClick = { AppLanguage.set("") }, label = { Text(stringResource(R.string.set_system_default)) })
                    }
                }

                SectionCard(stringResource(R.string.set_account)) {
                    if (!vm.signInAvailable) {
                        Text(stringResource(R.string.set_sync_not_configured), style = MaterialTheme.typography.bodyMedium)
                    } else if (account == null) {
                        Text(stringResource(R.string.set_sign_in_body), style = MaterialTheme.typography.bodyMedium)
                        Button(onClick = { signIn.launch(vm.signInIntent) }, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.set_sign_in)) }
                    } else {
                        Text(account!!.displayName, style = MaterialTheme.typography.titleMedium)
                        Text(account!!.email, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(stringResource(R.string.set_auto_sync), Modifier.weight(1f))
                            Switch(checked = s.autoSync, onCheckedChange = { vm.setAutoSync(it) })
                        }
                        val status = when (val st = sync) {
                            is SyncState.Syncing -> stringResource(R.string.sync_running)
                            is SyncState.Error -> stringResource(R.string.sync_error, st.message)
                            is SyncState.Done -> stringResource(R.string.sync_done, st.pulled, st.pushed)
                            SyncState.Idle -> if (s.lastSync > 0) stringResource(R.string.sync_last, formatDateTime(s.lastSync)) else stringResource(R.string.sync_never)
                        }
                        Text(status, style = MaterialTheme.typography.bodySmall)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { vm.syncNow() }, enabled = sync !is SyncState.Syncing) { Text(stringResource(R.string.set_sync_now)) }
                            OutlinedButton(onClick = { vm.signOut() }) { Text(stringResource(R.string.set_sign_out)) }
                        }
                    }
                }

                SectionCard(stringResource(R.string.set_ai)) {
                    Text(stringResource(R.string.set_ai_body), style = MaterialTheme.typography.bodySmall)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(selected = s.aiProvider == "anthropic", onClick = { vm.updateSettings { it.copy(aiProvider = "anthropic") } }, label = { Text("Claude") })
                        FilterChip(selected = s.aiProvider == "gemini", onClick = { vm.updateSettings { it.copy(aiProvider = "gemini") } }, label = { Text("Gemini") })
                    }
                    if (s.aiProvider == "gemini") {
                        SecretField(stringResource(R.string.set_api_key_gemini), s.geminiKey) { v -> vm.updateSettings { it.copy(geminiKey = v) } }
                        PlainField(stringResource(R.string.set_model), s.geminiModel) { v -> vm.updateSettings { it.copy(geminiModel = v) } }
                    } else {
                        SecretField(stringResource(R.string.set_api_key_claude), s.anthropicKey) { v -> vm.updateSettings { it.copy(anthropicKey = v) } }
                        PlainField(stringResource(R.string.set_model), s.anthropicModel) { v -> vm.updateSettings { it.copy(anthropicModel = v) } }
                    }
                }

                SectionCard(stringResource(R.string.set_appearance)) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("system" to R.string.theme_system, "light" to R.string.theme_light, "dark" to R.string.theme_dark).forEach { (k, label) ->
                            FilterChip(selected = s.theme == k, onClick = { vm.updateSettings { it.copy(theme = k) } }, label = { Text(stringResource(label)) })
                        }
                    }
                    PlainField(stringResource(R.string.set_currency), s.currency) { v -> vm.updateSettings { it.copy(currency = v.uppercase().take(4)) } }
                }

                SectionCard(stringResource(R.string.set_notifications)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(R.string.set_patch_alerts), Modifier.weight(1f))
                        Switch(checked = s.patchAlerts, onCheckedChange = { on ->
                            vm.setPatchAlerts(on)
                            if (on && Build.VERSION.SDK_INT >= 33) notifPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                        })
                    }
                    Text(stringResource(R.string.set_patch_after), style = MaterialTheme.typography.bodySmall)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(90, 180, 365).forEach { days ->
                            FilterChip(selected = s.patchDays == days, onClick = { vm.updateSettings { it.copy(patchDays = days) } }, label = { Text(stringResource(R.string.days_n, days)) })
                        }
                    }
                }

                SectionCard(stringResource(R.string.set_tags)) {
                    PlainField(stringResource(R.string.set_tag_prefix), s.assetPrefix) { v -> vm.updateSettings { it.copy(assetPrefix = v.uppercase().take(6)) } }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(s.assetDigits.toString(), { v -> v.toIntOrNull()?.let { n -> vm.updateSettings { it.copy(assetDigits = n.coerceIn(1, 8)) } } }, label = { Text(stringResource(R.string.set_tag_digits)) }, singleLine = true, keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                        OutlinedTextField(s.nextAssetNumber.toString(), { v -> v.toIntOrNull()?.let { n -> vm.updateSettings { it.copy(nextAssetNumber = n.coerceAtLeast(1)) } } }, label = { Text(stringResource(R.string.set_tag_next)) }, singleLine = true, keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(R.string.set_tag_category), Modifier.weight(1f))
                        Switch(checked = s.assetUseCategory, onCheckedChange = { on -> vm.updateSettings { it.copy(assetUseCategory = on) } })
                    }
                    OutlinedButton(onClick = { vm.export(ExportKind.LABELS) }, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.set_print_labels)) }
                }

                SectionCard(stringResource(R.string.set_data)) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { vm.export(ExportKind.PDF) }) { Text("PDF") }
                        OutlinedButton(onClick = { vm.export(ExportKind.CSV) }) { Text("CSV") }
                        OutlinedButton(onClick = { vm.export(ExportKind.JSON) }) { Text("JSON") }
                        OutlinedButton(onClick = { vm.export(ExportKind.TEXT) }) { Text("TXT") }
                        OutlinedButton(onClick = { vm.export(ExportKind.CARD) }) { Text(stringResource(R.string.set_card)) }
                    }
                    Button(onClick = { vm.export(ExportKind.BACKUP) }, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.set_backup)) }
                    OutlinedButton(onClick = { restore.launch(arrayOf("application/zip", "application/octet-stream")) }, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.set_restore)) }
                }

                OutlinedButton(onClick = onOpenShelf, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.shelf_mode)) }
                OutlinedButton(onClick = onOpenAbout, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.about_title)) }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun PlainField(label: String, value: String, onChange: (String) -> Unit) {
    OutlinedTextField(value, onChange, label = { Text(label) }, singleLine = true, modifier = Modifier.fillMaxWidth())
}

@Composable
private fun SecretField(label: String, value: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value, onChange, label = { Text(label) }, singleLine = true,
        visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(),
    )
}
