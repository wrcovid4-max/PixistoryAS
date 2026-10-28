package com.pixistory.ui.common

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker

enum class Posture { NORMAL, TABLETOP, BOOK }

/** Hinge rectangle in dp, relative to the window. */
data class HingeBounds(val left: Float, val top: Float, val width: Float, val height: Float)

/**
 * What the screen looks like right now. On a Pixel Fold the cover display is compact; unfolded it is
 * wide (two panes); half-opened with a horizontal hinge it is in tabletop posture, half-opened with
 * a vertical hinge it is in book posture.
 */
data class FoldState(
    val widthDp: Int,
    val heightDp: Int,
    val posture: Posture,
    val hinge: HingeBounds?,
) {
    val isWide: Boolean get() = widthDp >= 600
    val isTabletop: Boolean get() = posture == Posture.TABLETOP
    val isBook: Boolean get() = posture == Posture.BOOK
}

fun Context.findActivity(): Activity? {
    var c: Context? = this
    while (c is ContextWrapper) {
        if (c is Activity) return c
        c = c.baseContext
    }
    return null
}

@Composable
fun rememberFoldState(): FoldState {
    val context = LocalContext.current
    val config = LocalConfiguration.current
    val density = LocalDensity.current
    val activity = remember(context) { context.findActivity() }
    var feature by remember { mutableStateOf<FoldingFeature?>(null) }
    if (activity != null) {
        LaunchedEffect(activity) {
            WindowInfoTracker.getOrCreate(activity).windowLayoutInfo(activity).collect { info ->
                feature = info.displayFeatures.filterIsInstance<FoldingFeature>().firstOrNull()
            }
        }
    }
    val f = feature
    val posture = when {
        f != null && f.state == FoldingFeature.State.HALF_OPENED ->
            if (f.orientation == FoldingFeature.Orientation.HORIZONTAL) Posture.TABLETOP else Posture.BOOK
        else -> Posture.NORMAL
    }
    val hinge = f?.let {
        val b = it.bounds
        with(density) { HingeBounds(b.left.toDp().value, b.top.toDp().value, b.width().toDp().value, b.height().toDp().value) }
    }
    return FoldState(config.screenWidthDp, config.screenHeightDp, posture, hinge)
}
