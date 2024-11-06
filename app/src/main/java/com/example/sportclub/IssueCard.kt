package com.example.sportclub

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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
    }
    @SuppressLint("SetTextI18n") //Esto me sugirio el android studio
    private fun loadMemberData(memberDocument: Int) {
        // Ejemplo: cargar datos de DBHelper y asignar a los TextViews
        val memberData = DBHelper(this).getMember(memberDocument)

        if (memberData != null) {
            tvCardNumber.text = "Nº DE SOCIO: ${memberData["ID"]}"
            tvName.text = "NOMBRE: ${memberData["FirstName"]}"
            tvSurname.text = "APELLIDO: ${memberData["LastName"]}"
            tvDocType.text = "TIPO DE DOC: ${memberData["DocumentType"]}"
            tvDocNumber.text = "N° DE DOC: ${memberData["Document"]}"

            memberData["InscriptionDate"]?.toLong()?.let {
                val inscriptionDate = SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
                ).format(Date(memberData["InscriptionDate"]!!.toLong()))
                tvIssueDate.text = "FECHA DE EMISIÓN: $inscriptionDate"
            } ?: run {
                tvIssueDate.text = "FECHA DE EMISIÓN: N/A"
            }

            memberData["ExpirationDate"]?.toLong()?.let {
                val expirationDate = SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
                ).format(Date(memberData["ExpirationDate"]!!.toLong()))
                tvExpirationDate.text = "VENCIMIENTO: $expirationDate"
            } ?: run {
                tvExpirationDate.text = "VENCIMIENTO: N/A"
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
            val paint = Paint()
            val pageInfo = PdfDocument.PageInfo.Builder(300, 600, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas: Canvas = page.canvas

            // Configuración de estilo para el texto
            paint.color = Color.BLACK
            paint.textSize = 12f

            // Agregar el contenido del PDF
            var yPosition = 20
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
            val inscriptionDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(memberData["InscriptionDate"]!!.toLong()))
            val expirationDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(memberData["ExpirationDate"]!!.toLong()))

            canvas.drawText("FECHA DE EMISIÓN: $inscriptionDate", 10f, yPosition.toFloat(), paint)
            yPosition += 20
            canvas.drawText("VENCIMIENTO: $expirationDate", 10f, yPosition.toFloat(), paint)

            // Terminar la página y el documento
            pdfDocument.finishPage(page)

            // Guardar el archivo PDF en el almacenamiento externo
            val filePath = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Socio_$tvCardNumber.pdf")
            try {
                pdfDocument.writeTo(FileOutputStream(filePath))
                Toast.makeText(this, "PDF guardado en ${filePath.path}", Toast.LENGTH_LONG).show()
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