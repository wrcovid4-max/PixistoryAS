package com.pixistory

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pixistory.ui.PixistoryRoot
import com.pixistory.ui.theme.PixistoryTheme

class MainActivity : AppCompatActivity() {
    private var pendingAction by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (savedInstanceState == null) pendingAction = intent?.action
        val container = (application as PixistoryApp).container
        setContent {
            val settings by container.settings.state.collectAsStateWithLifecycle()
            PixistoryTheme(settings.theme) {
                PixistoryRoot(container, pendingAction) { pendingAction = null }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        pendingAction = intent.action
    }
}
