package com.pixistory.data.repo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import java.util.UUID

/** Photos and receipts copied into private storage. Paths in the database are relative to filesDir. */
class FileStore(private val context: Context) {
    private val root: File get() = context.filesDir

    fun resolve(relative: String): File? =
        if (relative.isBlank()) null else File(root, relative).takeIf { it.exists() }

    fun file(relative: String): File = File(root, relative)

    /** Copies a picked photo, scaled down to at most 1600 px on its longest side. Returns the relative path. */
    fun savePhoto(uri: Uri, deviceId: String): String? = try {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        var sample = 1
        val longest = maxOf(bounds.outWidth, bounds.outHeight)
        while (longest / sample > 1600) sample *= 2
        val opts = BitmapFactory.Options().apply { inSampleSize = sample }
        val bitmap = context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opts) }
        if (bitmap == null) {
            null
        } else {
            val dir = File(root, "photos").apply { mkdirs() }
            val rel = "photos/${deviceId}_${System.currentTimeMillis()}.jpg"
            File(root, rel).outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 86, it) }
            rel
        }
    } catch (e: Exception) {
        null
    }

    /** Copies a picked document (receipt, warranty card…). Returns Triple(relative path, display name, mime). */
    fun saveDocument(uri: Uri): Triple<String, String, String>? = try {
        var display = "file"
        context.contentResolver.query(uri, null, null, null, null)?.use { c ->
            val idx = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (idx >= 0 && c.moveToFirst()) display = c.getString(idx) ?: "file"
        }
        val mime = context.contentResolver.getType(uri) ?: "application/octet-stream"
        File(root, "files").mkdirs()
        val safe = display.replace(Regex("[^A-Za-z0-9._-]"), "_")
        val rel = "files/${UUID.randomUUID()}_$safe"
        context.contentResolver.openInputStream(uri)?.use { input ->
            File(root, rel).outputStream().use { out -> input.copyTo(out) }
        }
        Triple(rel, display, mime)
    } catch (e: Exception) {
        null
    }

    fun delete(relative: String) {
        if (relative.isNotBlank()) runCatching { File(root, relative).delete() }
    }
}
