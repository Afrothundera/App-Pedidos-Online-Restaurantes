package com.opdeveloper.pedidosserver.productlist.viewholder

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.opdeveloper.pedidosserver.R
import com.opdeveloper.pedidosserver.db.models.Product
import com.opdeveloper.pedidosserver.interfaces.OnItemClick
import com.squareup.picasso.Picasso

class ProductListAdapter(var listProduct:List<Product>, val context: Context) : RecyclerView.Adapter<ProductListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductListViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.product_list_item, parent, false)

        return  ProductListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listProduct.size
    }

    override fun onBindViewHolder(holder: ProductListViewHolder, position: Int) {
        holder.apply {
            productName.text = listProduct[position].Name
            precio.text = listProduct[position].Precio.toString()


            Picasso.get().load(listProduct[position].Image).into(image)

            itemClicklistener = object : OnItemClick {
                override fun onClick(view: View, position: Int, isLongClick: Boolean) {

                    /*val intent = Intent(context, ProductDetail::class.java)
                    intent.putExtra("ProductoId", listProduct[position].ProductoId.toString())
                    context.startActivity(intent)*/
                }
            }
        }
    }
}