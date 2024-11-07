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

    @SuppressLint("SetTextI18n") //Esto me sugirio el android studio
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_issue_card)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Referencia de vistas
        tvCardNumber = findViewById(R.id.tvCardNumber)
        tvName = findViewById(R.id.tvName)
        tvSurname = findViewById(R.id.tvSurname)
        tvDocType = findViewById(R.id.tvDocType)
        tvDocNumber = findViewById(R.id.tvDocNumber)
        tvIssueDate = findViewById(R.id.tvIssueDate)
        tvExpirationDate = findViewById(R.id.tvExpirationDate)

        val memberDocument = intent.getIntExtra("MEMBER_DOCUMENT", -1)

        Log.d("IssueCard", "Received MEMBER_DOCUMENT in IssueCard: $memberDocument")

        if (memberDocument != -1) {
            loadMemberData(memberDocument)
        } else {
            // Maneja el caso en que no se pase un memberDocument válido
            tvCardNumber.text = "Error: Documento de socio no encontrado"
        }

        // Botón de descarga de PDF
        val btnDownloadPDF = findViewById<Button>(R.id.btnDownloadPDF)
        btnDownloadPDF.setOnClickListener {
            // Lógica para descargar el PDF
            downloadPDF(memberDocument)
        }

        val btnBack = findViewById<Button>(R.id.btnBack)
        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent) // Inicia la actividad
        }
    }
    @SuppressLint("SetTextI18n") //Esto me sugirio el android studio
    private fun loadMemberData(memberDocument: Int) {
        // Ejemplo: cargar datos de DBHelper y asignar a los TextViews
        val memberData = DBHelper(this).getMember(memberDocument)

        if (memberData != null) {
            tvCardNumber.text = "Nº DE SOCIO: ${memberData["ID"] ?: "No disponible"}"
            tvName.text = "NOMBRE: ${memberData["FirstName"] ?: "No disponible"}"
            tvSurname.text = "APELLIDO: ${memberData["LastName"] ?: "No disponible"}"
            tvDocType.text = "TIPO DE DOC: ${memberData["DocumentType"] ?: "No disponible"}"
            tvDocNumber.text = "N° DE DOC: ${memberData["Document"] ?: "No disponible"}"

            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            val inscriptionDateStr = memberData["InscriptionDate"]
            tvIssueDate.text = if (inscriptionDateStr != null) {
                "FECHA DE EMISIÓN: ${format.format(format.parse(inscriptionDateStr))}"
            } else {
                "FECHA DE EMISIÓN: N/A"
            }

            val expirationDateStr = memberData["ExpirationDate"]
            tvExpirationDate.text = if (expirationDateStr != null) {
                "FECHA DE VENCIMIENTO: ${format.format(format.parse(expirationDateStr))}"
            } else {
                "FECHA DE VENCIMIENTO: N/A"
            }
        } else {
            tvCardNumber.text = "Error: Socio no encontrado"
        }
    }

    private fun downloadPDF(memberDocument: Int) {
        // Implementar la lógica para generar y descargar el PDF con los datos del socio
        val memberData = DBHelper(this).getMember(memberDocument)

        if (memberData != null) {
            // Crear un documento PDF
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(300, 600, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas: Canvas = page.canvas

            val paint = Paint()
            paint.textAlign = Paint.Align.CENTER

            // Fondo oscuro
            canvas.drawColor(Color.parseColor("#00203F"))

            // Logo
            val logoBitmap = BitmapFactory.decodeResource(resources, R.drawable.deportivo_mandiyu)
            val logoScaled = Bitmap.createScaledBitmap(logoBitmap, 80, 80, false)
            canvas.drawBitmap(logoScaled, pageInfo.pageWidth / 2f - logoScaled.width / 2f, 30f, null)

            // Texto "CARNET DE SOCIO" centrado debajo del logo
            paint.color = Color.WHITE
            paint.textSize = 18f
            canvas.drawText("CARNET DE SOCIO", pageInfo.pageWidth / 2f, 130f, paint)

            // Foto de perfil (imagen de marcador de lugar)
            val placeholderBitmap = BitmapFactory.decodeResource(resources, R.drawable.placeholder)
            val photoScaled = Bitmap.createScaledBitmap(placeholderBitmap, 60, 60, false)
            canvas.drawBitmap(photoScaled, pageInfo.pageWidth / 2f - photoScaled.width / 2f, 150f, null)

            // Datos del socio
            paint.textSize = 12f
            paint.textAlign = Paint.Align.LEFT

            // Información del socio
            var yPosition = 250
            canvas.drawText("Nº DE SOCIO: ${memberData["ID"]}", 10f, yPosition.toFloat(), paint)
            yPosition += 20
            canvas.drawText("NOMBRE: ${memberData["FirstName"]}", 10f, yPosition.toFloat(), paint)
            yPosition += 20
            canvas.drawText("APELLIDO: ${memberData["LastName"]}", 10f, yPosition.toFloat(), paint)
            yPosition += 20
            canvas.drawText("TIPO DE DOC: ${memberData["DocumentType"]}", 10f, yPosition.toFloat(), paint)
            yPosition += 20
            canvas.drawText("N° DE DOC: ${memberData["Document"]}", 10f, yPosition.toFloat(), paint)
            yPosition += 20

            // Convertir fechas de emisión y vencimiento
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val inscriptionDateString = memberData["InscriptionDate"]
            val expirationDateString = memberData["ExpirationDate"]

// Analizar las fechas a partir de sus cadenas y formatearlas en el mismo formato
            val inscriptionDate = if (inscriptionDateString != null) {
                dateFormat.format(dateFormat.parse(inscriptionDateString))
            } else {
                "Fecha no disponible"
            }

            val expirationDate = if (expirationDateString != null) {
                dateFormat.format(dateFormat.parse(expirationDateString))
            } else {
                "Fecha no disponible"
            }

// Escribir las fechas en el PDF
            yPosition += 80
            canvas.drawText("FECHA DE EMISIÓN: $inscriptionDate", 50f, yPosition.toFloat(), paint)
            yPosition += 20
            canvas.drawText("VENCIMIENTO: $expirationDate", 50f, yPosition.toFloat(), paint)

            // Terminar la página y el documento
            pdfDocument.finishPage(page)

            // Guardar el archivo PDF en el almacenamiento externo
            val filePath = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Socio_$memberDocument.pdf")
            try {
                pdfDocument.writeTo(FileOutputStream(filePath))
                Toast.makeText(this, "PDF guardado en ${filePath.absolutePath}", Toast.LENGTH_LONG).show()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, "Error al guardar el PDF: ${e.message}", Toast.LENGTH_LONG).show()
            }

            // Cerrar el documento PDF
            pdfDocument.close()
        } else {
            Toast.makeText(this, "Error: Socio no encontrado", Toast.LENGTH_SHORT).show()
        }
    }
}