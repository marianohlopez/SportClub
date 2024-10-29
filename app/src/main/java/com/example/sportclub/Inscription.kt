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

class Inscription : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inscription)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        dbHelper = DBHelper(this)

        val etName = findViewById<EditText>(R.id.etName)
        val etLastName = findViewById<EditText>(R.id.etLastName)
        val etDocument = findViewById<EditText>(R.id.etPassword)
        val spinnerOptions = findViewById<Spinner>(R.id.spinnerOptions)
        val checkboxHealth = findViewById<CheckBox>(R.id.checkboxHealth)
        val btnInscription = findViewById<Button>(R.id.btnInscription)
        val btnClean = findViewById<Button>(R.id.btnClean)
        val imageClub = findViewById<ImageView>(R.id.imageClub)

        imageClub.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent) // Inicia la actividad
        }

        // Crear un ArrayAdapter usando el array de strings y un diseño de spinner predeterminado
        ArrayAdapter.createFromResource(
            this,
            R.array.document_types,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerOptions.adapter = adapter
        }

        btnInscription.setOnClickListener {
            val firstName = etName.text.toString().trim()
            val lastName = etLastName.text.toString().trim()
            val documentType = spinnerOptions.selectedItem.toString()
            val document = etDocument.text.toString().toIntOrNull()

            if (firstName.isEmpty() || lastName.isEmpty() || document == null) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!checkboxHealth.isChecked) {
                Toast.makeText(this, "El socio debe tener apto físico", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d("InscriptionActivity", "Inscribing member: $firstName $lastName, Document Type: $documentType, Document: $document")

            // Inserta al nuevo miembro en la base de datos
            val result = dbHelper.addMember(firstName, lastName, documentType, document)
            if (result != -1L) {
                Toast.makeText(this, "Socio inscrito exitosamente", Toast.LENGTH_SHORT).show()
                clearFields()
            } else {
                Toast.makeText(this, "Error al inscribir socio", Toast.LENGTH_SHORT).show()
            }
        }

        btnClean.setOnClickListener {
            clearFields()
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