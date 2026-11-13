@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.pixistory.ui.common

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.DevicesOther
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.TabletAndroid
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.pixistory.data.model.DeviceEntity
import com.pixistory.data.model.DeviceStatus
import com.pixistory.data.model.DeviceType
import com.pixistory.data.model.typeEnum
import com.pixistory.data.repo.FileStore
import com.pixistory.util.Currencies
import com.pixistory.util.PatchStatus
import com.pixistory.util.patchStatus

fun typeIcon(type: DeviceType): ImageVector = when (type) {
    DeviceType.PHONE -> Icons.Filled.PhoneAndroid
    DeviceType.FOLDABLE -> Icons.Filled.Devices
    DeviceType.TABLET -> Icons.Filled.TabletAndroid
    DeviceType.WATCH -> Icons.Filled.Watch
    DeviceType.EARBUDS -> Icons.Filled.Headphones
    DeviceType.TV -> Icons.Filled.Tv
    DeviceType.LAPTOP -> Icons.Filled.Laptop
    DeviceType.CAMERA -> Icons.Filled.CameraAlt
    DeviceType.ACCESSORY -> Icons.Filled.Usb
    DeviceType.OTHER -> Icons.Filled.DevicesOther
}

/** The emoji shown for a device without a photo. */
fun deviceEmoji(type: DeviceType): String = when (type) {
    DeviceType.PHONE -> "\uD83D\uDCF1"       // 📱
    DeviceType.FOLDABLE -> "\uD83D\uDCD6"    // 📖 opens like a book
    DeviceType.TABLET -> "\uD83D\uDCCB"      // 📋
    DeviceType.WATCH -> "\u231A"              // ⌚
    DeviceType.EARBUDS -> "\uD83C\uDFA7"     // 🎧
    DeviceType.TV -> "\uD83D\uDCFA"          // 📺
    DeviceType.LAPTOP -> "\uD83D\uDCBB"      // 💻
    DeviceType.CAMERA -> "\uD83D\uDCF7"      // 📷
    DeviceType.ACCESSORY -> "\uD83D\uDD0C"   // 🔌
    DeviceType.OTHER -> "\uD83D\uDCE6"       // 📦
}

fun parseColorOrNull(hex: String): Color? =
    if (hex.isBlank()) null else runCatching { Color(android.graphics.Color.parseColor(hex)) }.getOrNull()

/** The device colour options offered in the editor. */
val DeviceColors: List<Pair<String, String>> = listOf(
    "#1C1C1E" to "Black",
    "#F2F2F7" to "White",
    "#B8BCC4" to "Silver",
    "#6B7280" to "Gray",
    "#2563EB" to "Blue",
    "#7DD3FC" to "Sky blue",
    "#16A34A" to "Green",
    "#A7F3D0" to "Mint",
    "#FACC15" to "Yellow",
    "#F97316" to "Orange",
    "#DC2626" to "Red",
    "#F472B6" to "Pink",
    "#8B5CF6" to "Purple",
    "#C9A227" to "Gold",
)

