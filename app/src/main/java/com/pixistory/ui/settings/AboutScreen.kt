@file:OptIn(ExperimentalMaterial3Api::class)

package com.pixistory.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import com.pixistory.ui.common.PixiTopBar
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pixistory.BuildConfig
import com.pixistory.R
import com.pixistory.ui.common.SectionCard

@Composable
fun AboutScreen(onBack: () -> Unit) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current
    Surface(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            PixiTopBar(
                title = { Text(stringResource(R.string.about_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back)) }
                },
            )
            Column(
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .navigationBarsPadding()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Image(
                    painterResource(R.drawable.pixistory_logo),
                    contentDescription = null,
                    modifier = Modifier.size(132.dp).clip(RoundedCornerShape(30.dp)),
                )
                Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineMedium)
                Text(
                    stringResource(R.string.about_version, BuildConfig.VERSION_NAME),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(stringResource(R.string.tagline), textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyLarge)

                Column(Modifier.widthIn(max = 640.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    SectionCard(stringResource(R.string.about_what_title)) {
                        Text(stringResource(R.string.about_what_body), style = MaterialTheme.typography.bodyMedium)
                    }
                    SectionCard(stringResource(R.string.about_features_title)) {
                        Text(stringResource(R.string.about_features_body), style = MaterialTheme.typography.bodyMedium)
                    }
                    SectionCard(stringResource(R.string.about_data_title)) {
                        Text(stringResource(R.string.about_data_body), style = MaterialTheme.typography.bodyMedium)
                    }
                    SectionCard(stringResource(R.string.about_developer_title)) {
                        Text(stringResource(R.string.about_developer_body), style = MaterialTheme.typography.bodyMedium)
                        OutlinedButton(
                            onClick = {
                                val mail = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + context.getString(R.string.about_email)))
                                runCatching { context.startActivity(mail) }
                            },
                            modifier = Modifier.fillMaxWidth(),
                        ) { Text(stringResource(R.string.about_email)) }
                        OutlinedButton(
                            onClick = {
                                val view = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/wrcovid4-max/PixistoryAS"))
                                runCatching { context.startActivity(view) }
                            },
                            modifier = Modifier.fillMaxWidth(),
                        ) { Text(stringResource(R.string.about_source)) }
                    }
                    SectionCard(stringResource(R.string.about_libs_title)) {
                        Text(stringResource(R.string.about_libs_body), style = MaterialTheme.typography.bodySmall)
                    }
                    Text(
                        stringResource(R.string.about_trademark),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Text(
                        stringResource(R.string.copyright),
                        style = MaterialTheme.typography.labelLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}
