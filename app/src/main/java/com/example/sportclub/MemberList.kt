package com.example.sportclub

import android.os.Bundle
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
    }
}
