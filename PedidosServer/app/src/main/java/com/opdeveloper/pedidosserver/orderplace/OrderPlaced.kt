package com.opdeveloper.pedidosserver.orderplace

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.*
import com.opdeveloper.pedidosserver.R
import com.opdeveloper.pedidosserver.db.models.Request
import com.opdeveloper.pedidosserver.orderplace.viewholder.OrderPlacedAdapter
import kotlinx.android.synthetic.main.activity_order_placed.*

class OrderPlaced : AppCompatActivity() {

    lateinit var database: FirebaseFirestore
    lateinit var reference: CollectionReference
    private var listOrders : MutableList<Request> = mutableListOf()
    private lateinit var adapter: OrderPlacedAdapter

    lateinit var spinnerStatus: Spinner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_placed)
        database = FirebaseFirestore.getInstance()
        reference = database.collection("Request")

        recycler_order_status.setHasFixedSize(true)
        recycler_order_status.layoutManager = LinearLayoutManager(this)

        loadOrdersStatus()
    }

    private fun loadOrdersStatus() {



            reference.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    // Handle error
                    //...

                }
                listOrders = querySnapshot!!.toObjects(Request::class.java)

                adapter = OrderPlacedAdapter(listOrders, this@OrderPlaced)
                adapter.notifyDataSetChanged()
                recycler_order_status.adapter = adapter
            }





    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.title.equals("Actualizar")){

            showUpdateDialog(adapter!!.request[item.order].requestId, adapter!!.request[item.order])
        }else if (item.title.equals("Borrar")){
            deleteOrder(adapter!!.request[item.order].requestId)
        }
        return super.onContextItemSelected(item)
    }

    private fun deleteOrder(requestId: String?) {
        val query = FirebaseFirestore.getInstance()
            .collection("Request").document(requestId!!)
        /*var image: String? = null
        query.get().addOnSuccessListener {
            image = it["image"].toString()
        }*/
        query.delete().addOnSuccessListener {
            adapter.notifyDataSetChanged()
        }

        Toast.makeText(this, "Orden borrado", Toast.LENGTH_LONG).show()
    }

    private fun showUpdateDialog(requestId: String?, request: Request) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Actualizar Orden")
        alertDialog.setMessage("Ingrese el nuevo estado del pedido")

        val inflater = this.layoutInflater

        val view =  inflater.inflate(R.layout.dialog_order_update, null)

        spinnerStatus = view.findViewById(R.id.statusSpinner)


        ArrayAdapter.createFromResource(
            this,
            R.array.pedido_update,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinnerStatus.adapter = adapter
        }
        alertDialog.setView(view)
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp)

        alertDialog.setPositiveButton( "Guardar",object: DialogInterface.OnClickListener{
            override fun onClick(p0: DialogInterface?, p1: Int) {
                p0!!.dismiss()

                val query = FirebaseFirestore.getInstance()
                    .collection("Request").document(requestId!!)


                val status = converStatus(spinnerStatus.selectedItem.toString())

                query.update("status", status).addOnSuccessListener {

                    adapter.notifyDataSetChanged()
                    Snackbar.make(orderLayout, "Ordern Actualizada:"+ requestId, Snackbar.LENGTH_LONG)
                }


            }

        })

        alertDialog.setNegativeButton( "Cancelar",object: DialogInterface.OnClickListener{
            override fun onClick(p0: DialogInterface?, p1: Int) {
                p0!!.dismiss()
            }

        })

        alertDialog.show()
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
