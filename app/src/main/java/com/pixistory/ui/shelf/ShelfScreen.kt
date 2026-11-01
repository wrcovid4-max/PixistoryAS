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
import androidx.compose.animation.animateColorAsState
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

/**
 * Shelf mode: a glowing night-stand display. A big clock, a swipeable stack of glass cards (one per
 * device you own) with a colour glow taken from the device, and a lit shelf of thumbnails below.
 * It keeps the screen on. On a half-folded Pixel Fold (tabletop posture) the clock and card sit above the
 * hinge and the shelf and controls below it. Cards drift gently with the phone's tilt unless animations
 * are turned off.
 */
@Composable
fun ShelfScreen(vm: AppViewModel, fold: FoldState, onClose: () -> Unit) {
    BackHandler(onBack = onClose)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val all by vm.devices.collectAsStateWithLifecycle()
    val devices = remember(all) { all.filter { it.statusEnum == DeviceStatus.OWNED } }
    val pager = rememberPagerState(pageCount = { devices.size })
    var playing by remember { mutableStateOf(true) }
    var now by remember { mutableStateOf(LocalDateTime.now()) }
    var tiltX by remember { mutableFloatStateOf(0f) }
    var tiltY by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) { while (true) { now = LocalDateTime.now(); delay(15_000) } }
    LaunchedEffect(playing, devices.size) {
        while (true) {
            delay(8000)
            if (playing && devices.size > 1 && !pager.isScrollInProgress) pager.animateScrollToPage((pager.currentPage + 1) % devices.size)
        }
    }
    DisposableEffect(Unit) {
        val window = context.findActivity()?.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose { window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) }
    }
    DisposableEffect(Unit) {
        val animationsOff = Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
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
    val glow by animateColorAsState(parseColorOrNull(current?.colorHex.orEmpty()) ?: Color(0xFF4F8DF7), label = "glow")

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
                contentPadding = PaddingValues(horizontal = if (fold.isWide) 120.dp else 40.dp),
                pageSpacing = 16.dp,
            ) { page ->
                val offset = abs((pager.currentPage - page) + pager.currentPageOffsetFraction).coerceIn(0f, 1f)
                GlassCard(
                    devices[page], vm,
                    Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            val scale = 1f - 0.12f * offset
                            scaleX = scale; scaleY = scale; alpha = 1f - 0.45f * offset
                            translationX = tiltX * 5f; translationY = tiltY * 5f
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
            // Half-folded Pixel Fold: everything you read above the hinge, everything you touch below it.
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
            color = Color.White, fontSize = if (compact) 52.sp else 76.sp, fontWeight = FontWeight.Light,
        )
        Text(now.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)), color = Color(0xFFB9C7DE), fontSize = 15.sp)
        Spacer(Modifier.height(4.dp))
        Text(stringResource(R.string.shelf_count, count), color = Color(0xFF8FA2BF), fontSize = 12.sp)
    }
}

@Composable
private fun GlassCard(d: DeviceEntity, vm: AppViewModel, modifier: Modifier) {
    val shape = RoundedCornerShape(36.dp)
    Column(
        modifier
            .clip(shape)
            .background(Brush.linearGradient(listOf(Color.White.copy(alpha = 0.14f), Color.White.copy(alpha = 0.05f))))
            .border(1.dp, Color.White.copy(alpha = 0.20f), shape)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        DeviceAvatar(d, vm.container.files, 150.dp)
        Spacer(Modifier.height(18.dp))
        Text(d.name.ifBlank { d.model }, color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
        val sub = listOfNotNull(
            d.typeEnum.text(),
            if (d.apiLevel != null || d.androidVersion.isNotBlank()) AndroidVersions.label(d.apiLevel, d.androidVersion) else null,
            d.releaseYear?.toString(),
        ).joinToString("  •  ")
        Spacer(Modifier.height(6.dp))
        Text(sub, color = Color(0xFFB9C7DE), fontSize = 15.sp, textAlign = TextAlign.Center)
        if (d.securityPatch.isNotBlank()) {
            Spacer(Modifier.height(14.dp))
            PatchChip(d.securityPatch)
        }
    }
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
        // the lit shelf board
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
