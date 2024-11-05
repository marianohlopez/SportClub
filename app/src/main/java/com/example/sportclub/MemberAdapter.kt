package com.example.sportclub

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MemberAdapter(private val memberList: List<Member>) :
    RecyclerView.Adapter<MemberAdapter.MemberViewHolder>() {

    class MemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val firstNameTextView: TextView = itemView.findViewById(R.id.nameHeader)
        val lastNameTextView: TextView = itemView.findViewById(R.id.lastnameHeader)
        val documentTextView: TextView = itemView.findViewById(R.id.docHeader)
        val itemLayout: View = itemView // Referencia al layout principal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.member_item, parent, false)
        return MemberViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val member = memberList[position]
        holder.firstNameTextView.text = member.firstName
        holder.lastNameTextView.text = member.lastName
        holder.documentTextView.text = member.document.toString()

        // Cambia el color de fondo si el miembro está activo
        if (member.isActive == 0) {
            holder.itemLayout.setBackgroundColor(Color.YELLOW) // Cambiar color a amarillo
        } else {
            holder.itemLayout.setBackgroundColor(Color.WHITE) // Fondo por defecto
        }
    }

    override fun getItemCount(): Int {
        return memberList.size
    }
}

