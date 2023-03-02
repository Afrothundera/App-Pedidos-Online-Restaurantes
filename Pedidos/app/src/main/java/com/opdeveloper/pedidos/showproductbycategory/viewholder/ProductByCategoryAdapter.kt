package com.opdeveloper.pedidos.showproductbycategory.viewholder

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import androidx.recyclerview.widget.RecyclerView
import com.opdeveloper.pedidos.R
import com.opdeveloper.pedidos.db.models.Product
import com.opdeveloper.pedidos.interfaces.OnItemClick
import com.opdeveloper.pedidos.productdetail.ProductDetail
import com.squareup.picasso.Picasso

class ProductByCategoryAdapter(var listProduct:List<Product>, val context: Context) : RecyclerView.Adapter<ProductByCategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductByCategoryViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.show_product_by_category_item, parent, false)

        return  ProductByCategoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listProduct.size
    }

    override fun onBindViewHolder(holder: ProductByCategoryViewHolder, position: Int) {
        holder.apply {
            productName.text = listProduct[position].Name
            precio.text = listProduct[position].Precio.toString()


            Picasso.get().load(listProduct[position].Image).into(image)

            itemClicklistener = object : OnItemClick {
                override fun onClick(view: View, position: Int, isLongClick: Boolean) {

                    val intent = Intent(context, ProductDetail::class.java)
                    intent.putExtra("ProductoId", listProduct[position].ProductoId.toString())
                    context.startActivity(intent)
                }
            }
        }
    }
}