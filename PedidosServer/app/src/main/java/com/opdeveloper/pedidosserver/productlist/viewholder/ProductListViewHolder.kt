package com.opdeveloper.pedidosserver.productlist.viewholder

import android.view.ContextMenu
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.opdeveloper.pedidosserver.R
import com.opdeveloper.pedidosserver.interfaces.OnItemClick

class ProductListViewHolder(v: View): RecyclerView.ViewHolder(v), View.OnClickListener, View.OnCreateContextMenuListener  {

    var productName: TextView
    var image: ImageView
    var precio: TextView
    var descripcion: TextView


    lateinit var itemClicklistener : OnItemClick

    init {
        productName = v.findViewById(R.id.txt_name)
        image = v.findViewById(R.id.product_by_Category_Image)
        precio = v.findViewById(R.id.txt_price)
        descripcion = v.findViewById(R.id.txt_descripcion)
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