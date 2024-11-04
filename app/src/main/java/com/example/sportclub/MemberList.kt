package com.example.sportclub

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MemberList : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var memberAdapter: MemberAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member_list)

        // Inicializar la base de datos y obtener los miembros
        dbHelper = DBHelper(this)
        val memberList = dbHelper.getAllMembers()

        // Configurar el RecyclerView
        recyclerView = findViewById(R.id.memberList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        memberAdapter = MemberAdapter(memberList)
        recyclerView.adapter = memberAdapter

        // Configurar el botón de logo para volver a MainActivity
        val logoClub = findViewById<ImageView>(R.id.logoClub)
        logoClub.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent) // Inicia la actividad
        }
    }
}