@Composable
fun DeviceAvatar(device: DeviceEntity, files: FileStore, size: Dp, modifier: Modifier = Modifier) {
    val file = remember(device.photoPath, device.updatedAt) { files.resolve(device.photoPath) }
    val tint = parseColorOrNull(device.colorHex)
    Box(
        modifier
            .size(size)
            .clip(RoundedCornerShape(size / 4))
            .background(tint?.copy(alpha = 0.22f) ?: MaterialTheme.colorScheme.secondaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        if (file != null) {
            AsyncImage(
                model = file,
                contentDescription = device.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Text(deviceEmoji(device.typeEnum), fontSize = (size.value * 0.5f).sp)
        }
    }
}

@Composable
fun PatchChip(patch: String, modifier: Modifier = Modifier) {
    val status = patchStatus(patch)
    Row(
        modifier
            .clip(RoundedCornerShape(50))
            .background(status.color.copy(alpha = 0.16f))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(status.color))
        Spacer(Modifier.width(6.dp))
        Text(status.text(), style = MaterialTheme.typography.labelMedium, color = status.color)
    }
}

/**
 * Page header: a large, quiet title on the page background (no coloured bar). Detail screens pass
 * large = false for a compact title next to the back arrow.
 */
@Composable
fun PixiTopBar(
    title: @Composable () -> Unit,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    large: Boolean = true,
) {
    val c = MaterialTheme.colorScheme
    Row(
        Modifier
            .fillMaxWidth()
            .background(c.surface)
            .statusBarsPadding()
            .padding(start = if (large) 20.dp else 4.dp, end = 8.dp, top = if (large) 14.dp else 4.dp, bottom = if (large) 8.dp else 4.dp)
            .heightIn(min = 48.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        navigationIcon()
        androidx.compose.material3.ProvideTextStyle(
            if (large) MaterialTheme.typography.headlineMedium.copy(color = c.onSurface)
            else MaterialTheme.typography.titleLarge.copy(color = c.onSurface),
        ) {
            Box(Modifier.weight(1f).padding(start = if (large) 0.dp else 8.dp)) { title() }
        }
        Row(verticalAlignment = Alignment.CenterVertically) { actions() }
    }
}

/** A section: small muted title, then content on a white card with a hairline border. */
@Composable
fun SectionCard(title: String, modifier: Modifier = Modifier, accent: Color? = null, content: @Composable () -> Unit) {
    val c = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(20.dp)
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            title.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = accent ?: c.onSurfaceVariant,
            modifier = Modifier.padding(start = 6.dp),
        )
        Column(
            Modifier.fillMaxWidth().clip(shape).background(c.surfaceContainerLow).border(1.dp, c.outlineVariant, shape).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) { content() }
    }
}

/** Two-column list with hairline dividers. Rows whose value is blank are left out. */
@Composable
fun InfoTable(rows: List<Pair<String, String>>, modifier: Modifier = Modifier) {
    val shown = rows.filter { it.second.isNotBlank() }
    if (shown.isEmpty()) return
    val c = MaterialTheme.colorScheme
    Column(modifier.fillMaxWidth()) {
        shown.forEachIndexed { i, (k, v) ->
            if (i > 0) androidx.compose.material3.HorizontalDivider(color = c.outlineVariant, thickness = 0.5.dp)
            Row(Modifier.fillMaxWidth().padding(vertical = 10.dp), verticalAlignment = Alignment.Top) {
                Text(k, Modifier.weight(0.38f), style = MaterialTheme.typography.bodyMedium, color = c.onSurfaceVariant)
                Text(v, Modifier.weight(0.62f), style = MaterialTheme.typography.bodyMedium, color = c.onSurface)
            }
        }
    }
}

/** Small status label: tinted text on a soft tinted rounded rectangle. */
@Composable
fun StatusPill(text: String, color: Color, modifier: Modifier = Modifier) {
    Text(
        text,
        modifier = modifier.clip(RoundedCornerShape(8.dp)).background(color.copy(alpha = 0.12f)).padding(horizontal = 8.dp, vertical = 3.dp),
        style = MaterialTheme.typography.labelMedium,
        color = color,
    )
}

/** The app's filter chip: quiet when off, soft blue when on. */
@Composable
fun PixiChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
) {
    val c = MaterialTheme.colorScheme
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = label,
        modifier = modifier,
        leadingIcon = leadingIcon,
        shape = RoundedCornerShape(12.dp),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = c.surfaceContainerLow,
            labelColor = c.onSurfaceVariant,
            selectedContainerColor = c.primaryContainer,
            selectedLabelColor = c.onPrimaryContainer,
            selectedLeadingIconColor = c.onPrimaryContainer,
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true, selected = selected,
            borderColor = c.outlineVariant, selectedBorderColor = c.primary.copy(alpha = 0.35f),
        ),
    )
}

