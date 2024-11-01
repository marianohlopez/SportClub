package com.example.sportclub

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnSignUp = findViewById<Button>(R.id.btnSignUp) // Referencia al botón "Inscribir Socio"
        val btnMakeCard = findViewById<Button>(R.id.btnMakeCard) // Referencia al botón "Emitir Carnet"

        btnSignUp.setOnClickListener {
            // Intent para abrir la actividad Inscription
            val intent = Intent(this, Inscription::class.java)
            startActivity(intent)
        }

        btnMakeCard.setOnClickListener {
            // Intent para abrir la actividad IssueCardMain
            val intent = Intent(this, IssueCardMain::class.java)
            startActivity(intent)
        }
    }
}