package com.example.sportclub

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date


class IssueCardMain : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper
    private lateinit var etIdNumber: EditText
    private lateinit var btnSignUp: Button
    private lateinit var btnBack: Button

    @SuppressLint("MissingInflatedId") // Lo recomendó Android Studio
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_issue_card_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DBHelper(this)
        etIdNumber = findViewById(R.id.etIdNumber)
        btnSignUp = findViewById(R.id.btnSignUp)
        btnBack = findViewById(R.id.btnBack)
        val imageClub = findViewById<ImageView>(R.id.imageClubISM)

        imageClub.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent) // Inicia la actividad
        }

        // Configura el botón de "Ingresar"
        btnSignUp.setOnClickListener {
            val documentNumber = etIdNumber.text.toString()
            if (documentNumber.isNotEmpty()) {
                checkMemberStatus(documentNumber.toInt())
            } else {
                Toast.makeText(this, "Por favor, ingrese un número de documento", Toast.LENGTH_SHORT).show()
            }
        }

        // Configura el botón de "Volver"
        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun checkMemberStatus(documentNumber: Int) {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT ${DBHelper.MEMBER_COLUMN_ID},${DBHelper.MEMBER_COLUMN_DOCUMENT}, ${DBHelper.MEMBER_COLUMN_ISACTIVE}, ${DBHelper.MEMBER_COLUMN_EXPIRATIONDATE} FROM ${DBHelper.TABLE_MEMBERS} WHERE ${DBHelper.MEMBER_COLUMN_DOCUMENT} = ?",
            arrayOf(documentNumber.toString())
        )

        if (cursor.moveToFirst()) {
            val memberDocument = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.MEMBER_COLUMN_DOCUMENT))
            val isActive = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.MEMBER_COLUMN_ISACTIVE)) == 1
            val expirationDate = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.MEMBER_COLUMN_EXPIRATIONDATE))

            // Verifica si el miembro está activo y si su fecha de expiración no ha pasado
            if (!isActive) {
                showAlert("El socio está inactivo. Por favor, contacte a administración.")
            } else if (isExpired(expirationDate)) {
                showAlert("El socio debe estar al día con el pago.")
            } else {
                // Si el socio está activo y al día, procede al siguiente Activity
                goToIssueCardActivity(memberDocument)
            }
        } else {
            showAlert("No se encontró un socio con el número de documento ingresado.")
        }
        cursor.close()
        db.close()
    }


    private fun showAlert(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Atención")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun isExpired(expirationDate: String): Boolean {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = Date()
        return sdf.parse(expirationDate)?.before(today) == true
    }
    private fun goToIssueCardActivity(memberDocument: Int) {
        val intent = Intent(this, IssueCard::class.java).apply {
            putExtra("MEMBER_ID", memberDocument)
        }
        startActivity(intent)
    }
}