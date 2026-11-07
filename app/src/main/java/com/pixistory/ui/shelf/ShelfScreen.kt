@file:OptIn(ExperimentalMaterial3Api::class)

package com.pixistory.ui.shelf

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.provider.Settings
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pixistory.R
import com.pixistory.data.model.DeviceEntity
import com.pixistory.data.model.DeviceStatus
import com.pixistory.data.model.statusEnum
import com.pixistory.data.model.typeEnum
import com.pixistory.ui.AppViewModel
import com.pixistory.ui.common.*
import com.pixistory.util.AndroidVersions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.math.abs

private const val FACE_SUMMARY = 0
private const val FACE_DETAILS = 1
private const val FACE_SPECS = 2
private const val FACE_STORY = 3
private const val FACE_OWNERSHIP = 4

/**
 * Shelf mode: a glowing night-stand display. A big clock and a swipeable stack of glass cards, one per
 * device you own. Each card shows its device, then animates into its details, tech specs or backstory (picked
 * at random) for a few seconds, and then the shelf moves on to the next device with a tiny wobble. Tap the
 * card or its icon to open the device. The card can take the device's colour (a setting). The screen stays on.
 * On a half-folded Pixel Fold (tabletop posture) the clock and card sit above the hinge and the controls below.
 */
@Composable
fun ShelfScreen(vm: AppViewModel, fold: FoldState, onOpenDevice: (String) -> Unit, onClose: () -> Unit) {
    BackHandler(onBack = onClose)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val all by vm.devices.collectAsStateWithLifecycle()
    val settings by vm.settings.collectAsStateWithLifecycle()
    val devices = remember(all) { all.filter { it.statusEnum == DeviceStatus.OWNED } }
    val pager = rememberPagerState(pageCount = { devices.size })
    var playing by remember { mutableStateOf(true) }
    var now by remember { mutableStateOf(LocalDateTime.now()) }
    var tiltX by remember { mutableFloatStateOf(0f) }
    var tiltY by remember { mutableFloatStateOf(0f) }
    var face by remember { mutableIntStateOf(FACE_SUMMARY) }
    val wobble = remember { Animatable(0f) }
    val animationsOff = remember { Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f }
    val knowledge = vm.container.knowledge

    LaunchedEffect(Unit) { while (true) { now = LocalDateTime.now(); delay(15_000) } }

    // Each device: show it, animate into one random face for a few seconds, then move on to the next device.
    // Keyed on the *settled* page so a scroll that is still moving is never cancelled half way.
    LaunchedEffect(pager.settledPage, playing, devices.size) {
        face = FACE_SUMMARY
        if (devices.isEmpty()) return@LaunchedEffect
        // a tiny wobble as the device arrives
        if (!animationsOff) {
            for (t in listOf(2.4f, -2.0f, 1.3f, -0.7f, 0f)) wobble.animateTo(t, tween(110))
        }
        if (!playing) return@LaunchedEffect
        delay(1600)
        val d = devices.getOrNull(pager.settledPage) ?: return@LaunchedEffect
        val known = knowledge.match(d.name, d.model)
        val options = buildList {
            add(FACE_DETAILS)
            add(FACE_OWNERSHIP)
            if (known != null) { add(FACE_SPECS); add(FACE_STORY) }
        }
        face = options.random()
        delay(6000)
        if (devices.size > 1) pager.animateScrollToPage((pager.settledPage + 1) % devices.size)
    }
    DisposableEffect(Unit) {
        val window = context.findActivity()?.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose { window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) }
    }
    DisposableEffect(Unit) {
        val sm = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val listener = object : SensorEventListener {
            override fun onSensorChanged(e: SensorEvent) {
                tiltX = tiltX * 0.92f + (-e.values[0]) * 0.08f
                tiltY = tiltY * 0.92f + e.values[1] * 0.08f
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        if (!animationsOff && sensor != null) sm.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)
        onDispose { sm.unregisterListener(listener) }
    }

    val current = devices.getOrNull(pager.currentPage)
    val deviceColor = parseColorOrNull(current?.colorHex.orEmpty())
    val glow by animateColorAsState(deviceColor ?: Color(0xFF4F8DF7), label = "glow")

    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF060B16), Color(0xFF111F45), Color(0xFF291A50))))
            .background(Brush.radialGradient(listOf(glow.copy(alpha = 0.40f), Color.Transparent), radius = 1100f))
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        if (devices.isEmpty()) {
            Column(Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text(stringResource(R.string.shelf_empty), color = Color.White, textAlign = TextAlign.Center, fontSize = 18.sp)
                Spacer(Modifier.height(20.dp))
                FilledTonalButton(onClick = onClose) { Text(stringResource(R.string.cancel)) }
            }
            return@Box
        }

        val clock: @Composable () -> Unit = { ClockBlock(now, devices.size, compact = fold.isTabletop) }
        val pagerBlock: @Composable (Modifier) -> Unit = { mod ->
            HorizontalPager(
                state = pager,
                modifier = mod,
                contentPadding = PaddingValues(horizontal = if (fold.isWide) 120.dp else 32.dp),
                pageSpacing = 16.dp,
            ) { page ->
                val offset = abs((pager.currentPage - page) + pager.currentPageOffsetFraction).coerceIn(0f, 1f)
                val isCurrent = page == pager.currentPage
                GlassCard(
                    device = devices[page],
                    vm = vm,
                    face = if (isCurrent) face else FACE_SUMMARY,
                    tint = if (settings.shelfMatchColor) parseColorOrNull(devices[page].colorHex) else null,
                    onOpen = { onOpenDevice(devices[page].id) },
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            val scale = 1f - 0.12f * offset
                            scaleX = scale; scaleY = scale; alpha = 1f - 0.45f * offset
                            translationX = tiltX * 5f; translationY = tiltY * 5f
                            rotationZ = if (isCurrent) wobble.value else 0f
                        },
                )
            }
        }
        val shelf: @Composable () -> Unit = {
            ShelfRow(devices, pager.currentPage, glow, vm) { i -> scope.launch { pager.animateScrollToPage(i) } }
        }
        val controls: @Composable () -> Unit = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                RoundButton(Icons.AutoMirrored.Filled.NavigateBefore, stringResource(R.string.shelf_prev)) {
                    scope.launch { pager.animateScrollToPage((pager.currentPage - 1 + devices.size) % devices.size) }
                }
                RoundButton(if (playing) Icons.Filled.Pause else Icons.Filled.PlayArrow, stringResource(if (playing) R.string.shelf_pause else R.string.shelf_play), big = true) { playing = !playing }
                RoundButton(Icons.AutoMirrored.Filled.NavigateNext, stringResource(R.string.shelf_next)) {
                    scope.launch { pager.animateScrollToPage((pager.currentPage + 1) % devices.size) }
                }
            }
        }

        if (fold.isTabletop && fold.hinge != null) {
            Column(Modifier.fillMaxSize()) {
                Column(Modifier.weight(1f).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    clock()
                    pagerBlock(Modifier.weight(1f).fillMaxWidth())
                }
                Column(Modifier.weight(1f).fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceEvenly) {
                    shelf()
                    controls()
                }
            }
        } else {
            Column(Modifier.fillMaxSize().padding(vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp), horizontalArrangement = Arrangement.End) {
                    IconButton(onClick = onClose) { Icon(Icons.Filled.Close, stringResource(R.string.cancel), tint = Color.White.copy(alpha = 0.8f)) }
                }
                clock()
                pagerBlock(Modifier.weight(1f).fillMaxWidth().padding(vertical = 12.dp))
                shelf()
                Spacer(Modifier.height(12.dp))
                controls()
            }
        }
        if (fold.isTabletop) {
            IconButton(onClick = onClose, modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)) {
                Icon(Icons.Filled.Close, stringResource(R.string.cancel), tint = Color.White.copy(alpha = 0.8f))
            }
        }
    }
}

