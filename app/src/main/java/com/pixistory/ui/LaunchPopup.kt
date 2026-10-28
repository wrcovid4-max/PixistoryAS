package com.pixistory.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixistory.R
import com.pixistory.ui.common.currentLang
import com.pixistory.util.AppLanguage
import kotlinx.coroutines.delay

/** The pop-up shown when the app opens: logo, name and copyright, with a quick English / Urdu choice. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaunchPopup(onDone: () -> Unit) {
    var shown by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(if (shown) 1f else 0f, tween(500), label = "launch-fade")
    val lang = currentLang()
    LaunchedEffect(Unit) {
        shown = true
        delay(3200)
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
        Spacer(Modifier.height(24.dp))
        Text(stringResource(R.string.app_name), color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        Text(
            stringResource(R.string.tagline),
            color = Color(0xFFB9C7DE),
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(36.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FilterChip(
                selected = lang == "en",
                onClick = { onDone(); AppLanguage.set("en") },
                label = { Text("English") },
                colors = FilterChipDefaults.filterChipColors(labelColor = Color.White, selectedLabelColor = Color(0xFF172A43), selectedContainerColor = Color.White),
            )
            FilterChip(
                selected = lang == "ur",
                onClick = { onDone(); AppLanguage.set("ur") },
                label = { Text("اردو") },
                colors = FilterChipDefaults.filterChipColors(labelColor = Color.White, selectedLabelColor = Color(0xFF172A43), selectedContainerColor = Color.White),
            )
        }
        Spacer(Modifier.height(40.dp))
        Text(stringResource(R.string.copyright), color = Color(0xFF8FA2BF), fontSize = 13.sp, textAlign = TextAlign.Center)
        Spacer(Modifier.height(4.dp))
        Text(stringResource(R.string.tap_to_continue), color = Color(0xFF6F819C), fontSize = 11.sp)
    }
}
