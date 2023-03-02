package com.opdeveloper.pedidosserver.productlist

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.storage.FirebaseStorage
import com.opdeveloper.pedidosserver.R
import com.opdeveloper.pedidosserver.db.models.Category
import com.opdeveloper.pedidosserver.db.models.Product
import com.opdeveloper.pedidosserver.productlist.viewholder.ProductListAdapter
import kotlinx.android.synthetic.main.activity_product_list.*
import kotlinx.android.synthetic.main.content_product_list.*
import java.util.*

class ProductList : AppCompatActivity() {

    private var adapter:ProductListAdapter? = null
    private var productList = mutableListOf<Product>()
    internal var categoryId: String? = null
    private val db = FirebaseFirestore.getInstance()
    private  var query: Query? = null

    private lateinit var storage: FirebaseStorage
    var uriSaver: Uri? = null

    lateinit var edtProductName: EditText
    lateinit var edtProductDes: EditText
    lateinit var edtProductPrecio: EditText

    lateinit var selectImage: Button
    lateinit var selectUpload: Button
    lateinit var dialogProgressBar: ProgressBar
    lateinit var progressUpload: ProgressBar
    private val PICK_IMAGE_REQUEST = 71
    var newProduct: Product? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)
        val toolbar: Toolbar = findViewById(R.id.toolbar_showByCategory)
        setSupportActionBar(toolbar)
        storage = FirebaseStorage.getInstance()

        fabListProduct.setOnClickListener {
            showDialog()
        }
        if (intent != null) categoryId = intent.getStringExtra("CategoryId")

        if (!categoryId.isNullOrEmpty()){

            recycler_productList.setHasFixedSize(true)
            val mLayoutManager = LinearLayoutManager(applicationContext)
            recycler_productList.setHasFixedSize(true)
            recycler_productList.layoutManager = mLayoutManager
            recycler_productList.itemAnimator = DefaultItemAnimator()
            loadProducts(categoryId!!)

        }
    }

    private fun loadProducts(categoryId: String) {

        query = FirebaseFirestore.getInstance()
            .collection("Productos").whereEqualTo("categoryId", categoryId)

        query!!.addSnapshotListener(object: EventListener<QuerySnapshot> {
            override fun onEvent(p0: QuerySnapshot?, p1: FirebaseFirestoreException?) {
                if (p1 != null) {
                    // Handle error
                    //...
                    return
                }
                productList = mutableListOf()

                // Convert query snapshot to a list of chats
                productList = p0!!.toObjects(Product::class.java)

                adapter = ProductListAdapter(productList, this@ProductList)
                adapter!!.notifyDataSetChanged()
                recycler_productList.adapter = adapter
            }

        })




    }

    private fun showDialog() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Nuevo Producto")
        alertDialog.setMessage("Ingrese la informacion del producto")

        val inflater = this.layoutInflater

        val view =  inflater.inflate(R.layout.dialog_new_product, null)

        edtProductName = view.findViewById(R.id.edtProductName)
        edtProductDes = view.findViewById(R.id.edtProductDes)
        edtProductPrecio = view.findViewById(R.id.edtProductPrecio)

        selectImage = view.findViewById(R.id.selectImage)
        selectUpload = view.findViewById(R.id.selectUpload)
        dialogProgressBar = view.findViewById(R.id.dialogProgressBar)
        dialogProgressBar.visibility = View.GONE
        progressUpload = view.findViewById(R.id.progressUpload)
        selectImage.setOnClickListener {
            chooseImage()
        }

        selectUpload.setOnClickListener {
            uploadImage()
        }
        alertDialog.setView(view)
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp)

        alertDialog.setPositiveButton( "Guardar",object: DialogInterface.OnClickListener{
            override fun onClick(p0: DialogInterface?, p1: Int) {
                p0!!.dismiss()
                if (newProduct != null){
                    val query = FirebaseFirestore.getInstance()
                        .collection("Productos")


                    query.add(newProduct!!).addOnSuccessListener {
                        val id = it.id
                        it.update("productoId", id).addOnSuccessListener {
                            uriSaver = null
                        }

                    }

                    Snackbar.make(rootLayout, "Producto creado:"+ edtProductName.text.toString(), Snackbar.LENGTH_LONG)
                }
            }

        })

        alertDialog.setNegativeButton( "Cancelar",object: DialogInterface.OnClickListener{
            override fun onClick(p0: DialogInterface?, p1: Int) {
                uriSaver = null
                p0!!.dismiss()
            }

        })

        alertDialog.show()
    }

    private fun uploadImage() {
        if (uriSaver != null && !edtProductName.text.isNullOrEmpty() && !edtProductDes.text.isNullOrEmpty() && !edtProductPrecio.text.isNullOrEmpty()){

            dialogProgressBar.visibility = View.VISIBLE
            progressUpload.visibility = View.VISIBLE
            val stringName = UUID.randomUUID().toString()
            var storageReference = storage.reference
            val imageRef = storageReference.child("images/"+stringName)
            imageRef.putFile(uriSaver!!).addOnSuccessListener {
                dialogProgressBar.visibility = View.GONE

                Toast.makeText(this, "Imagen Subida", Toast.LENGTH_LONG).show()
                imageRef.downloadUrl.addOnSuccessListener {
                    newProduct = Product(categoryId, edtProductDes.text.toString(), it.toString(), edtProductName.text.toString(), edtProductPrecio.text.toString().toDouble(), null)
                }
            }.addOnFailureListener {
                dialogProgressBar.visibility = View.GONE
                progressUpload.visibility = View.GONE
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
            }.addOnProgressListener {
                val progress = 100* it.bytesTransferred / it.totalByteCount
                Log.v("Progress", progress.toString())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    progressUpload.setProgress(progress.toInt(), true)
                }else{
                    progressUpload.progress = progress.toInt()
                }
            }.addOnCompleteListener {
                progressUpload.visibility = View.GONE

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null){
            uriSaver = data.data
            selectImage.text = "Imagen seleccionada"
            selectUpload.isEnabled = true
        }
    }

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(intent,"Selecciona la imagen" ), PICK_IMAGE_REQUEST)
    }

    private fun showUpdateDialog(id: String?, product: Product) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Actualizar Producto")
        alertDialog.setMessage("Ingrese la informacion del producto")

        val inflater = this.layoutInflater

        val view =  inflater.inflate(R.layout.dialog_new_product, null)

        edtProductName = view.findViewById(R.id.edtProductName)
        edtProductDes = view.findViewById(R.id.edtProductDes)
        edtProductPrecio = view.findViewById(R.id.edtProductPrecio)

        selectImage = view.findViewById(R.id.selectImage)
        selectUpload = view.findViewById(R.id.selectUpload)
        dialogProgressBar = view.findViewById(R.id.dialogProgressBar)
        dialogProgressBar.visibility = View.GONE
        progressUpload = view.findViewById(R.id.progressUpload)

        edtProductName.setText(product.Name)
        edtProductDes.setText(product.Descripcion)
        edtProductPrecio.setText(product.Precio.toString())

        selectImage.setOnClickListener {
            chooseImage()
        }

        selectUpload.setOnClickListener {
            changeImage(product)
        }
        alertDialog.setView(view)
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp)

        alertDialog.setPositiveButton( "Guardar",object: DialogInterface.OnClickListener{
            override fun onClick(p0: DialogInterface?, p1: Int) {
                p0!!.dismiss()
                product.Name = edtProductName.text.toString()
                product.Precio = edtProductPrecio.text.toString().toDouble()
                product.Descripcion = edtProductDes.text.toString()
                val query = FirebaseFirestore.getInstance()
                    .collection("Productos").document(id!!)


                query.set(product).addOnSuccessListener {

                    Snackbar.make(rootLayout, "Producto Actualizada:"+ edtProductName.text.toString(), Snackbar.LENGTH_LONG)
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

    private fun deleteCategory(id: String?) {
        val query = FirebaseFirestore.getInstance()
            .collection("Productos").document(id!!)
        /*var image: String? = null
        query.get().addOnSuccessListener {
            image = it["image"].toString()
        }*/
        query.delete()

        Toast.makeText(this, "Producto borrada", Toast.LENGTH_LONG).show()
    }

    private fun changeImage(product: Product) {
        if (uriSaver != null && !edtProductName.text.isNullOrEmpty() && !edtProductDes.text.isNullOrEmpty() && !edtProductPrecio.text.isNullOrEmpty()){


            dialogProgressBar.visibility = View.VISIBLE
            progressUpload.visibility = View.VISIBLE
            val stringName = UUID.randomUUID().toString()
            var storageReference = storage.reference
            val imageRef = storageReference.child("images/"+stringName)
            imageRef.putFile(uriSaver!!).addOnSuccessListener {
                dialogProgressBar.visibility = View.GONE

                Toast.makeText(this, "Imagen Subida", Toast.LENGTH_LONG).show()
                imageRef.downloadUrl.addOnSuccessListener {
                    product.Image = it.toString()
                }
            }.addOnFailureListener {
                dialogProgressBar.visibility = View.GONE
                progressUpload.visibility = View.GONE
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
            }.addOnProgressListener {
                val progress = 100* it.bytesTransferred / it.totalByteCount
                Log.v("Progress", progress.toString())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    progressUpload.setProgress(progress.toInt(), true)
                }else{
                    progressUpload.progress = progress.toInt()
                }
            }.addOnCompleteListener {
                dialogProgressBar.visibility = View.GONE
                progressUpload.visibility = View.GONE
            }
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.title.equals("Actualizar")){

            showUpdateDialog(adapter!!.listProduct[item.order].ProductoId, adapter!!.listProduct[item.order])
        }else if (item.title.equals("Borrar")){
            deleteCategory(adapter!!.listProduct[item.order].ProductoId)
        }
        return super.onContextItemSelected(item)
    }
}
