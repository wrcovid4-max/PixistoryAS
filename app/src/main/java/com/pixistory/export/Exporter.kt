package com.pixistory.export

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.pixistory.analysis.Analytics
import com.pixistory.data.model.ChatMessageEntity
import com.pixistory.data.model.DeviceEntity
import com.pixistory.data.model.DeviceFileEntity
import com.pixistory.data.model.DeviceStatus
import com.pixistory.data.model.customFields
import com.pixistory.data.model.deviceFromJson
import com.pixistory.data.model.statusEnum
import com.pixistory.data.model.toJson
import com.pixistory.data.model.typeEnum
import com.pixistory.data.model.upgrades
import com.pixistory.data.repo.AssetTags
import com.pixistory.data.repo.DeviceRepository
import com.pixistory.util.AndroidVersions
import com.pixistory.util.formatDate
import com.pixistory.util.formatMoney
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.time.LocalDate
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class Exporter(private val context: Context, private val repo: DeviceRepository) {
    private val dir: File get() = File(context.cacheDir, "shared").apply { mkdirs() }
    private fun stamp() = LocalDate.now().toString()

    suspend fun json(): File = withContext(Dispatchers.IO) {
        val devices = repo.active()
        val arr = JSONArray()
        devices.forEach { arr.put(it.toJson()) }
        val root = JSONObject().put("app", "Pixistory").put("version", 1)
            .put("exportedAt", System.currentTimeMillis()).put("devices", arr)
        File(dir, "pixistory-${stamp()}.json").also { it.writeText(root.toString(2)) }
    }

    suspend fun csv(): File = withContext(Dispatchers.IO) {
        val devices = repo.active()
        fun cell(v: Any?): String {
            val s = v?.toString().orEmpty()
            return if (s.any { it == ',' || it == '"' || it == '\n' }) "\"" + s.replace("\"", "\"\"") + "\"" else s
        }
        val header = listOf(
            "Name", "Brand", "Model", "Type", "Status", "Released", "Android", "API", "Security patch", "Build",
            "Serial", "Colour", "Storage GB", "RAM GB", "Purchased", "Price", "Currency", "Condition", "Group",
            "Asset tag", "Favourite", "Notes",
        )
        val sb = StringBuilder(header.joinToString(",")).append('\n')
        devices.forEach { d ->
            sb.append(
                listOf(
                    d.name, d.brand, d.model, d.typeEnum.label, d.statusEnum.label, d.releaseYear, d.androidVersion,
                    d.apiLevel, d.securityPatch, d.buildNumber, d.serial, d.colorName, d.storageGb, d.ramGb,
                    d.purchaseDate?.let { formatDate(it) }, d.price, d.currency, d.condition, d.groupName,
                    d.assetTag, d.favorite, d.notes,
                ).joinToString(",") { cell(it) },
            ).append('\n')
        }
        File(dir, "pixistory-${stamp()}.csv").also { it.writeText(sb.toString()) }
    }

    private fun describe(d: DeviceEntity): String = buildString {
        append(d.name)
        append("  [${d.typeEnum.label}, ${d.statusEnum.label}]\n")
        if (d.brand.isNotBlank() || d.model.isNotBlank()) append("  ${listOf(d.brand, d.model).filter { it.isNotBlank() }.joinToString(" ")}\n")
        if (d.releaseYear != null) append("  Released: ${d.releaseYear}\n")
        if (d.apiLevel != null || d.androidVersion.isNotBlank()) append("  Software: ${AndroidVersions.label(d.apiLevel, d.androidVersion)}\n")
        if (d.securityPatch.isNotBlank()) append("  Security patch: ${d.securityPatch}\n")
        if (d.serial.isNotBlank()) append("  Serial: ${d.serial}\n")
        if (d.assetTag.isNotBlank()) append("  Asset tag: ${d.assetTag}\n")
        if (d.storageGb != null || d.ramGb != null) append("  Memory: ${d.ramGb?.let { "$it GB RAM" } ?: ""} ${d.storageGb?.let { "$it GB storage" } ?: ""}\n")
        if (d.price != null) append("  Paid: ${formatMoney(d.price, d.currency)}${d.purchaseDate?.let { " on ${formatDate(it)}" } ?: ""}\n")
        d.customFields().forEach { (k, v) -> append("  $k: $v\n") }
        d.upgrades().forEach { append("  Upgrade ${formatDate(it.date)}: ${it.from} to ${it.to}\n") }
        if (d.notes.isNotBlank()) append("  Notes: ${d.notes}\n")
    }

    suspend fun text(): File = withContext(Dispatchers.IO) {
        val devices = repo.active()
        val sb = StringBuilder("My device collection (${stamp()})\n\n")
        devices.groupBy { it.statusEnum }.toSortedMap().forEach { (status, list) ->
            sb.append("== ${status.label} (${list.size}) ==\n\n")
            list.forEach { sb.append(describe(it)).append('\n') }
        }
        File(dir, "pixistory-${stamp()}.txt").also { it.writeText(sb.toString()) }
    }

    suspend fun pdf(): File = withContext(Dispatchers.IO) {
        val devices = repo.active()
        val doc = PdfDocument()
        val title = Paint().apply { textSize = 20f; typeface = Typeface.DEFAULT_BOLD; isAntiAlias = true }
        val body = Paint().apply { textSize = 10f; isAntiAlias = true }
        val bold = Paint().apply { textSize = 11f; typeface = Typeface.DEFAULT_BOLD; isAntiAlias = true }
        val pageW = 595
        val pageH = 842
        var pageNo = 1
        var page = doc.startPage(PdfDocument.PageInfo.Builder(pageW, pageH, pageNo).create())
        var c = page.canvas
        var y = 56f
        c.drawText("My device collection", 40f, y, title)
        y += 18f
        c.drawText("${devices.size} devices, exported ${stamp()}", 40f, y, body)
        y += 24f
        devices.forEach { d ->
            val lines = describe(d).trimEnd().split('\n')
            if (y + lines.size * 13f + 12f > pageH - 40f) {
                doc.finishPage(page)
                pageNo++
                page = doc.startPage(PdfDocument.PageInfo.Builder(pageW, pageH, pageNo).create())
                c = page.canvas
                y = 50f
            }
            lines.forEachIndexed { i, line ->
                c.drawText(line.take(95), 40f, y, if (i == 0) bold else body)
                y += 13f
            }
            y += 8f
        }
        doc.finishPage(page)
        val out = File(dir, "pixistory-${stamp()}.pdf")
        out.outputStream().use { doc.writeTo(it) }
        doc.close()
        out
    }

    suspend fun labels(): File = withContext(Dispatchers.IO) {
        val out = File(dir, "pixistory-labels-${stamp()}.pdf")
        AssetTags.labelSheet(repo.active(), out)
        out
    }

    /** A shareable image summarising the collection. */
    suspend fun card(currency: String): File = withContext(Dispatchers.IO) {
        val devices = repo.active()
        val stats = Analytics.compute(devices, currency)
        val w = 1080
        val h = 1350
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        val bg = Paint().apply {
            shader = LinearGradient(0f, 0f, w.toFloat(), h.toFloat(), Color.parseColor("#0F172A"), Color.parseColor("#1E3A8A"), Shader.TileMode.CLAMP)
        }
        c.drawRect(0f, 0f, w.toFloat(), h.toFloat(), bg)
        val white = Paint().apply { color = Color.WHITE; isAntiAlias = true }
        val dim = Paint().apply { color = Color.parseColor("#A9B8D6"); isAntiAlias = true }
        val accent = Paint().apply { color = Color.parseColor("#38BDF8"); isAntiAlias = true }

        white.textSize = 56f; white.typeface = Typeface.DEFAULT_BOLD
        c.drawText("My device collection", 80f, 150f, white)
        dim.textSize = 34f
        c.drawText("Pixistory", 80f, 200f, dim)

        white.textSize = 260f
        c.drawText(stats.owned.toString(), 80f, 470f, white)
        dim.textSize = 40f
        c.drawText(if (stats.owned == 1) "device owned" else "devices owned", 90f, 530f, dim)

        var x = 80f
        listOf("${stats.wishlist}" to "wishlist", "${stats.sold}" to "sold / gone", "${stats.upgradeCount}" to "upgrades").forEach { (n, label) ->
            white.textSize = 64f
            c.drawText(n, x, 650f, white)
            dim.textSize = 30f
            c.drawText(label, x, 692f, dim)
            x += 320f
        }

        accent.textSize = 36f; accent.typeface = Typeface.DEFAULT_BOLD
        c.drawText("TOP BRANDS", 80f, 800f, accent)
        var yy = 860f
        val maxCount = stats.byBrand.firstOrNull()?.second ?: 1
        stats.byBrand.take(5).forEach { (brand, count) ->
            white.textSize = 38f; white.typeface = Typeface.DEFAULT
            c.drawText(brand, 80f, yy, white)
            val barW = 520f * count / maxCount
            c.drawRoundRect(400f, yy - 30f, 400f + barW, yy + 4f, 8f, 8f, accent)
            dim.textSize = 32f
            c.drawText(count.toString(), 400f + barW + 16f, yy, dim)
            yy += 64f
        }

        val colors = stats.palette.take(8)
        if (colors.isNotEmpty()) {
            var cx = 100f
            colors.forEach { (hex, _) ->
                val p = Paint().apply { color = runCatching { Color.parseColor(hex) }.getOrDefault(Color.GRAY); isAntiAlias = true }
                c.drawCircle(cx, 1230f, 30f, p)
                cx += 78f
            }
        }
        dim.textSize = 28f
        c.drawText("Made with Pixistory", 80f, 1305f, dim)

        val out = File(dir, "pixistory-card-${stamp()}.png")
        out.outputStream().use { bmp.compress(Bitmap.CompressFormat.PNG, 100, it) }
        out
    }

    // ---- backup & restore ---------------------------------------------------------------------

    suspend fun backup(): File = withContext(Dispatchers.IO) {
        val devices = repo.active()
        val files = repo.db.files().all()
        val chat = repo.db.chat().recent(500).reversed()
        val out = File(dir, "pixistory-backup-${stamp()}.zip")
        ZipOutputStream(out.outputStream().buffered()).use { zip ->
            fun put(name: String, text: String) {
                zip.putNextEntry(ZipEntry(name)); zip.write(text.toByteArray()); zip.closeEntry()
            }
            put("devices.json", JSONArray().also { a -> devices.forEach { a.put(it.toJson()) } }.toString())
            put(
                "files.json",
                JSONArray().also { a ->
                    files.forEach {
                        a.put(JSONObject().put("id", it.id).put("deviceId", it.deviceId).put("displayName", it.displayName)
                            .put("mime", it.mime).put("path", it.path).put("addedAt", it.addedAt))
                    }
                }.toString(),
            )
            put(
                "chat.json",
                JSONArray().also { a -> chat.forEach { a.put(JSONObject().put("role", it.role).put("text", it.text).put("createdAt", it.createdAt)) } }.toString(),
            )
            val rels = devices.map { it.photoPath } + files.map { it.path }
            rels.filter { it.isNotBlank() }.distinct().forEach { rel ->
                val f = repo.files.resolve(rel) ?: return@forEach
                zip.putNextEntry(ZipEntry(rel)); f.inputStream().use { it.copyTo(zip) }; zip.closeEntry()
            }
        }
        out
    }

    /** Merges a backup into the collection: newer records win. Returns the number of devices restored. */
    suspend fun restore(uri: Uri): Int = withContext(Dispatchers.IO) {
        val root = context.filesDir.canonicalFile
        var devices: List<DeviceEntity> = emptyList()
        var fileRows: List<DeviceFileEntity> = emptyList()
        var chat: List<ChatMessageEntity> = emptyList()
        context.contentResolver.openInputStream(uri)?.use { raw ->
            ZipInputStream(raw.buffered()).use { zip ->
                var e = zip.nextEntry
                while (e != null) {
                    if (!e.isDirectory) {
                        when {
                            e.name == "devices.json" -> {
                                val a = JSONArray(String(zip.readBytes()))
                                devices = (0 until a.length()).mapNotNull { deviceFromJson(a.getJSONObject(it)) }
                            }
                            e.name == "files.json" -> {
                                val a = JSONArray(String(zip.readBytes()))
                                fileRows = (0 until a.length()).map {
                                    val o = a.getJSONObject(it)
                                    DeviceFileEntity(o.getString("id"), o.getString("deviceId"), o.optString("displayName"), o.optString("mime"), o.getString("path"), o.optLong("addedAt"))
                                }
                            }
                            e.name == "chat.json" -> {
                                val a = JSONArray(String(zip.readBytes()))
                                chat = (0 until a.length()).map {
                                    val o = a.getJSONObject(it)
                                    ChatMessageEntity(role = o.optString("role", "user"), text = o.optString("text"), createdAt = o.optLong("createdAt"))
                                }
                            }
                            e.name.startsWith("photos/") || e.name.startsWith("files/") -> {
                                val target = File(root, e.name).canonicalFile
                                // Never write outside the app's own folder, whatever the ZIP says.
                                if (target.path.startsWith(root.path + File.separator)) {
                                    target.parentFile?.mkdirs()
                                    target.outputStream().use { zip.copyTo(it) }
                                }
                            }
                        }
                    }
                    zip.closeEntry()
                    e = zip.nextEntry
                }
            }
        }
        val existing = repo.db.devices().allIncludingDeleted().associateBy { it.id }
        val toWrite = devices.filter { d -> existing[d.id]?.let { d.updatedAt > it.updatedAt } ?: true }
        if (toWrite.isNotEmpty()) repo.db.devices().upsertAll(toWrite)
        if (fileRows.isNotEmpty()) repo.db.files().insertAll(fileRows)
        if (chat.isNotEmpty() && repo.db.chat().recent(1).isEmpty()) chat.forEach { repo.db.chat().insert(it) }
        toWrite.size
    }
}

fun shareFile(context: Context, file: File, mime: String, title: String) {
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    val send = Intent(Intent.ACTION_SEND).apply {
        type = mime
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(send, title).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
}

fun openFile(context: Context, file: File, mime: String) {
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    val view = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, mime)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    runCatching { context.startActivity(view) }
}

fun shareText(context: Context, text: String, title: String) {
    val send = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(send, title).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
}
