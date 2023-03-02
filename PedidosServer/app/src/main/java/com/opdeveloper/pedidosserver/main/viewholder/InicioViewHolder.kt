package com.opdeveloper.pedidosserver.main.viewholder


import android.view.ContextMenu
import android.view.View

import android.widget.ImageView
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.opdeveloper.pedidosserver.R
import com.opdeveloper.pedidosserver.interfaces.OnItemClick


class InicioViewHolder(v:View) : RecyclerView.ViewHolder(v), View.OnClickListener, View.OnCreateContextMenuListener {


    var categoryName:TextView
    var image:ImageView

    lateinit var itemClicklistener : OnItemClick

    init {
        categoryName = v.findViewById(R.id.categoryName)
        image = v.findViewById(R.id.categoryImage)
        v.setOnCreateContextMenuListener(this)
        v.setOnClickListener(this)
    }
    override fun onClick(p0: View?) {
        itemClicklistener.onClick(p0!!, adapterPosition, false)
    }

    override fun onCreateContextMenu(contextMenu: ContextMenu?, p1: View?, p2: ContextMenu.ContextMenuInfo?) {
        contextMenu!!.setHeaderTitle("Selecciona la accion")

        contextMenu.add(0,0,adapterPosition, "Actualizar")
        contextMenu.add(0,1,adapterPosition, "Borrar")
    }
}