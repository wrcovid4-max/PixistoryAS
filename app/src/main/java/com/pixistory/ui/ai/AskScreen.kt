@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.pixistory.ui.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Key
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

/** Ask Pixi: a clean conversation view. Your messages are solid blue, Pixi's are quiet cards with a hairline. */
@Composable
fun AskScreen(vm: AppViewModel, fold: FoldState) {
    val chat by vm.chat.collectAsStateWithLifecycle()
    val busy by vm.aiBusy.collectAsStateWithLifecycle()
    val error by vm.aiError.collectAsStateWithLifecycle()
    var input by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val configured = vm.aiConfigured
    val c = MaterialTheme.colorScheme
    val suggestions = listOf(
        stringResource(R.string.ask_s1), stringResource(R.string.ask_s2), stringResource(R.string.ask_s3),
        stringResource(R.string.ask_s4), stringResource(R.string.ask_s5), stringResource(R.string.ask_s6),
    )
    LaunchedEffect(chat.size, busy) { if (chat.isNotEmpty()) listState.animateScrollToItem(chat.size) }

    Column(Modifier.fillMaxSize().imePadding()) {
        PixiTopBar(
            title = { Text(stringResource(R.string.ask_name)) },
            actions = {
                if (chat.isNotEmpty()) IconButton(onClick = { vm.clearChat() }) { Icon(Icons.Filled.DeleteSweep, stringResource(R.string.ask_clear)) }
            },
        )
        Column(Modifier.weight(1f).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Column(Modifier.widthIn(max = 760.dp).weight(1f).fillMaxWidth()) {
                Text(
                    stringResource(R.string.ask_banner_sub),
                    style = MaterialTheme.typography.bodyMedium,
                    color = c.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
                if (!configured) {
                    Row(
                        Modifier.padding(20.dp, 12.dp).fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(c.tertiaryContainer).padding(14.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Icon(Icons.Filled.Key, null, tint = c.onTertiaryContainer, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(10.dp))
                        Text(stringResource(R.string.ask_need_key), style = MaterialTheme.typography.bodySmall, color = c.onTertiaryContainer)
                    }
                }
                if (chat.isEmpty()) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(stringResource(R.string.ask_intro), style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.height(4.dp))
                        suggestions.forEach { text ->
                            val shape = RoundedCornerShape(14.dp)
                            Row(
                                Modifier.fillMaxWidth().clip(shape).border(1.dp, c.outlineVariant, shape).background(c.surfaceContainerLow)
                                    .clickable { if (configured) vm.askAi(text) else input = text }.padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(Icons.Filled.AutoAwesome, null, tint = c.primary, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(12.dp))
                                Text(text, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(20.dp, 12.dp, 20.dp, 12.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        items(chat, key = { it.id }) { m ->
                            if (m.role == "user") {
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                    Box(
                                        Modifier.widthIn(max = 520.dp).clip(RoundedCornerShape(18.dp, 18.dp, 4.dp, 18.dp))
                                            .background(c.primary).padding(horizontal = 16.dp, vertical = 11.dp),
                                    ) { FormattedText(m.text, color = c.onPrimary) }
                                }
                            } else {
                                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                                    PixiMark()
                                    Spacer(Modifier.width(10.dp))
                                    Box(
                                        Modifier.widthIn(max = 560.dp).clip(RoundedCornerShape(18.dp, 18.dp, 18.dp, 4.dp))
                                            .background(c.surfaceContainerLow).border(1.dp, c.outlineVariant, RoundedCornerShape(18.dp, 18.dp, 18.dp, 4.dp))
                                            .padding(horizontal = 16.dp, vertical = 11.dp),
                                    ) { FormattedText(m.text) }
                                }
                            }
                        }
                        if (busy) item {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                PixiMark()
                                Spacer(Modifier.width(10.dp))
                                CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                                Spacer(Modifier.width(10.dp))
                                Text(stringResource(R.string.ask_thinking), style = MaterialTheme.typography.bodyMedium, color = c.onSurfaceVariant)
                            }
                        }
                        error?.let { e ->
                            item {
                                Text(e, Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(c.errorContainer).padding(12.dp), color = c.onErrorContainer, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
        // input
        Column(Modifier.fillMaxWidth().background(c.surface), horizontalAlignment = Alignment.CenterHorizontally) {
            HorizontalDivider(color = c.outlineVariant, thickness = 0.5.dp)
            Row(Modifier.widthIn(max = 760.dp).fillMaxWidth().padding(12.dp).navigationBarsPadding(), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(26.dp),
                    placeholder = { Text(stringResource(R.string.ask_hint)) },
                    maxLines = 4,
                )
                Spacer(Modifier.width(10.dp))
                FilledIconButton(
                    onClick = { if (input.isNotBlank()) { vm.askAi(input); input = "" } },
                    enabled = !busy && configured,
                    modifier = Modifier.size(50.dp),
                ) { Icon(Icons.AutoMirrored.Filled.Send, stringResource(R.string.ask_send)) }
            }
        }
    }
}

@Composable
private fun PixiMark() {
    Box(Modifier.size(30.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
        Icon(Icons.Filled.AutoAwesome, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
    }
}
