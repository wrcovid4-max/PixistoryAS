@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.pixistory.ui.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pixistory.R
import com.pixistory.ui.AppViewModel
import com.pixistory.ui.common.*

@Composable
fun AskScreen(vm: AppViewModel, fold: FoldState) {
    val chat by vm.chat.collectAsStateWithLifecycle()
    val busy by vm.aiBusy.collectAsStateWithLifecycle()
    val error by vm.aiError.collectAsStateWithLifecycle()
    val settings by vm.settings.collectAsStateWithLifecycle()
    var input by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val configured = if (settings.aiProvider == "gemini") settings.geminiKey.isNotBlank() else settings.anthropicKey.isNotBlank()
    val suggestions = listOf(
        stringResource(R.string.ask_s1), stringResource(R.string.ask_s2), stringResource(R.string.ask_s3),
        stringResource(R.string.ask_s4), stringResource(R.string.ask_s5), stringResource(R.string.ask_s6),
    )
    LaunchedEffect(chat.size, busy) { if (chat.isNotEmpty()) listState.animateScrollToItem(chat.size) }

    Column(Modifier.fillMaxSize().imePadding()) {
        TopAppBar(
            title = { Text(stringResource(R.string.tab_ask)) },
            actions = { if (chat.isNotEmpty()) IconButton(onClick = { vm.clearChat() }) { Icon(Icons.Filled.DeleteSweep, stringResource(R.string.ask_clear)) } },
        )
        if (!configured) {
            Card(Modifier.padding(horizontal = 16.dp).fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)) {
                Text(stringResource(R.string.ask_need_key), Modifier.padding(14.dp), style = MaterialTheme.typography.bodyMedium)
            }
        }
        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
            if (chat.isEmpty()) {
                Column(Modifier.widthIn(max = 640.dp).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Filled.AutoAwesome, null, tint = MaterialTheme.colorScheme.primary)
                    Text(stringResource(R.string.ask_intro), style = MaterialTheme.typography.bodyLarge)
                    ChipRow(suggestions, { if (configured) vm.askAi(it) else input = it })
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.widthIn(max = 720.dp).fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(chat, key = { it.id }) { m ->
                        val mine = m.role == "user"
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = if (mine) Arrangement.End else Arrangement.Start) {
                            Box(
                                Modifier
                                    .widthIn(max = 560.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(if (mine) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh)
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                            ) { FormattedText(m.text) }
                        }
                    }
                    if (busy) item { Row { CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp); Spacer(Modifier.width(10.dp)); Text(stringResource(R.string.ask_thinking)) } }
                    error?.let { e -> item { Text(e, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium) } }
                }
            }
        }
        Row(Modifier.padding(12.dp).navigationBarsPadding(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text(stringResource(R.string.ask_hint)) },
                maxLines = 4,
            )
            Spacer(Modifier.width(8.dp))
            FilledIconButton(onClick = { if (input.isNotBlank()) { vm.askAi(input); input = "" } }, enabled = !busy && configured) {
                Icon(Icons.AutoMirrored.Filled.Send, stringResource(R.string.ask_send))
            }
        }
    }
}
