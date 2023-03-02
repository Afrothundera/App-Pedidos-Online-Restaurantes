package com.opdeveloper.pedidosserver.orderplace.viewholder

import android.view.ContextMenu
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.opdeveloper.pedidosserver.R
import com.opdeveloper.pedidosserver.interfaces.OnItemClick

class OrderPlacedViewHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener, View.OnCreateContextMenuListener {


    var orderId : TextView
    var orderStatus : TextView
    var orderPhone : TextView
    var orderAddress : TextView
    var orderEmail : TextView
    var orderTime : TextView
    lateinit var itemClicklistener : OnItemClick
    init {
        orderId = v.findViewById(R.id.order_item_id)
        orderStatus = v.findViewById(R.id.order_item_status)
        orderPhone = v.findViewById(R.id.order_item_phone)
        orderAddress = v.findViewById(R.id.order_item_address)
        orderEmail = v.findViewById(R.id.order_item_email)
        orderTime = v.findViewById(R.id.order_item_time)
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