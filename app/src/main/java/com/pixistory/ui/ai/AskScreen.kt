@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.pixistory.ui.ai

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pixistory.R
import com.pixistory.ui.AppViewModel
import com.pixistory.ui.common.*

private val PixiGradient = Brush.linearGradient(listOf(Color(0xFF1A73E8), Color(0xFF7B61FF)))

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
            title = { Text(stringResource(R.string.tab_ask)) },
            actions = {
                if (chat.isNotEmpty()) IconButton(onClick = { vm.clearChat() }) { Icon(Icons.Filled.DeleteSweep, stringResource(R.string.ask_clear)) }
            },
        )
        Column(Modifier.weight(1f).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Column(Modifier.widthIn(max = 760.dp).weight(1f).fillMaxWidth()) {
                // Header banner
                Row(
                    Modifier.padding(16.dp, 12.dp, 16.dp, 4.dp).fillMaxWidth().clip(RoundedCornerShape(22.dp)).background(PixiGradient).padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    PixiAvatar(44, Color.White.copy(alpha = 0.22f))
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(stringResource(R.string.ask_name), style = MaterialTheme.typography.titleLarge, color = Color.White)
                        Text(stringResource(R.string.ask_banner_sub), style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.88f))
                    }
                }
                if (!configured) {
                    Row(
                        Modifier.padding(16.dp, 8.dp).fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(c.tertiaryContainer).padding(14.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Icon(Icons.Filled.Key, null, tint = c.onTertiaryContainer)
                        Spacer(Modifier.width(10.dp))
                        Text(stringResource(R.string.ask_need_key), style = MaterialTheme.typography.bodyMedium, color = c.onTertiaryContainer)
                    }
                }
                if (chat.isEmpty()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(stringResource(R.string.ask_intro), style = MaterialTheme.typography.bodyLarge)
                        val tints = listOf(c.primaryContainer to c.onPrimaryContainer, c.secondaryContainer to c.onSecondaryContainer, c.tertiaryContainer to c.onTertiaryContainer, c.errorContainer to c.onErrorContainer)
                        suggestions.chunked(2).forEachIndexed { row, pair ->
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                pair.forEachIndexed { col, text ->
                                    val (bg, fg) = tints[(row * 2 + col) % tints.size]
                                    Card(
                                        onClick = { if (configured) vm.askAi(text) else input = text },
                                        colors = CardDefaults.cardColors(containerColor = bg),
                                        modifier = Modifier.weight(1f),
                                    ) {
                                        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Icon(Icons.Filled.AutoAwesome, null, tint = fg, modifier = Modifier.size(18.dp))
                                            Text(text, style = MaterialTheme.typography.bodyMedium, color = fg)
                                        }
                                    }
                                }
                                if (pair.size == 1) Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(chat, key = { it.id }) { m ->
                            if (m.role == "user") {
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                    Box(
                                        Modifier
                                            .widthIn(max = 520.dp)
                                            .clip(RoundedCornerShape(20.dp, 20.dp, 6.dp, 20.dp))
                                            .background(PixiGradient)
                                            .padding(horizontal = 16.dp, vertical = 11.dp),
                                    ) { FormattedText(m.text, color = Color.White) }
                                }
                            } else {
                                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                                    PixiAvatar(34, c.primary)
                                    Spacer(Modifier.width(8.dp))
                                    Box(
                                        Modifier
                                            .widthIn(max = 560.dp)
                                            .clip(RoundedCornerShape(20.dp, 20.dp, 20.dp, 6.dp))
                                            .background(c.surfaceContainerHigh)
                                            .padding(horizontal = 16.dp, vertical = 11.dp),
                                    ) { FormattedText(m.text) }
                                }
                            }
                        }
                        if (busy) item {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                PixiAvatar(34, c.primary)
                                Spacer(Modifier.width(8.dp))
                                Row(
                                    Modifier.clip(RoundedCornerShape(20.dp)).background(c.surfaceContainerHigh).padding(horizontal = 16.dp, vertical = 11.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                                    Spacer(Modifier.width(10.dp))
                                    Text(stringResource(R.string.ask_thinking), style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                        error?.let { e ->
                            item {
                                Text(
                                    e,
                                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(c.errorContainer).padding(12.dp),
                                    color = c.onErrorContainer,
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                        }
                    }
                }
            }
        }
        // Input bar
        Row(
            Modifier.fillMaxWidth().background(c.surfaceContainer).padding(12.dp).navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Row(Modifier.widthIn(max = 760.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(28.dp),
                    placeholder = { Text(stringResource(R.string.ask_hint)) },
                    maxLines = 4,
                )
                Spacer(Modifier.width(10.dp))
                FilledIconButton(
                    onClick = { if (input.isNotBlank()) { vm.askAi(input); input = "" } },
                    enabled = !busy && configured,
                    modifier = Modifier.size(52.dp),
                ) { Icon(Icons.AutoMirrored.Filled.Send, stringResource(R.string.ask_send)) }
            }
        }
    }
}

@Composable
private fun PixiAvatar(size: Int, bg: Color) {
    Box(Modifier.size(size.dp).clip(CircleShape).background(bg), contentAlignment = Alignment.Center) {
        Icon(Icons.Filled.AutoAwesome, null, tint = Color.White, modifier = Modifier.size((size * 0.55f).dp))
    }
}
