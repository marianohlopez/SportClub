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

        val btnSignUp = findViewById<Button>(R.id.btnSignUp)
        val btnListMembers = findViewById<Button>(R.id.btnListMembers)
        val btnPayCourse = findViewById<Button>(R.id.btnPayCourse)

        btnSignUp.setOnClickListener {
            val intent = Intent(this, Inscription::class.java)
            startActivity(intent)
        }
        btnListMembers.setOnClickListener {
            val intent = Intent(this, MemberList::class.java)
            startActivity(intent)
        }

        btnPayCourse.setOnClickListener {
            val intent = Intent(this, Payment::class.java)
            startActivity(intent)
        }
    }
}