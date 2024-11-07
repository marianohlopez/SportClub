package com.example.sportclub

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.graphics.pdf.PdfDocument
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class Payment : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper
    private var paidDNI: Int? = null
    private var paidAmount: Int? = null
    private var paymentMethod: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_payment)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        dbHelper = DBHelper(this)

        val etDNI = findViewById<EditText>(R.id.editTextCardNumber)
        val etAmount = findViewById<EditText>(R.id.editTextAmount)
        val checkboxCash = findViewById<CheckBox>(R.id.checkboxCash)
        val checkboxCard = findViewById<CheckBox>(R.id.checkboxCard)
        val buttonSubmitPayment = findViewById<Button>(R.id.buttonSubmitPayment)
        val buttonSubmitReceipt = findViewById<Button>(R.id.buttonSubmitReceipt)
        val imageClubPayment = findViewById<ImageView>(R.id.imageClubPayment)

        imageClubPayment.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Limitar la selección de solo un CheckBox
        checkboxCash.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkboxCard.isChecked = false
            }
        }

        checkboxCard.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkboxCash.isChecked = false
            }
        }

        buttonSubmitPayment.setOnClickListener {
            val dni = etDNI.text.toString().toIntOrNull()
            val amount = etAmount.text.toString().toIntOrNull()

            if (dni == null || amount == null) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d("PaymentActivity", "Paying member: Document: $dni")

            // Guardar el método de pago antes de limpiar los campos
            paymentMethod = if (checkboxCash.isChecked) "Efectivo" else "Tarjeta"

            // Activar miembro en la base de datos
            val result = dbHelper.activateMember(dni)
            if (result != -1) {
                paidDNI = dni
                paidAmount = amount

                Toast.makeText(this, "Cuota pagada exitosamente", Toast.LENGTH_SHORT).show()
                clearFields()
            } else {
                Toast.makeText(this, "Error al pagar la cuota", Toast.LENGTH_SHORT).show()
            }
        }

        // Generar el PDF al hacer clic en el botón de recibo
        buttonSubmitReceipt.setOnClickListener {
            if (paidDNI != null && paidAmount != null) {
                createPDFReceipt(paidDNI!!, paidAmount!!)
            } else {
                Toast.makeText(this, "Realiza un pago antes de generar el recibo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createPDFReceipt(dni: Int, amount: Int) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(300, 600, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val paint = Paint()

        // Encabezado estilizado
        paint.textSize = 20f
        paint.color = Color.BLUE
        paint.isFakeBoldText = true
        canvas.drawText("Recibo de Pago - SportClub", 50f, 50f, paint)

        // Línea divisoria
        paint.color = Color.GRAY
        paint.strokeWidth = 1f
        canvas.drawLine(50f, 70f, 250f, 70f, paint)

        // Detalles del pago
        paint.textSize = 16f
        paint.color = Color.BLACK
        paint.isFakeBoldText = true
        canvas.drawText("Detalles del Pago", 50f, 100f, paint)
        paint.textSize = 14f
        paint.isFakeBoldText = false
        canvas.drawText("DNI del Cliente: $dni", 50f, 130f, paint)

        // Método de pago y monto con descuento si es efectivo
        val paymentMethodText = paymentMethod ?: "Tarjeta"
        canvas.drawText("Método de Pago: $paymentMethodText", 50f, 160f, paint)

        if (paymentMethodText == "Efectivo") {
            val discount = (amount * 0.1).toInt()
            val amountWithDiscount = amount - discount
            canvas.drawText("Monto Pagado: $$amountWithDiscount", 50f, 190f, paint)
            canvas.drawText("Descuento 10%: $$discount", 50f, 220f, paint)
        } else {
            canvas.drawText("Monto Pagado: $$amount", 50f, 190f, paint)
        }

        // Línea divisoria
        canvas.drawLine(50f, 240f, 250f, 240f, paint)

        // Añadir el logo
        val logoBitmap = BitmapFactory.decodeResource(resources, R.drawable.deportivo_mandiyu)
        val resizedLogo = Bitmap.createScaledBitmap(logoBitmap, 100, 100, false)
        canvas.drawBitmap(resizedLogo, 100f, 260f, null)

        // Mensaje de agradecimiento
        paint.textSize = 12f
        paint.color = Color.DKGRAY
        canvas.drawText("Gracias por su pago. ", 50f, 370f, paint)
        canvas.drawText("¡Disfruta de los beneficios del club!", 50f, 390f, paint)

        pdfDocument.finishPage(page)

        // Guardar el PDF en la carpeta de Descargas
        val filePath = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "Recibo_Pago_$dni.pdf")
        try {
            pdfDocument.writeTo(FileOutputStream(filePath))
            Toast.makeText(this, "Recibo guardado en: ${filePath.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Error al guardar el recibo", Toast.LENGTH_SHORT).show()
        } finally {
            pdfDocument.close()
        }
    }

    private fun clearFields() {
        findViewById<EditText>(R.id.editTextCardNumber).text.clear()
        findViewById<EditText>(R.id.editTextAmount).text.clear()
        findViewById<CheckBox>(R.id.checkboxCash).isChecked = false
        findViewById<CheckBox>(R.id.checkboxCard).isChecked = false
    }
}
