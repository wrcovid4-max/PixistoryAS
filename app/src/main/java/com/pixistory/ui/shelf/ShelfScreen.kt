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
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pixistory.R
import com.pixistory.data.model.DeviceStatus
import com.pixistory.data.model.DeviceEntity
import com.pixistory.data.model.statusEnum
import com.pixistory.ui.AppViewModel
import com.pixistory.ui.common.*
import com.pixistory.util.AndroidVersions
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * Shelf mode: a clock and a slowly rotating display of your devices that keeps the screen on.
 * On a half-folded Pixel Fold (tabletop posture) the clock and device sit above the hinge and the
 * controls below it. The cards tilt gently with the phone unless animations are turned off.
 */
@Composable
fun ShelfScreen(vm: AppViewModel, fold: FoldState, onClose: () -> Unit) {
    BackHandler(onBack = onClose)
    val context = LocalContext.current
    val all by vm.devices.collectAsStateWithLifecycle()
    val devices = remember(all) { all.filter { it.statusEnum == DeviceStatus.OWNED } }
    var index by remember { mutableIntStateOf(0) }
    var now by remember { mutableStateOf(LocalDateTime.now()) }
    var tiltX by remember { mutableFloatStateOf(0f) }
    var tiltY by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(devices.size) { index = 0 }
    LaunchedEffect(devices.size) {
        while (true) {
            delay(7000)
            if (devices.isNotEmpty()) index = (index + 1) % devices.size
        }
    }
    LaunchedEffect(Unit) { while (true) { now = LocalDateTime.now(); delay(15_000) } }

    // Keep the screen awake while the shelf is open.
    DisposableEffect(Unit) {
        val window = context.findActivity()?.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose { window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) }
    }
    // Gentle tilt from the accelerometer, skipped when the user has turned animations off.
    DisposableEffect(Unit) {
        val animationsOff = Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
        val sm = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val listener = object : SensorEventListener {
            override fun onSensorChanged(e: SensorEvent) {
                tiltX = tiltX * 0.9f + (-e.values[0]) * 0.1f
                tiltY = tiltY * 0.9f + (e.values[1]) * 0.1f
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        if (!animationsOff && sensor != null) sm.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)
        onDispose { sm.unregisterListener(listener) }
    }

    val clock: @Composable () -> Unit = {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(now.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)), color = Color.White, fontSize = 64.sp)
            Text(now.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)), color = Color(0xFFB9C7DE), fontSize = 16.sp)
        }
    }
    val card: @Composable (Modifier) -> Unit = { mod ->
        val d = devices.getOrNull(index)
        Crossfade(targetState = d, label = "shelf", modifier = mod) { dev ->
            if (dev == null) {
                Text(stringResource(R.string.shelf_empty), color = Color.White, textAlign = TextAlign.Center)
            } else {
                Column(
                    Modifier.graphicsLayer { translationX = tiltX * 6f; translationY = tiltY * 6f },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    DeviceAvatar(dev, vm.container.files, 168.dp)
                    Text(dev.name, color = Color.White, fontSize = 24.sp, textAlign = TextAlign.Center)
                    val sub = listOfNotNull(
                        if (dev.apiLevel != null || dev.androidVersion.isNotBlank()) AndroidVersions.label(dev.apiLevel, dev.androidVersion) else null,
                        dev.releaseYear?.toString(),
                    ).joinToString(" • ")
                    if (sub.isNotBlank()) Text(sub, color = Color(0xFFB9C7DE), fontSize = 15.sp)
                    if (dev.securityPatch.isNotBlank()) PatchChip(dev.securityPatch)
                }
            }
        }
    }
    val controls: @Composable () -> Unit = {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            IconButton(onClick = { if (devices.isNotEmpty()) index = (index - 1 + devices.size) % devices.size }) {
                Icon(Icons.AutoMirrored.Filled.NavigateBefore, stringResource(R.string.shelf_prev), tint = Color.White)
            }
            Text("${if (devices.isEmpty()) 0 else index + 1} / ${devices.size}", color = Color.White)
            IconButton(onClick = { if (devices.isNotEmpty()) index = (index + 1) % devices.size }) {
                Icon(Icons.AutoMirrored.Filled.NavigateNext, stringResource(R.string.shelf_next), tint = Color.White)
            }
            IconButton(onClick = onClose) { Icon(Icons.Filled.Close, stringResource(R.string.cancel), tint = Color.White) }
        }
    }

    Box(Modifier.fillMaxSize().background(Color(0xFF0B1424)).statusBarsPadding().navigationBarsPadding()) {
        val hinge = fold.hinge
        if (fold.isTabletop && hinge != null) {
            // Tabletop: everything above the hinge, controls on the lower half.
            Column(Modifier.fillMaxSize()) {
                Column(Modifier.weight(1f).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceEvenly) {
                    clock()
                    card(Modifier)
                }
                Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) { controls() }
            }
        } else {
            Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {
                clock()
                card(Modifier)
                controls()
            }
        }
    }
}
