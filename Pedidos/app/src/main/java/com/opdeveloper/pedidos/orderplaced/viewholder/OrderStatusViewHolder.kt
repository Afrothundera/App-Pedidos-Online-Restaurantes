package com.opdeveloper.pedidos.orderplaced.viewholder

import android.view.View
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.opdeveloper.pedidos.R
import com.opdeveloper.pedidos.interfaces.OnItemClick

class OrderStatusViewHolder(v:View) : RecyclerView.ViewHolder(v), View.OnClickListener {

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
        v.setOnClickListener(this)
    }
    override fun onClick(p0: View?) {
        itemClicklistener.onClick(p0!!, adapterPosition, false)
    }
}