package com.opdeveloper.pedidos.home.viewholder


import android.view.View

import android.widget.ImageView
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView

import com.opdeveloper.pedidos.R

import com.opdeveloper.pedidos.interfaces.OnItemClick


class InicioViewHolder(v:View) : RecyclerView.ViewHolder(v), View.OnClickListener {

    var categoryName:TextView
    var image:ImageView

    lateinit var itemClicklistener : OnItemClick

    init {
        categoryName = v.findViewById(R.id.categoryName)
        image = v.findViewById(R.id.categoryImage)
        v.setOnClickListener(this)
    }
    override fun onClick(p0: View?) {
        itemClicklistener.onClick(p0!!, adapterPosition, false)
    }
}