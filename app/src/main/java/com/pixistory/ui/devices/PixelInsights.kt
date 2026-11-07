@file:OptIn(ExperimentalLayoutApi::class)

package com.pixistory.ui.devices

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pixistory.util.formatDate
import java.net.URLEncoder
import java.time.LocalDate
import java.time.ZoneId
import com.pixistory.R
import com.pixistory.data.pixel.PixelKnowledge
import com.pixistory.data.pixel.PixelModel
import com.pixistory.ui.common.InfoTable
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

    val uri = LocalUriHandler.current
    val q = model.name.replace(" ", "+")
    model.launch?.let { l ->
        SectionCard(stringResource(R.string.pixel_launch)) {
            val date = runCatching { formatDate(LocalDate.parse(l.date).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()) }.getOrDefault(l.date)
            InfoTable(
                listOf(
                    stringResource(R.string.launch_date) to date,
                    stringResource(R.string.launch_event) to l.event.get(lang),
                    stringResource(R.string.launch_place) to l.place.get(lang),
                    stringResource(R.string.launch_presenter) to l.presenter.get(lang),
                ),
            )
            OutlinedButton(
                onClick = { uri.openUri("https://www.youtube.com/results?search_query=" + URLEncoder.encode(l.query, "UTF-8")) },
                modifier = Modifier.fillMaxWidth(),
            ) { Text(stringResource(R.string.link_youtube)) }
        }
    }

    SectionCard(stringResource(R.string.pixel_links)) {
        OutlinedButton(onClick = { uri.openUri(model.officialUrl) }, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(if (model.officialIsStore) R.string.link_official_store else R.string.link_official_search))
        }
        OutlinedButton(onClick = { uri.openUri("https://www.gsmarena.com/results.php3?sQuickSearch=yes&sName=" + URLEncoder.encode(model.name, "UTF-8").replace("+", "%20")) }, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.link_gsmarena))
        }
        model.ifixitScore?.let { score ->
            Text(stringResource(R.string.repair_score, String.format("%.0f", score)), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.secondary)
            if (model.ifixitNote.isNotBlank()) Text(stringResource(R.string.repair_provisional), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        OutlinedButton(
            onClick = { uri.openUri(model.ifixitUrl.ifBlank { "https://www.ifixit.com/Search?query=$q" }) },
            modifier = Modifier.fillMaxWidth(),
        ) { Text(stringResource(R.string.link_ifixit)) }
    }

    SectionCard(stringResource(R.string.pixel_specs)) {
        val labels = mapOf(
            "chip" to R.string.spec_chip, "ram" to R.string.f_ram, "storage" to R.string.f_storage,
            "display" to R.string.spec_display, "battery" to R.string.spec_battery, "camera" to R.string.spec_camera,
            "protection" to R.string.spec_protection, "price" to R.string.spec_price,
        )
        val rows = model.specs.map { (k, v) -> (labels[k]?.let { stringResource(it) } ?: k) to v } + listOf(
            stringResource(R.string.spec_released) to model.released,
            stringResource(R.string.spec_launch_android) to (if (model.launchApi > 0) "API ${model.launchApi}" else ""),
            stringResource(R.string.spec_support) to model.supportEnds,
        )
        InfoTable(rows)
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

    if (model.id.startsWith("pixel")) {
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
}
