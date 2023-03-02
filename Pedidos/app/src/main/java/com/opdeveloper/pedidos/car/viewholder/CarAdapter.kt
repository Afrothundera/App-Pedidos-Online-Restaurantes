package com.opdeveloper.pedidos.car.viewholder

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.opdeveloper.pedidos.R
import com.opdeveloper.pedidos.db.models.Order
import com.opdeveloper.pedidos.db.models.OrderDb
import java.text.NumberFormat
import java.util.*

class CarAdapter(val carList: List<OrderDb>, val context:Context) : RecyclerView.Adapter<CarViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val inflater = LayoutInflater.from(context).inflate(R.layout.car_item,parent, false)
        return CarViewHolder(inflater)
    }

    override fun getItemCount(): Int {
        return carList.size
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {

        val textDrawable = TextDrawable.builder().buildRound(""+ carList[position].Cantidad, Color.RED)
        Log.v("Adapter", carList[position].ProductoName!!)
        holder.carItemCount.setImageDrawable(textDrawable)
        val locale = Locale("en", "US")
        val format = NumberFormat.getCurrencyInstance(locale)
        var precio = carList[position].Precio!! * carList[position].Cantidad!!.toDouble()

        holder.carItemPrice.text = format.format(precio)
        holder.carItemName.text = carList[position].ProductoName
    }
}