@Composable
private fun ClockBlock(now: LocalDateTime, count: Int, compact: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            now.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)),
            color = Color.White, fontSize = if (compact) 52.sp else 72.sp, fontWeight = FontWeight.Light,
        )
        Text(now.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)), color = Color(0xFFB9C7DE), fontSize = 15.sp)
        Spacer(Modifier.height(4.dp))
        Text(stringResource(R.string.shelf_count, count), color = Color(0xFF8FA2BF), fontSize = 12.sp)
    }
}

/** The whole card is tappable (opens the device). Its content animates between the summary and one other face. */
@Composable
private fun GlassCard(device: DeviceEntity, vm: AppViewModel, face: Int, tint: Color?, onOpen: () -> Unit, modifier: Modifier) {
    val shape = RoundedCornerShape(36.dp)
    val lang = currentLang()
    val fill = if (tint != null) Brush.linearGradient(listOf(tint.copy(alpha = 0.50f), tint.copy(alpha = 0.16f)))
    else Brush.linearGradient(listOf(Color.White.copy(alpha = 0.14f), Color.White.copy(alpha = 0.05f)))
    val edge = tint?.copy(alpha = 0.65f) ?: Color.White.copy(alpha = 0.20f)
    val known = remember(device.name, device.model) { vm.container.knowledge.match(device.name, device.model) }
    Column(
        modifier
            .clip(shape)
            .background(fill)
            .border(1.dp, edge, shape)
            .clickable(onClick = onOpen)
            .padding(22.dp)
            .animateContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        DeviceAvatar(device, vm.container.files, if (face == FACE_SUMMARY) 150.dp else 96.dp, Modifier.clickable(onClick = onOpen))
        Spacer(Modifier.height(14.dp))
        Text(device.name.ifBlank { device.model }, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        AnimatedContent(
            targetState = face,
            transitionSpec = { fadeIn(tween(450)) togetherWith fadeOut(tween(250)) },
            label = "shelf-face",
        ) { f ->
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                when (f) {
                    FACE_DETAILS -> {
                        FaceTitle(stringResource(R.string.section_details))
                        val rows = listOfNotNull(
                            listOf(device.brand, device.model).filter { it.isNotBlank() }.joinToString(" ").takeIf { it.isNotBlank() && it != device.name },
                            device.typeEnum.text(),
                            if (device.apiLevel != null || device.androidVersion.isNotBlank()) AndroidVersions.label(device.apiLevel, device.androidVersion) else null,
                            device.releaseYear?.let { stringResource(R.string.f_released) + ": " + it },
                            device.securityPatch.takeIf { it.isNotBlank() }?.let { stringResource(R.string.f_patch) + ": " + it },
                            listOfNotNull(device.storageGb?.let { "$it GB" }, device.ramGb?.let { "$it GB RAM" }).joinToString(" • ").takeIf { it.isNotBlank() },
                            device.colorName.takeIf { it.isNotBlank() },
                        )
                        rows.take(6).forEach { FaceLine(it) }
                    }
                    FACE_OWNERSHIP -> {
                        FaceTitle(stringResource(R.string.shelf_ownership))
                        val days = ((System.currentTimeMillis() - device.createdAt) / 86_400_000L).toInt().coerceAtLeast(0)
                        FaceLine(stringResource(R.string.shelf_added, com.pixistory.util.formatDate(device.createdAt)))
                        FaceLine(stringResource(R.string.shelf_owned_days, days))
                        device.price?.let { FaceLine(stringResource(R.string.f_price) + ": " + com.pixistory.util.formatMoney(it, device.currency)) }
                        device.groupName.takeIf { it.isNotBlank() }?.let { FaceLine(it) }
                        device.notes.takeIf { it.isNotBlank() }?.let { FaceLine(it.take(90)) }
                    }
                    FACE_SPECS -> {
                        FaceTitle(stringResource(R.string.pixel_specs))
                        known?.specs?.entries?.take(5)?.forEach { (k, v) -> FaceLine("${specLabel(k)}: $v") }
                    }
                    FACE_STORY -> {
                        FaceTitle(stringResource(R.string.shelf_story))
                        val story = known?.let { vm.container.knowledge.gen(it.gen)?.history?.get(lang) }.orEmpty()
                        Text(firstSentences(story, 330), color = Color.White.copy(alpha = 0.92f), fontSize = 14.sp, textAlign = TextAlign.Center, maxLines = 9, overflow = TextOverflow.Ellipsis)
                    }
                    else -> {
                        val sub = listOfNotNull(
                            device.typeEnum.text(),
                            if (device.apiLevel != null || device.androidVersion.isNotBlank()) AndroidVersions.label(device.apiLevel, device.androidVersion) else null,
                            device.releaseYear?.toString(),
                        ).joinToString("  •  ")
                        Text(sub, color = Color(0xFFC9D5EA), fontSize = 15.sp, textAlign = TextAlign.Center)
                        if (device.securityPatch.isNotBlank()) PatchChip(device.securityPatch)
                    }
                }
            }
        }
    }
}

