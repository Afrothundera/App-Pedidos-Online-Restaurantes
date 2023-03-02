package com.opdeveloper.pedidosserver.main

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.storage.FirebaseStorage
import com.opdeveloper.pedidosserver.R
import com.opdeveloper.pedidosserver.db.models.Category
import com.opdeveloper.pedidosserver.main.viewholder.InicioViewHolderAdapter
import com.opdeveloper.pedidosserver.orderplace.OrderPlaced
import com.opdeveloper.pedidosserver.services.ListenService
import kotlinx.android.synthetic.main.content_main.*
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    //private val db = FirebaseFirestore.getInstance()


    private lateinit var auth: FirebaseAuth
    private lateinit var navView: NavigationView
    private var adapter: InicioViewHolderAdapter? = null
    private var categoryList = mutableListOf<Category>()
    private var firestoreListener: ListenerRegistration? = null
    private lateinit var storage:FirebaseStorage

    var uriSaver: Uri? = null

    private val PICK_IMAGE_REQUEST = 71
    lateinit var edtNewCategory: EditText
    lateinit var selectImage: Button
    lateinit var selectUpload: Button
    lateinit var dialogProgressBar: ProgressBar
    lateinit var progressUpload: ProgressBar

    lateinit var drawerLayout: DrawerLayout
    var newCategory: Category? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.title = "Inicio"
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            showDialog()
        }
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)
        getHeaderComponentDraw()
        storage = FirebaseStorage.getInstance()
        recycler_menu.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(applicationContext)
        recycler_menu.setHasFixedSize(true)
        recycler_menu.layoutManager = mLayoutManager as RecyclerView.LayoutManager?
        recycler_menu.itemAnimator = DefaultItemAnimator()

        loadMenu()
        val serviceIntent = Intent(this, ListenService::class.java)
        startService(serviceIntent)
    }

    private fun showDialog() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Nueva Categoria")
        alertDialog.setMessage("Ingrese el nombre de la nueva categoria")

        val inflater = this.layoutInflater

        val view =  inflater.inflate(R.layout.dialog_new_category, null)

        edtNewCategory = view.findViewById(R.id.edtNewCategory)
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
                if (newCategory != null){
                    val query = FirebaseFirestore.getInstance()
                        .collection("Category")


                    query.add(newCategory!!).addOnSuccessListener {
                        val id = it.id
                        it.update("id", id).addOnSuccessListener {
                            uriSaver = null
                        }

                    }

                    Snackbar.make(drawerLayout, "Categoria creada:"+ edtNewCategory.text.toString(), Snackbar.LENGTH_LONG)
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

    private fun uploadImage() {
        if (uriSaver != null && !edtNewCategory.text.isNullOrEmpty()){

            dialogProgressBar.visibility = View.VISIBLE
            progressUpload.visibility = View.VISIBLE
            val stringName = UUID.randomUUID().toString()
            var storageReference = storage.reference
            val imageRef = storageReference.child("images/"+stringName)
            imageRef.putFile(uriSaver!!).addOnSuccessListener {
                dialogProgressBar.visibility = View.GONE

                Toast.makeText(this, "Imagen Subida", Toast.LENGTH_LONG).show()
                imageRef.downloadUrl.addOnSuccessListener {
                    newCategory = Category(edtNewCategory.text.toString(), it.toString(), null)
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


    private fun loadMenu() {
        val query = FirebaseFirestore.getInstance()
            .collection("Category")

        query.addSnapshotListener(object: EventListener<QuerySnapshot> {
            override fun onEvent(p0: QuerySnapshot?, p1: FirebaseFirestoreException?) {
                if (p1 != null) {
                    // Handle error
                    //...
                    return
                }
                categoryList = mutableListOf()
                // Convert query snapshot to a list of chats
                categoryList = p0!!.toObjects(Category::class.java)

                adapter = InicioViewHolderAdapter(categoryList, this@MainActivity)
                adapter!!.notifyDataSetChanged()
                recycler_menu.adapter = adapter

            }

        })




        /*firestoreListener = db!!.collection("Category")
            .addSnapshotListener(EventListener { documentSnapshots, e ->
                if (e != null) {

                    return@EventListener
                }

                categoryList = mutableListOf()

                if (documentSnapshots != null) {
                    for (doc in documentSnapshots) {
                        val note = doc.toObject(Category::class.java)
                        //note.Name = doc.
                        categoryList.add(note)
                    }
                }

                adapter!!.notifyDataSetChanged()
                recycler_menu.adapter = adapter
            })*/
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_menu -> {
                // Handle the camera action
            }
            R.id.nav_car -> {
                /*val intent = Intent(this, CarActivity::class.java)
                startActivity(intent)*/
            }
            R.id.nav_orders ->{
                val intent = Intent(this, OrderPlaced::class.java)
                startActivity(intent)
            }
            R.id.nav_log_out -> {
                /*auth.signOut()
                val intent = Intent(this, WelcomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)*/
            }

        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun getHeaderComponentDraw(){
        /*val headerView = navView.getHeaderView(0)
        val emailDisplay = headerView.findViewById<TextView>(R.id.emailDisplay)
        if (auth.currentUser != null) emailDisplay.text = auth.currentUser!!.email*/
    }
    private fun getHeaderComponentDraw(currentUser: FirebaseUser) {
        val headerView = navView.getHeaderView(0)
        val emailDisplay = headerView.findViewById<TextView>(R.id.emailDisplay)
        emailDisplay.text = currentUser.email
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.title.equals("Actualizar")){

            showUpdateDialog(adapter!!.listCategory[item.order].Id, adapter!!.listCategory[item.order])
        }else if (item.title.equals("Borrar")){
            deleteCategory(adapter!!.listCategory[item.order].Id)
        }
        return super.onContextItemSelected(item)
    }

    private fun showUpdateDialog(name: String?, category: Category) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Actualizar Categoria")
        alertDialog.setMessage("Ingrese el nombre de la categoria")

        val inflater = this.layoutInflater

        val view =  inflater.inflate(R.layout.dialog_new_category, null)

        edtNewCategory = view.findViewById(R.id.edtNewCategory)
        selectImage = view.findViewById(R.id.selectImage)
        selectUpload = view.findViewById(R.id.selectUpload)
        dialogProgressBar = view.findViewById(R.id.dialogProgressBar)
        dialogProgressBar.visibility = View.GONE
        progressUpload = view.findViewById(R.id.progressUpload)

        edtNewCategory.setText(category.Name)
        selectImage.setOnClickListener {
            chooseImage()
        }

        selectUpload.setOnClickListener {
            changeImage(category)
        }
        alertDialog.setView(view)
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp)

        alertDialog.setPositiveButton( "Guardar",object: DialogInterface.OnClickListener{
            override fun onClick(p0: DialogInterface?, p1: Int) {
                p0!!.dismiss()
                category.Name = edtNewCategory.text.toString()
                    val query = FirebaseFirestore.getInstance()
                        .collection("Category").document(name!!)


                query.set(category).addOnSuccessListener {

                    Snackbar.make(drawerLayout, "Categoria Actualizada:"+ edtNewCategory.text.toString(), Snackbar.LENGTH_LONG)
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
            .collection("Category").document(id!!)
        /*var image: String? = null
        query.get().addOnSuccessListener {
            image = it["image"].toString()
        }*/
        query.delete()

        Toast.makeText(this, "Categoria borrada", Toast.LENGTH_LONG).show()
    }

    private fun changeImage(category: Category) {
        if (uriSaver != null && !edtNewCategory.text.isNullOrEmpty()){

            dialogProgressBar.visibility = View.VISIBLE
            progressUpload.visibility = View.VISIBLE
            val stringName = UUID.randomUUID().toString()
            var storageReference = storage.reference
            val imageRef = storageReference.child("images/"+stringName)
            imageRef.putFile(uriSaver!!).addOnSuccessListener {
                dialogProgressBar.visibility = View.GONE

                Toast.makeText(this, "Imagen Subida", Toast.LENGTH_LONG).show()
                imageRef.downloadUrl.addOnSuccessListener {
                    category.Image = it.toString()
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
}
