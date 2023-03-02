package com.opdeveloper.pedidos.showproductbycategory.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.opdeveloper.pedidos.R
import com.opdeveloper.pedidos.interfaces.OnItemClick

class ProductByCategoryViewHolder(v:View): RecyclerView.ViewHolder(v), View.OnClickListener {

    var productName: TextView
    var image: ImageView
    var precio:TextView
    var descripcion: TextView


    lateinit var itemClicklistener : OnItemClick

    init {
        productName = v.findViewById(R.id.txt_name)
        image = v.findViewById(R.id.product_by_Category_Image)
        precio = v.findViewById(R.id.txt_price)
        descripcion = v.findViewById(R.id.txt_descripcion)
        v.setOnClickListener(this)
    }
    override fun onClick(p0: View?) {
        itemClicklistener.onClick(p0!!, adapterPosition, false)
    }
}