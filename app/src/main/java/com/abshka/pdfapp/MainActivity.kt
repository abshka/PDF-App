package com.abshka.pdfapp

import android.content.ContentValues
import android.content.Context
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import java.io.OutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var editText: EditText
    private lateinit var buttonSavePdf: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editText = findViewById(R.id.editText)
        buttonSavePdf = findViewById(R.id.buttonSavePdf)

        buttonSavePdf.setOnClickListener {
            saveTextAsPdf()
        }
    }

    private fun saveTextAsPdf() {
        val text = editText.text.toString()
        if (text.isEmpty()) {
            Toast.makeText(this, "Введите текст", Toast.LENGTH_SHORT).show()
            return
        }

        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)

        val canvas = page.canvas
        val paint = android.graphics.Paint()
        paint.textSize = 12f

        val x = 10f
        val y = 25f

        canvas.drawText(text, x, y, paint)
        pdfDocument.finishPage(page)

        val fileName = "sample.pdf"
        val outputStream: OutputStream?

        try {
            outputStream = getOutputStream(this, fileName)
            if (outputStream != null) {
                pdfDocument.writeTo(outputStream)
                Toast.makeText(this, "PDF сохранен", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Ошибка сохранения PDF", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Ошибка сохранения PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            pdfDocument.close()
        }
    }

    private fun getOutputStream(context: Context, fileName: String): OutputStream? {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Documents/")
        }

        val contentResolver = context.contentResolver
        val uri: Uri? = contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
        return uri?.let { contentResolver.openOutputStream(it) }
    }
}