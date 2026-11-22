package com.pixistory.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixistory.R
import kotlinx.coroutines.delay

/**
 * The one splash screen: the logo with "(C) WR COVID" under it, on the same dark blue as the system
 * start-up window (which now shows nothing of its own), so opening the app is a single screen.
 * Tap it to skip.
 */
@Composable
fun LaunchPopup(onDone: () -> Unit) {
    var shown by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(if (shown) 1f else 0f, tween(400), label = "launch-fade")
    LaunchedEffect(Unit) {
        shown = true
        delay(2000)
        onDone()
    }
    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF172A43))
            .clickable { onDone() }
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
            .alpha(alpha),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painterResource(R.drawable.pixistory_logo),
            contentDescription = stringResource(R.string.app_name),
            modifier = Modifier.size(200.dp).clip(RoundedCornerShape(44.dp)),
        )
        Spacer(Modifier.height(20.dp))
        Text(
            stringResource(R.string.splash_credit),
            color = Color(0xFFB9C7DE),
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
        )
    }
}
