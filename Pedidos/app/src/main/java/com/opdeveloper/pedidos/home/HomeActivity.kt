package com.opdeveloper.pedidos.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.opdeveloper.pedidos.R
import com.opdeveloper.pedidos.db.models.Category
import kotlinx.android.synthetic.main.content_home.*
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.opdeveloper.pedidos.car.CarActivity
import com.opdeveloper.pedidos.home.viewholder.InicioViewHolder
import com.opdeveloper.pedidos.home.viewholder.InicioViewHolderAdapter
import com.opdeveloper.pedidos.interfaces.OnItemClick
import com.opdeveloper.pedidos.orderplaced.OrderPlaced
import com.opdeveloper.pedidos.services.ListenService
import com.opdeveloper.pedidos.showproductbycategory.ShowProductsByCategory
import com.opdeveloper.pedidos.welcome.WelcomeActivity
import com.squareup.picasso.Picasso


class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val db = FirebaseFirestore.getInstance()


    private lateinit var auth: FirebaseAuth
    private lateinit var navView: NavigationView
    private var adapter:InicioViewHolderAdapter? = null
    private var categoryList = mutableListOf<Category>()
    private var firestoreListener: ListenerRegistration? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        auth = FirebaseAuth.getInstance()
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.title = "Inicio"
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            val intent = Intent(this@HomeActivity, CarActivity::class.java)
            startActivity(intent)
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
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

        recycler_menu.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(applicationContext)
        recycler_menu.setHasFixedSize(true)
        recycler_menu.layoutManager = mLayoutManager as RecyclerView.LayoutManager?
        recycler_menu.itemAnimator = DefaultItemAnimator()

        loadMenu()

        val serviceIntent = Intent(this, ListenService::class.java)
        startService(serviceIntent)
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

                adapter = InicioViewHolderAdapter(categoryList, this@HomeActivity)
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
        menuInflater.inflate(R.menu.home, menu)
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
                val intent = Intent(this, CarActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_orders ->{
                val intent = Intent(this, OrderPlaced::class.java)
                startActivity(intent)
            }
            R.id.nav_log_out -> {
                auth.signOut()
                val intent = Intent(this, WelcomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }

        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
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



    /*override fun onResume(){

        //adapter!!.startListening()
        loadMenu()
        super.onResume()
    }*/


}
