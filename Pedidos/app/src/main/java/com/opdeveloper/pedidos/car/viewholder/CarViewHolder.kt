package com.opdeveloper.pedidos.car.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.opdeveloper.pedidos.R

class CarViewHolder(v:View) : RecyclerView.ViewHolder(v), View.OnClickListener {

    var carItemName: TextView
    var carItemPrice: TextView
    var carItemCount: ImageView
    init {
        carItemCount = v.findViewById(R.id.car_item_count)
        carItemPrice = v.findViewById(R.id.cart_item_precio)
        carItemName = v.findViewById(R.id.cart_item_name)
    }
    override fun onClick(p0: View?) {

    }

}