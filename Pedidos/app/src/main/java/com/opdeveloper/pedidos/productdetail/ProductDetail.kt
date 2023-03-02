package com.opdeveloper.pedidos.productdetail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.dbflow5.structure.save
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

import com.opdeveloper.pedidos.R
import com.opdeveloper.pedidos.db.models.Order
import com.opdeveloper.pedidos.db.models.OrderDb
import com.opdeveloper.pedidos.db.models.Product
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_product_detail.*
import kotlinx.android.synthetic.main.content_product_detail.*

class ProductDetail : AppCompatActivity() {

    lateinit var database: FirebaseFirestore
    lateinit var reference: CollectionReference
    var productoId: String? = null
    lateinit var currentProduct : Product
    var cantidadProduct = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        database = FirebaseFirestore.getInstance()
        reference = database.collection("Productos")

        collasing_detail.setExpandedTitleTextAppearance(R.style.ExpandedAppbar)
        collasing_detail.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar)

        if (intent != null) productoId = intent.getStringExtra("ProductoId")

        if (!productoId.isNullOrEmpty()){
            LoadDetail(productoId!!)
        }

        btnCart.setOnClickListener {
            val order = OrderDb()
            order.ProductoId = productoId
            order.ProductoName = currentProduct.Name
            order.Precio = currentProduct.Precio
            order.Cantidad = cantidadProduct
            order.Descuento = null
            order.save()
            Log.v("Agregar", order.toString())

            Toast.makeText(this, "Producto agregado al carrito", Toast.LENGTH_LONG).show()
        }

        less.setOnClickListener {
            cantidadProduct--
            if (cantidadProduct == 0) cantidadProduct = 1

            cantidad.text = cantidadProduct.toString()
        }

        plus.setOnClickListener {
            cantidadProduct++
            cantidad.text = cantidadProduct.toString()
        }

    }

    private fun LoadDetail(productoId: String) {

        reference.whereEqualTo("productoId", productoId).get().addOnSuccessListener {
            task->
            val producto = task.toObjects(Product::class.java)
            currentProduct = producto.first()
            Log.v("ProductoDetail", producto.toString())

            Picasso.get().load(producto.first().Image).into(img_product_detail)

            collasing_detail.title = producto.first().Name
            product_detail_name.text = producto.first().Name
            product_detail_precio.text = producto.first().Precio.toString()
            product_detail_descripcion.text = producto.first().Descripcion


        }
        }

}
