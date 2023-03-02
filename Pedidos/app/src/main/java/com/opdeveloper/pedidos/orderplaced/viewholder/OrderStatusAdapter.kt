package com.opdeveloper.pedidos.orderplaced.viewholder

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.opdeveloper.pedidos.R
import com.opdeveloper.pedidos.db.models.Request
import java.text.SimpleDateFormat
import java.util.*

class OrderStatusAdapter(val request: List<Request>, val context: Context) : RecyclerView.Adapter<OrderStatusViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderStatusViewHolder {
        val inflater = LayoutInflater.from(context).inflate(R.layout.order_status_item, parent, false)
        return OrderStatusViewHolder(inflater)
    }

    override fun getItemCount(): Int {
        return request.size
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: OrderStatusViewHolder, position: Int) {

        holder.orderId.text = request[position].requestId
        if (request[position].timestamp != null){
            val date= Date(request[position].timestamp!!.seconds)
            val sfd =  SimpleDateFormat("dd-MM-yyyy HH:mm:ss")

            holder.orderTime.text = sfd.format(date)
        }

        holder.orderStatus.text = converStatus(request[position].status)
        holder.orderPhone.text = request[position].phone
        holder.orderAddress.text = request[position].address
        holder.orderEmail.text = request[position].email
    }

    private fun converStatus(status: String?): CharSequence? {
        if (status.equals("0")){
            return "Creado"
        }else if (status.equals("1")){
            return "En camino"
        }else {
            return "Entregado"
        }
    }


}