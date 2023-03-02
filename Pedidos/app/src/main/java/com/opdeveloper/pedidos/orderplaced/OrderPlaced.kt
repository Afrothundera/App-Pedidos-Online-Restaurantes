package com.opdeveloper.pedidos.orderplaced

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.opdeveloper.pedidos.R
import com.opdeveloper.pedidos.db.models.Request
import com.opdeveloper.pedidos.orderplaced.viewholder.OrderStatusAdapter
import kotlinx.android.synthetic.main.activity_order_placed.*

class OrderPlaced : AppCompatActivity() {

    lateinit var database: FirebaseFirestore
    lateinit var reference: CollectionReference
    private lateinit var auth: FirebaseAuth

    private var listOrders : MutableList<Request> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_placed)
        database = FirebaseFirestore.getInstance()
        reference = database.collection("Request")
        auth = FirebaseAuth.getInstance()

        recycler_order_status.setHasFixedSize(true)
        recycler_order_status.layoutManager = LinearLayoutManager(this)

        loadOrdersStatus(auth.currentUser?.uid)
    }

    private fun loadOrdersStatus(uid: String?) {
        Log.v("UID", uid!!)
        if (uid != null){
            Log.v("UID", uid)
            reference.whereEqualTo("userId", uid).addSnapshotListener(object:
                EventListener<QuerySnapshot>{
                override fun onEvent(p0: QuerySnapshot?, p1: FirebaseFirestoreException?) {
                    if (p1 != null) return
                    listOrders = p0!!.toObjects(Request::class.java)
                    Log.v("ListOrders", listOrders.toString())
                    val adapter = OrderStatusAdapter(listOrders, this@OrderPlaced)
                    recycler_order_status.adapter = adapter
                }

            })
        }
    }
}
