package com.opdeveloper.pedidos.showproductbycategory

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.google.firebase.firestore.core.Filter
import com.opdeveloper.pedidos.R
import com.opdeveloper.pedidos.db.models.Product
import com.opdeveloper.pedidos.interfaces.OnItemClick
import com.opdeveloper.pedidos.productdetail.ProductDetail
import com.opdeveloper.pedidos.showproductbycategory.viewholder.ProductByCategoryAdapter
import com.opdeveloper.pedidos.showproductbycategory.viewholder.ProductByCategoryViewHolder
import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.content_show_products_by_category.*

class ShowProductsByCategory : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    internal var categoryId: String? = null
    private val db = FirebaseFirestore.getInstance()


    private  var auth: FirebaseAuth  = FirebaseAuth.getInstance()
    private lateinit var navView: NavigationView
    private var adapter:ProductByCategoryAdapter? = null
    private var productList = mutableListOf<Product>()
    private var firestoreListener: ListenerRegistration? = null
    private lateinit var searchViewMenu : MenuItem
    private lateinit var searchView: SearchView
    private  var query: Query? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_products_by_category)
        val toolbar: Toolbar = findViewById(R.id.toolbar_showByCategory)
        setSupportActionBar(toolbar)


        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout_showByCategory)
        navView = findViewById(R.id.nav_view_showByCategory)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)
        getHeaderComponentDraw()
        if (intent != null) categoryId = intent.getStringExtra("CategoryId")

        if (!categoryId.isNullOrEmpty()){

            recycler_productById.setHasFixedSize(true)
            val mLayoutManager = LinearLayoutManager(applicationContext)
            recycler_productById.setHasFixedSize(true)
            recycler_productById.layoutManager = mLayoutManager
            recycler_productById.itemAnimator = DefaultItemAnimator()
            loadProducts(categoryId!!)

        }

    }

    private fun loadProducts(categoryId: String) {
        Log.v("Categoria", categoryId)
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

                adapter = ProductByCategoryAdapter(productList, this@ShowProductsByCategory)
                adapter!!.notifyDataSetChanged()
                recycler_productById.adapter = adapter
            }

        })


        

    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout_showByCategory)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.show_products_by_category, menu)
        searchViewMenu = menu.findItem(R.id.action_search)
        searchView = searchViewMenu.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                    query = FirebaseFirestore.getInstance()
                        .collection("Productos").whereEqualTo("categoryId", categoryId).whereLessThanOrEqualTo("Name", newText as Any)
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
                            adapter!!.listProduct = productList
                            //adapter = ProductByCategoryAdapter(productList, this@ShowProductsByCategory)
                            adapter!!.notifyDataSetChanged()
                           // recycler_productById.adapter = adapter
                        }

                    })

                return false
            }

        })
        searchView.suggestionsAdapter
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_search -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_tools -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout_showByCategory)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun getHeaderComponentDraw(){
        val headerView = navView.getHeaderView(0)
        val emailDisplay = headerView.findViewById<TextView>(R.id.emailDisplay)
        if (auth.currentUser != null) emailDisplay.text = auth.currentUser!!.email
    }
    private fun getHeaderComponentDraw(currentUser: FirebaseUser) {
        val headerView = navView.getHeaderView(0)
        val emailDisplay = headerView.findViewById<TextView>(R.id.emailDisplay)
        emailDisplay.text = currentUser.email
    }

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        if (currentUser != null){

            getHeaderComponentDraw(currentUser)
        }
    }


    override fun onStop() {
        super.onStop()

    }
}
