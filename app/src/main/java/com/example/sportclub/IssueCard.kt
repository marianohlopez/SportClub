package com.example.sportclub

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.os.Environment
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import android.util.Log
import android.widget.ImageView

class IssueCard : AppCompatActivity() {

    private lateinit var tvCardNumber: TextView
    private lateinit var tvName: TextView
    private lateinit var tvSurname: TextView
    private lateinit var tvDocType: TextView
    private lateinit var tvDocNumber: TextView
    private lateinit var tvIssueDate: TextView
    private lateinit var tvExpirationDate: TextView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_issue_card)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvCardNumber = findViewById(R.id.tvCardNumber)
        tvName = findViewById(R.id.tvName)
        tvSurname = findViewById(R.id.tvSurname)
        tvDocType = findViewById(R.id.tvDocType)
        tvDocNumber = findViewById(R.id.tvDocNumber)
        tvIssueDate = findViewById(R.id.tvIssueDate)
        tvExpirationDate = findViewById(R.id.tvExpirationDate)

        val imageClub = findViewById<ImageView>(R.id.imageClubIC)

        imageClub.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val memberDocument = intent.getIntExtra("MEMBER_DOCUMENT", -1)

        if (memberDocument != -1) {
            loadMemberData(memberDocument)
        } else {
            tvCardNumber.text = "Error: Documento de socio no encontrado"
        }

        val btnDownloadPDF = findViewById<Button>(R.id.btnDownloadPDF)
        btnDownloadPDF.setOnClickListener {
            downloadPDF(memberDocument)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadMemberData(memberDocument: Int) {
        val memberData = DBHelper(this).getMember(memberDocument)

        if (memberData != null) {
            tvCardNumber.text = "Nº DE SOCIO: ${memberData["memberId"]}"
            tvName.text = "NOMBRE: ${memberData["name"]}"
            tvSurname.text = "APELLIDO: ${memberData["surname"]}"
            tvDocType.text = "TIPO DE DOC: ${memberData["docType"]}"
            tvDocNumber.text = "N° DE DOC: ${memberData["docNumber"]}"
            tvIssueDate.text = "FECHA DE EMISIÓN: ${memberData["issueDate"]}"
            tvExpirationDate.text = "VENCIMIENTO: ${memberData["expirationDate"]}"
        } else {
            tvCardNumber.text = "Error: Socio no encontrado"
        }
    }

    private fun downloadPDF(memberDocument: Int) {
        val memberData = DBHelper(this).getMember(memberDocument)

        if (memberData != null) {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(300, 600, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas
            val paint = Paint()

            // Encabezado estilizado
            paint.textSize = 20f
            paint.color = Color.BLUE
            paint.isFakeBoldText = true
            canvas.drawText("Ficha de Socio - SportClub", 50f, 50f, paint)

            // Línea divisoria
            paint.color = Color.GRAY
            paint.strokeWidth = 1f
            canvas.drawLine(50f, 70f, 250f, 70f, paint)

            // Detalles del socio
            paint.textSize = 16f
            paint.color = Color.BLACK
            paint.isFakeBoldText = true
            canvas.drawText("Detalles del Socio", 50f, 100f, paint)
            paint.textSize = 14f
            paint.isFakeBoldText = false

            var yPosition = 130f
            canvas.drawText("Nº DE SOCIO: ${memberData["memberId"]}", 50f, yPosition, paint)
            yPosition += 30
            canvas.drawText("NOMBRE: ${memberData["name"]}", 50f, yPosition, paint)
            yPosition += 30
            canvas.drawText("APELLIDO: ${memberData["surname"]}", 50f, yPosition, paint)
            yPosition += 30
            canvas.drawText("TIPO DE DOC: ${memberData["docType"]}", 50f, yPosition, paint)
            yPosition += 30
            canvas.drawText("N° DE DOC: ${memberData["docNumber"]}", 50f, yPosition, paint)
            yPosition += 30
            canvas.drawText("FECHA DE EMISIÓN: ${memberData["issueDate"]}", 50f, yPosition, paint)
            yPosition += 30
            canvas.drawText("VENCIMIENTO: ${memberData["expirationDate"]}", 50f, yPosition, paint)

            // Línea divisoria
            yPosition += 20
            paint.color = Color.GRAY
            paint.strokeWidth = 1f
            canvas.drawLine(50f, yPosition, 250f, yPosition, paint)

            // Añadir el logo
            val logoBitmap = BitmapFactory.decodeResource(resources, R.drawable.deportivo_mandiyu)
            val resizedLogo = Bitmap.createScaledBitmap(logoBitmap, 100, 100, false)
            canvas.drawBitmap(resizedLogo, 100f, yPosition + 30f, null)

            // Mensaje de agradecimiento
            yPosition += 150
            paint.textSize = 12f
            paint.color = Color.DKGRAY
            canvas.drawText("Gracias por ser parte de nuestro club.", 50f, yPosition, paint)

            pdfDocument.finishPage(page)

            // Guardar el PDF en la carpeta de Descargas
            val filePath = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "Ficha_Socio_${memberData["memberId"]}.pdf")
            try {
                pdfDocument.writeTo(FileOutputStream(filePath))
                Toast.makeText(this, "Ficha guardada en: ${filePath.absolutePath}", Toast.LENGTH_LONG).show()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, "Error al guardar la ficha", Toast.LENGTH_SHORT).show()
            } finally {
                pdfDocument.close()
            }
        } else {
            Toast.makeText(this, "No se encontraron datos para generar la ficha", Toast.LENGTH_SHORT).show()
        }
    }

}
