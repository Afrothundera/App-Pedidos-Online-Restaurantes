package com.opdeveloper.pedidos.car

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dbflow5.config.database
import com.dbflow5.config.databaseForTable
import com.dbflow5.query.Delete
import com.dbflow5.query.list
import com.dbflow5.query.select
import com.dbflow5.structure.delete
import com.dbflow5.transaction.fastDelete
import com.google.android.gms.common.internal.service.Common
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.opdeveloper.pedidos.R
import com.opdeveloper.pedidos.car.viewholder.CarAdapter
import com.opdeveloper.pedidos.db.models.Order
import com.opdeveloper.pedidos.db.models.OrderDb
import com.opdeveloper.pedidos.db.models.OrderDb_Table
import com.opdeveloper.pedidos.db.models.Request
import kotlinx.android.synthetic.main.activity_car.*
import java.text.NumberFormat
import java.util.*
import kotlin.collections.HashMap

class CarActivity : AppCompatActivity() {

    lateinit var database: FirebaseFirestore
    lateinit var reference: CollectionReference
    private lateinit var auth: FirebaseAuth
    var car: MutableList<Order> = mutableListOf()
    var order: MutableList<OrderDb> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_car)
        database = FirebaseFirestore.getInstance()
        reference = database.collection("Request")
        auth = FirebaseAuth.getInstance()
        recycler_car.setHasFixedSize(true)
        recycler_car.layoutManager = LinearLayoutManager(this)
        btnRealizarPedido.isEnabled = false
        loadOrderCar()
        btnRealizarPedido.setOnClickListener{
            showAlertDialog()
        }
    }

    private fun showAlertDialog() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Datos de direccion de envio")
        alertDialog.setMessage("Ingrese su direccion")

        val edtAddress = EditText(this)
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT)

        edtAddress.layoutParams = lp
        alertDialog.setView(edtAddress)
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp)
        alertDialog.setPositiveButton("Ok", object: DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                val orderPlace = Request(
                    null,
                    auth.currentUser?.phoneNumber,
                    auth.currentUser?.displayName,
                    edtAddress.text.toString(),
                    total.text.toString(),
                    "0",
                    auth.currentUser?.email,
                    auth.currentUser?.uid,
                    null,
                    car)

                reference.add(orderPlace).addOnSuccessListener {
                    val hashMap = hashMapOf<String, Any>(
                            "requestId" to it.id,
                            "timestamp" to FieldValue.serverTimestamp()
                    )

                    it.update(hashMap).addOnCompleteListener {
                        Toast.makeText(this@CarActivity, "Su orden ha sido realizada proceda a realizar el pago de manera manual", Toast.LENGTH_LONG).show()
                        //order.delete()
                        //com.dbflow5.query.delete(OrderDb::class.java)

                        (select from OrderDb::class).list.forEach {
                            it.delete()
                        }

                        finish()

                    }


                }


            }

        })
        alertDialog.setNegativeButton("Cancelar", object: DialogInterface.OnClickListener{
            override fun onClick(p0: DialogInterface?, p1: Int) {
                p0!!.dismiss()
            }

        })
        alertDialog.show()
    }

    private fun loadOrderCar() {

        databaseForTable<OrderDb> {

            order = (select from OrderDb::class).queryList(this)



            val adapter = CarAdapter(order, this@CarActivity)
            recycler_car.adapter = adapter
            adapter.notifyDataSetChanged()
            var totalcar:Double = 0.0
            car.clear()
            for (item in order){
                car.add(Order(item.ProductoId, item.ProductoName, item.Cantidad, item.Precio, item.Descuento))
                totalcar += item.Precio!! * item.Cantidad!!.toDouble()
            }
            val locale = Locale("en", "US")
            val format = NumberFormat.getCurrencyInstance(locale)
            total.text = format.format(totalcar)
            btnRealizarPedido.isEnabled = true
        }
        //car = (select from OrderDb)
    }
}
