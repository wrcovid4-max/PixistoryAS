@file:OptIn(ExperimentalLayoutApi::class)

package com.pixistory.ui.devices

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pixistory.R
import com.pixistory.data.pixel.PixelKnowledge
import com.pixistory.data.pixel.PixelModel
import com.pixistory.ui.common.InfoRow
import com.pixistory.ui.common.SectionCard

/** Background, tech specs, what was new, known faults and received Pixel Drops for a Pixel in the collection. */
@Composable
fun PixelInsights(model: PixelModel, knowledge: PixelKnowledge, lang: String) {
    val gen = remember(model.gen) { knowledge.gen(model.gen) }
    val drops = remember(model.id) { knowledge.dropsFor(model.id) }

    if (gen != null) {
        SectionCard(stringResource(R.string.pixel_background, gen.name.get(lang))) {
            Text(gen.history.get(lang), style = MaterialTheme.typography.bodyMedium)
        }
    }

    SectionCard(stringResource(R.string.pixel_specs)) {
        val labels = mapOf(
            "chip" to R.string.spec_chip, "ram" to R.string.f_ram, "storage" to R.string.f_storage,
            "display" to R.string.spec_display, "battery" to R.string.spec_battery, "camera" to R.string.spec_camera,
            "protection" to R.string.spec_protection, "price" to R.string.spec_price,
        )
        model.specs.forEach { (k, v) -> InfoRow(labels[k]?.let { stringResource(it) } ?: k, v) }
        InfoRow(stringResource(R.string.spec_released), model.released)
        if (model.launchApi > 0) InfoRow(stringResource(R.string.spec_launch_android), "API ${model.launchApi}")
        InfoRow(stringResource(R.string.spec_support), model.supportEnds)
    }

    if (gen != null && gen.features.isNotEmpty()) {
        SectionCard(stringResource(R.string.pixel_new_in_gen)) {
            gen.features.forEach { f -> Text("•  " + f.get(lang), style = MaterialTheme.typography.bodyMedium) }
        }
    }

    SectionCard(stringResource(R.string.pixel_faults)) {
        if (model.faults.isEmpty()) {
            Text(stringResource(R.string.pixel_faults_none), style = MaterialTheme.typography.bodyMedium)
        } else {
            model.faults.forEach { f ->
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    val tag = if (f.official) R.string.fault_official else R.string.fault_reported
                    val color = if (f.official) Color(0xFFD64545) else Color(0xFFE0A100)
                    Text(
                        stringResource(tag),
                        style = MaterialTheme.typography.labelMedium,
                        color = color,
                        modifier = Modifier.clip(RoundedCornerShape(50)).background(color.copy(alpha = 0.14f)).padding(horizontal = 10.dp, vertical = 3.dp),
                    )
                    Text(f.text.get(lang), style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        Text(stringResource(R.string.pixel_faults_note), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }

    SectionCard(stringResource(R.string.pixel_drops_received, drops.size)) {
        if (drops.isEmpty()) {
            Text(stringResource(R.string.pixel_drops_none), style = MaterialTheme.typography.bodyMedium)
        } else {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                drops.forEach { d ->
                    Text(
                        d.title.get(lang),
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.clip(RoundedCornerShape(50)).background(MaterialTheme.colorScheme.secondaryContainer).padding(horizontal = 10.dp, vertical = 4.dp),
                    )
                }
            }
        }
    }
}
