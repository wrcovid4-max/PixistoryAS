@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.pixistory.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.pixistory.data.model.DeviceEntity
import com.pixistory.data.model.DeviceStatus
import com.pixistory.data.model.DeviceType
import com.pixistory.data.model.typeEnum
import com.pixistory.data.repo.FileStore
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
            Icon(
                typeIcon(device.typeEnum),
                contentDescription = null,
                modifier = Modifier.size(size * 0.5f),
                tint = tint ?: MaterialTheme.colorScheme.onSecondaryContainer,
            )
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

/** Top bar tinted with the brand colour. */
@Composable
fun PixiTopBar(
    title: @Composable () -> Unit,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
) {
    val c = MaterialTheme.colorScheme
    TopAppBar(
        title = title,
        navigationIcon = navigationIcon,
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = c.primaryContainer,
            titleContentColor = c.onPrimaryContainer,
            navigationIconContentColor = c.onPrimaryContainer,
            actionIconContentColor = c.onPrimaryContainer,
        ),
    )
}

/** A card with a coloured accent bar; the colour is picked from the title unless given. */
@Composable
fun SectionCard(title: String, modifier: Modifier = Modifier, accent: Color? = null, content: @Composable () -> Unit) {
    val c = MaterialTheme.colorScheme
    val palette = listOf(c.primary, c.secondary, c.tertiary, c.error)
    val color = accent ?: palette[(title.hashCode() and 0x7fffffff) % palette.size]
    Card(
        modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = c.surfaceContainerLow),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.width(5.dp).height(22.dp).clip(RoundedCornerShape(3.dp)).background(color))
                Spacer(Modifier.width(10.dp))
                Text(title, style = MaterialTheme.typography.titleMedium, color = color)
            }
            content()
        }
    }
}

/** Two-column table with striped rows. Rows whose value is blank are left out. */
@Composable
fun InfoTable(rows: List<Pair<String, String>>, modifier: Modifier = Modifier) {
    val shown = rows.filter { it.second.isNotBlank() }
    if (shown.isEmpty()) return
    val c = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(12.dp)
    Column(modifier.fillMaxWidth().clip(shape).border(1.dp, c.outlineVariant, shape)) {
        shown.forEachIndexed { i, (k, v) ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(if (i % 2 == 0) c.surfaceContainerHigh.copy(alpha = 0.7f) else c.surfaceContainerLowest.copy(alpha = 0.35f))
                    .padding(horizontal = 12.dp, vertical = 9.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Text(k, Modifier.weight(0.38f), style = MaterialTheme.typography.labelLarge, color = c.primary)
                Text(v, Modifier.weight(0.62f), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

/** Small coloured status pill. */
@Composable
fun StatusPill(text: String, color: Color, modifier: Modifier = Modifier) {
    Text(
        text,
        modifier = modifier.clip(RoundedCornerShape(50)).background(color.copy(alpha = 0.16f)).padding(horizontal = 10.dp, vertical = 3.dp),
        style = MaterialTheme.typography.labelMedium,
        color = color,
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
