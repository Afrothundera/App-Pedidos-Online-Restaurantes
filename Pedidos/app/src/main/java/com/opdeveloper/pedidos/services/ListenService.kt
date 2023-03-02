package com.opdeveloper.pedidos.services

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.opdeveloper.pedidos.db.models.Request
import com.opdeveloper.pedidos.orderplaced.OrderPlaced

class ListenService : Service(), EventListener<QuerySnapshot> {


    lateinit var database: FirebaseFirestore
    var reference: Query? = null
    lateinit var auth:FirebaseAuth

    override fun onBind(intent: Intent): IBinder? {
        return null
    }



    override fun onCreate() {
        super.onCreate()
        database = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()


    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (auth.currentUser != null){
            reference = database.collection("Request").whereEqualTo("UserId", auth.currentUser!!.uid)
            reference!!.addSnapshotListener(this)
        }
        else reference =null


        return super.onStartCommand(intent, flags, startId)
    }

    override fun onEvent(p0: QuerySnapshot?, p1: FirebaseFirestoreException?) {
        val request = p0!!.toObjects(Request::class.java)
        if (!request.isEmpty())
        showNotification(auth.currentUser!!.uid, request.first())
    }

    private fun showNotification(uid: String, request: Request) {
        val intent = Intent(baseContext, OrderPlaced::class.java)
        intent.putExtra("UserId", uid)
        val pendingIntent = PendingIntent.getActivities(baseContext, 0 , arrayOf(intent), PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(baseContext).apply {
            setDefaults(Notification.DEFAULT_ALL)
            setWhen(System.currentTimeMillis())
            setTicker("Pedidos Brazil")
            setContentInfo("Su pedido ha sido actualizado")
            setContentText("El pedido #"+ request.requestId + " fue actualizado a: "+ converStatus(request.status) )
            setContentIntent(pendingIntent)
            setContentInfo("Info")
            setSmallIcon(R.drawable.notification_bg)
        }

        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(1, builder.build())
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
