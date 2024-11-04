package com.example.sportclub

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Payment : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper

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
        val imageClubPayment = findViewById<ImageView>(R.id.imageClubPayment)

        imageClubPayment.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent) // Inicia la actividad
        }

        /*// Crear un ArrayAdapter usando el array de strings y un diseño de spinner predeterminado
        ArrayAdapter.createFromResource(
            this,
            R.array.document_types,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerOptions.adapter = adapter
        } */

        buttonSubmitPayment.setOnClickListener {
            val dni = etDNI.text.toString().toIntOrNull()
            val amount = etAmount.text.toString().toIntOrNull()


            if (dni == null || amount == null ) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

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

            Log.d("PaymentActivity", "Paying member: Document: $dni")

            // activa los miembros
            val result = dbHelper.activateMember(dni)
            if (result != -1) {
                Toast.makeText(this, "Cuota pagada exitosamente", Toast.LENGTH_SHORT).show()
                clearFields()
            } else {
                Toast.makeText(this, "Error al pagar la cuota", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun clearFields() {
        findViewById<EditText>(R.id.etName).text.clear()
        findViewById<EditText>(R.id.etLastName).text.clear()
        findViewById<EditText>(R.id.etPassword).text.clear()
        findViewById<CheckBox>(R.id.checkboxHealth).isChecked = false
        findViewById<Spinner>(R.id.spinnerOptions).setSelection(0)
    }
}