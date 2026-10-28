package com.pixistory.ui.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val ChartColors = listOf(
    Color(0xFF4F8DF7), Color(0xFF2EBFA5), Color(0xFFF2A93B), Color(0xFFE5645F),
    Color(0xFF9B7BE6), Color(0xFF5CC8F0), Color(0xFFB0B84A), Color(0xFFD97CB3),
)

@Composable
fun HBarChart(
    data: List<Pair<String, Number>>,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    valueLabel: (Number) -> String = { it.toInt().toString() },
) {
    val max = (data.maxOfOrNull { it.second.toDouble() } ?: 1.0).coerceAtLeast(1e-9)
    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        data.forEach { (label, value) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    label,
                    modifier = Modifier.width(104.dp),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Box(Modifier.weight(1f)) {
                    Box(
                        Modifier
                            .fillMaxWidth((value.toDouble() / max).toFloat().coerceIn(0.03f, 1f))
                            .height(14.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .background(color),
                    )
                }
                Text(
                    valueLabel(value),
                    modifier = Modifier.padding(start = 8.dp),
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}

@Composable
fun DonutChart(
    slices: List<Triple<String, Int, Color>>,
    modifier: Modifier = Modifier,
    centerLabel: String = "",
) {
    val total = slices.sumOf { it.second }.coerceAtLeast(1)
    Row(modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(20.dp)) {
        Box(Modifier.size(132.dp), contentAlignment = Alignment.Center) {
            Canvas(Modifier.size(132.dp)) {
                val stroke = 22.dp.toPx()
                val inset = stroke / 2
                var start = -90f
                slices.forEach { (_, value, color) ->
                    val sweep = 360f * value / total
                    if (value > 0) {
                        drawArc(
                            color = color,
                            startAngle = start,
                            sweepAngle = (sweep - 2f).coerceAtLeast(1f),
                            useCenter = false,
                            topLeft = Offset(inset, inset),
                            size = Size(size.width - stroke, size.height - stroke),
                            style = Stroke(width = stroke, cap = StrokeCap.Butt),
                        )
                    }
                    start += sweep
                }
            }
            Text(centerLabel, style = MaterialTheme.typography.titleLarge)
        }
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            slices.forEach { (label, value, color) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(10.dp).clip(CircleShape).background(color))
                    Spacer(Modifier.width(8.dp))
                    Text("$label  $value", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

/** Vertical columns with a label under each, scrollable when there are many. */
@Composable
fun ColumnChart(
    data: List<Pair<String, Double>>,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    valueLabel: (Double) -> String = { it.toInt().toString() },
) {
    val max = (data.maxOfOrNull { it.second } ?: 1.0).coerceAtLeast(1e-9)
    Row(
        modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        data.forEach { (label, value) ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(valueLabel(value), fontSize = 10.sp, style = MaterialTheme.typography.labelSmall)
                Spacer(Modifier.height(3.dp))
                Box(
                    Modifier
                        .width(28.dp)
                        .height((110 * (value / max)).coerceAtLeast(4.0).dp)
                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                        .background(color),
                )
                Spacer(Modifier.height(4.dp))
                Text(label, fontSize = 10.sp, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