@Composable
fun EmptyState(icon: ImageVector, title: String, body: String, modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(56.dp), tint = MaterialTheme.colorScheme.outline)
        Spacer(Modifier.size(12.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
        Spacer(Modifier.size(4.dp))
        Text(body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    if (value.isBlank()) return
    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp), verticalAlignment = Alignment.Top) {
        Text(label, Modifier.width(120.dp), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun <T> DropdownField(
    label: String,
    selected: T,
    options: List<T>,
    optionLabel: @Composable (T) -> String,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }, modifier = modifier) {
        OutlinedTextField(
            value = optionLabel(selected),
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(optionLabel(option)) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
fun ChipRow(items: List<String>, onClick: (String) -> Unit, modifier: Modifier = Modifier) {
    FlowRow(modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items.forEach { AssistChip(onClick = { onClick(it) }, label = { Text(it) }) }
    }
}

/** Tiny **bold** and "- " bullet formatting for assistant replies. */
@Composable
fun FormattedText(text: String, modifier: Modifier = Modifier, color: Color = Color.Unspecified) {
    val annotated = remember(text) {
        androidx.compose.ui.text.buildAnnotatedString {
            text.lines().forEachIndexed { index, rawLine ->
                if (index > 0) append("\n")
                var line = rawLine.trimEnd()
                if (line.startsWith("- ") || line.startsWith("* ")) line = "•  " + line.drop(2)
                line = line.removePrefix("### ").removePrefix("## ").removePrefix("# ")
                var i = 0
                while (i < line.length) {
                    if (line.startsWith("**", i)) {
                        val end = line.indexOf("**", i + 2)
                        if (end > 0) {
                            pushStyle(androidx.compose.ui.text.SpanStyle(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold))
                            append(line.substring(i + 2, end))
                            pop()
                            i = end + 2
                            continue
                        }
                    }
                    append(line[i])
                    i++
                }
            }
        }
    }
    Text(annotated, modifier, color = color, style = MaterialTheme.typography.bodyMedium)
}

@Composable
fun statusColor(status: DeviceStatus): Color = when (status) {
    DeviceStatus.OWNED -> MaterialTheme.colorScheme.secondary
    DeviceStatus.WISHLIST -> MaterialTheme.colorScheme.tertiary
    DeviceStatus.SOLD -> MaterialTheme.colorScheme.outline
}

/** Currency chips (same list as CashMemer's receipt generator), styled like the language selector. */
@Composable
fun CurrencySwitcher(selected: String, onSelect: (String) -> Unit, modifier: Modifier = Modifier, scrollable: Boolean = false) {
    val options = remember(selected) { (listOf(selected) + Currencies.COMMON).filter { it.isNotBlank() }.distinct() }
    val chips: @Composable () -> Unit = {
        options.forEach { code ->
            PixiChip(
                selected = code == selected,
                onClick = { onSelect(code) },
                label = { Text("${Currencies.flagOf(code)} $code".trim()) },
            )
        }
    }
    if (scrollable) {
        Row(modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) { chips() }
    } else {
        FlowRow(modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) { chips() }
    }
}

/** Fades, scales up slightly and slides in each time [key] changes: a soft arrival for a freshly opened page. */
@Composable
fun Entrance(key: Any?, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val progress = remember { androidx.compose.animation.core.Animatable(0f) }
    androidx.compose.runtime.LaunchedEffect(key) {
        progress.snapTo(0f)
        progress.animateTo(1f, androidx.compose.animation.core.tween(420, easing = androidx.compose.animation.core.FastOutSlowInEasing))
    }
    Box(
        modifier.graphicsLayer {
            val p = progress.value
            alpha = p
            val scale = 0.94f + 0.06f * p
            scaleX = scale
            scaleY = scale
            translationY = (1f - p) * 56f
        },
    ) { content() }
}
