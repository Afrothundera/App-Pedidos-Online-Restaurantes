package com.opdeveloper.pedidosserver.orderplace.viewholder

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.opdeveloper.pedidosserver.R
import com.opdeveloper.pedidosserver.db.models.Request
import com.opdeveloper.pedidosserver.interfaces.OnItemClick
import com.opdeveloper.pedidosserver.maps.MapsActivity
import com.opdeveloper.pedidosserver.productlist.ProductList
import java.text.SimpleDateFormat
import java.util.*

class OrderPlacedAdapter(val request: List<Request>, val context: Context) : RecyclerView.Adapter<OrderPlacedViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderPlacedViewHolder {
        val inflater = LayoutInflater.from(context).inflate(R.layout.order_placed_item, parent, false)
        return OrderPlacedViewHolder(inflater)
    }

    override fun getItemCount(): Int {
        return request.size
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: OrderPlacedViewHolder, position: Int) {
        holder.apply {
            orderId.text = request[position].requestId
            if (request[position].timestamp != null){
                val date= Date(request[position].timestamp!!.seconds)
                val sfd =  SimpleDateFormat("dd-MM-yyyy HH:mm:ss")

                orderTime.text = sfd.format(date)
            }

            orderStatus.text = converStatus(request[position].status)
            orderPhone.text = request[position].phone
            orderAddress.text = request[position].address
            orderEmail.text = request[position].email

            itemClicklistener = object :OnItemClick{
                override fun onClick(view: View, position: Int, isLongClick: Boolean) {
                    val intent = Intent(context, MapsActivity::class.java)
                    intent.putExtra("RequestId", request[position].requestId)
                    context.startActivity(intent)
                }
            }
        }


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