@Composable
private fun FaceTitle(text: String) {
    Text(text.uppercase(), color = Color.White.copy(alpha = 0.75f), fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
}

@Composable
private fun FaceLine(text: String) {
    Text(text, color = Color.White.copy(alpha = 0.95f), fontSize = 15.sp, textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)
}

@Composable
private fun specLabel(key: String): String = stringResource(
    when (key) {
        "chip" -> R.string.spec_chip
        "ram" -> R.string.f_ram
        "storage" -> R.string.f_storage
        "display" -> R.string.spec_display
        "battery" -> R.string.spec_battery
        "camera" -> R.string.spec_camera
        "protection" -> R.string.spec_protection
        else -> R.string.spec_price
    },
)

private fun firstSentences(text: String, max: Int): String {
    if (text.length <= max) return text
    val cut = text.take(max)
    val end = cut.lastIndexOfAny(charArrayOf('.', '۔'))
    return if (end > 60) cut.substring(0, end + 1) else cut.trimEnd() + "…"
}

@Composable
private fun ShelfRow(devices: List<DeviceEntity>, selected: Int, glow: Color, vm: AppViewModel, onPick: (Int) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            itemsIndexed(devices) { i, d ->
                val on = i == selected
                Box(
                    Modifier
                        .size(if (on) 64.dp else 52.dp)
                        .then(if (on) Modifier.border(2.dp, glow, RoundedCornerShape(18.dp)) else Modifier)
                        .clickable { onPick(i) },
                    contentAlignment = Alignment.Center,
                ) { DeviceAvatar(d, vm.container.files, if (on) 60.dp else 48.dp) }
            }
        }
        Box(
            Modifier
                .padding(top = 6.dp)
                .fillMaxWidth(0.9f)
                .height(5.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Brush.horizontalGradient(listOf(Color.Transparent, glow.copy(alpha = 0.9f), Color.Transparent))),
        )
    }
}

@Composable
private fun RoundButton(icon: androidx.compose.ui.graphics.vector.ImageVector, description: String, big: Boolean = false, onClick: () -> Unit) {
    Box(
        Modifier
            .size(if (big) 64.dp else 52.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = if (big) 0.22f else 0.12f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) { Icon(icon, description, tint = Color.White, modifier = Modifier.size(if (big) 32.dp else 26.dp)) }
}
