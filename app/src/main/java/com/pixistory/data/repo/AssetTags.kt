package com.pixistory.data.repo

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.google.zxing.BarcodeFormat
import com.google.zxing.oned.Code128Writer
import com.pixistory.data.model.DeviceEntity
import com.pixistory.data.model.DeviceType
import com.pixistory.data.model.typeEnum
import java.io.File

object AssetTags {
    /** "PX-PH-0007" from the user's prefix, the device category and the running number. */
    fun format(prefix: String, type: DeviceType, number: Int, digits: Int, useCategory: Boolean): String {
        val p = prefix.trim().uppercase().ifBlank { "PX" }
        val mid = if (useCategory) "-${type.code}" else ""
        return "$p$mid-${number.toString().padStart(digits.coerceIn(1, 8), '0')}"
    }

    fun barcode(content: String, widthPx: Int, heightPx: Int): Bitmap {
        val matrix = Code128Writer().encode(content, BarcodeFormat.CODE_128, widthPx, heightPx)
        val bmp = Bitmap.createBitmap(matrix.width, matrix.height, Bitmap.Config.ARGB_8888)
        for (x in 0 until matrix.width) {
            for (y in 0 until matrix.height) {
                bmp.setPixel(x, y, if (matrix.get(x, y)) Color.BLACK else Color.WHITE)
            }
        }
        return bmp
    }

    /**
     * A printable A4 sheet of 62 x 30 mm labels (3 across, 9 down): barcode, tag, name and serial.
     * Open the PDF and print it on any sticker sheet.
     */
    fun labelSheet(devices: List<DeviceEntity>, out: File) {
        val labels = devices.filter { it.assetTag.isNotBlank() }
        val pageW = 595
        val pageH = 842
        val labelW = 176
        val labelH = 85
        val cols = 3
        val rows = 9
        val marginX = (pageW - cols * labelW) / 2
        val marginY = (pageH - rows * labelH) / 2
        val doc = PdfDocument()
        val border = Paint().apply { color = Color.LTGRAY; style = Paint.Style.STROKE; strokeWidth = 0.5f }
        val big = Paint().apply { color = Color.BLACK; textSize = 9f; typeface = Typeface.DEFAULT_BOLD; isAntiAlias = true }
        val small = Paint().apply { color = Color.DKGRAY; textSize = 7f; isAntiAlias = true }
        val perPage = cols * rows
        val pages = maxOf(1, (labels.size + perPage - 1) / perPage)
        for (p in 0 until pages) {
            val page = doc.startPage(PdfDocument.PageInfo.Builder(pageW, pageH, p + 1).create())
            val c: Canvas = page.canvas
            for (i in 0 until perPage) {
                val idx = p * perPage + i
                if (idx >= labels.size) break
                val d = labels[idx]
                val x = marginX + (i % cols) * labelW
                val y = marginY + (i / cols) * labelH
                c.drawRect(x.toFloat(), y.toFloat(), (x + labelW).toFloat(), (y + labelH).toFloat(), border)
                val bar = barcode(d.assetTag, 320, 80)
                val dst = android.graphics.Rect(x + 8, y + 6, x + labelW - 8, y + 44)
                c.drawBitmap(bar, null, dst, null)
                c.drawText(d.assetTag, (x + 8).toFloat(), (y + 56).toFloat(), big)
                c.drawText(d.name.take(34), (x + 8).toFloat(), (y + 67).toFloat(), small)
                val sub = buildString {
                    append(d.typeEnum.label)
                    if (d.serial.isNotBlank()) append("  •  S/N ${d.serial}")
                }
                c.drawText(sub.take(40), (x + 8).toFloat(), (y + 77).toFloat(), small)
            }
            doc.finishPage(page)
        }
        out.outputStream().use { doc.writeTo(it) }
        doc.close()
    }
}
