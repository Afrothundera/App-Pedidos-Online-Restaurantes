package com.opdeveloper.pedidos.home.viewholder

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.opdeveloper.pedidos.R
import com.opdeveloper.pedidos.db.models.Category
import com.opdeveloper.pedidos.interfaces.OnItemClick
import com.opdeveloper.pedidos.showproductbycategory.ShowProductsByCategory
import com.squareup.picasso.Picasso


class InicioViewHolderAdapter(val listCategory:List<Category>, val context: Context): RecyclerView.Adapter<InicioViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InicioViewHolder {
        val view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.home_item, parent, false)

        return  InicioViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listCategory.size
    }

    override fun onBindViewHolder(holder: InicioViewHolder, position: Int) {
        holder.apply {
            categoryName.text = listCategory[position].Name

            Picasso.get().load(listCategory[position].Image).into(image)

            itemClicklistener = object : OnItemClick {
                override fun onClick(view: View, position: Int, isLongClick: Boolean) {
                    val intent = Intent(context, ShowProductsByCategory::class.java)
                    intent.putExtra("CategoryId", listCategory[position].Id)
                    context.startActivity(intent)
                }
            }
        }
    }
}