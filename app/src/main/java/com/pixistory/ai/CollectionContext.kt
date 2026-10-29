package com.pixistory.ai

import com.pixistory.analysis.Analytics
import com.pixistory.data.model.DeviceEntity
import com.pixistory.data.model.customFields
import com.pixistory.data.model.statusEnum
import com.pixistory.data.model.typeEnum
import com.pixistory.data.model.upgrades
import com.pixistory.util.AndroidVersions
import com.pixistory.util.formatDate
import com.pixistory.util.patchAgeDays
import java.time.LocalDate

/** Turns the collection into the compact text the assistant answers from. */
object CollectionContext {
    fun systemPrompt(devices: List<DeviceEntity>, currency: String): String {
        val today = LocalDate.now()
        val stats = Analytics.compute(devices, currency, today)
        val lines = devices.take(200).joinToString("\n") { d ->
            buildString {
                append("- ${d.name}")
                append(" | ${d.typeEnum.label} | ${d.statusEnum.label}")
                if (d.brand.isNotBlank()) append(" | brand ${d.brand}")
                if (d.releaseYear != null) append(" | released ${d.releaseYear}")
                if (d.apiLevel != null || d.androidVersion.isNotBlank()) {
                    append(" | runs ${AndroidVersions.label(d.apiLevel, d.androidVersion)}")
                    if (d.apiLevel != null) append(" (API ${d.apiLevel})")
                }
                if (d.securityPatch.isNotBlank()) {
                    val age = patchAgeDays(d.securityPatch, today)
                    append(" | patch ${d.securityPatch}")
                    if (age != null) append(" ($age days old)")
                }
                if (d.storageGb != null) append(" | ${d.storageGb}GB storage")
                if (d.ramGb != null) append(" | ${d.ramGb}GB RAM")
                if (d.colorName.isNotBlank()) append(" | ${d.colorName}")
                if (d.price != null) append(" | paid ${d.price} ${d.currency}")
                if (d.purchaseDate != null) append(" on ${formatDate(d.purchaseDate)}")
                append(" | condition ${d.condition.lowercase()}")
                if (d.groupName.isNotBlank()) append(" | group ${d.groupName}")
                if (d.assetTag.isNotBlank()) append(" | tag ${d.assetTag}")
                if (d.serial.isNotBlank()) append(" | serial ${d.serial}")
                val up = d.upgrades()
                if (up.isNotEmpty()) append(" | upgrades: " + up.joinToString("; ") { "${it.from} to ${it.to} (${formatDate(it.date)})" })
                val custom = d.customFields()
                if (custom.isNotEmpty()) append(" | " + custom.entries.joinToString(", ") { "${it.key}: ${it.value}" })
                if (d.notes.isNotBlank()) append(" | notes: ${d.notes.take(120).replace('\n', ' ')}")
            }
        }
        return """
You are Pixi, the assistant inside Pixistory, an Android app for people who collect and keep devices.
Answer questions about the user's own collection and about Android hardware and software, using the data below.
Reply in the language the user writes in (English or Urdu). Be concise and practical. Use short paragraphs or simple "- " bullet lists. Put key facts in **bold**.
If the answer is not in the data, say so plainly instead of guessing, and keep device specifications you are not sure of clearly labelled as general knowledge.
Today's date is $today. The user's default currency is $currency.

Collection summary: ${stats.owned} owned, ${stats.wishlist} on the wishlist, ${stats.sold} sold or gone.

Devices (${devices.size}${if (devices.size > 200) ", first 200 shown" else ""}):
${lines.ifBlank { "(the collection is empty)" }}
""".trimIndent()
    }

    val suggestions = listOf(
        "Which of my devices need a software update?",
        "What's my oldest device and what can it still do?",
        "How much have I spent in total?",
        "Which Android versions am I missing?",
        "Which phone should I use daily?",
        "Summarise my collection in three lines",
    )
}
