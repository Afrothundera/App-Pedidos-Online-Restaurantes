package com.opdeveloper.pedidos

import android.app.Application
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.dbflow5.config.*
import com.dbflow5.database.AndroidSQLiteOpenHelper
import com.dbflow5.database.DatabaseCallback
import com.dbflow5.database.OpenHelper
import com.opdeveloper.pedidos.db.models.pedidosdb.AppDb


class CustomApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        FlowManager.init(this)
        /*FlowManager.init(FlowConfig.builder(this)
            .database(
                DatabaseConfig.builder(AppDb::class, AndroidSQLiteOpenHelper.createHelperCreator(this))
                    .databaseName("AppDatabase")
                    .build()
            ).build())*/
    }

    override fun onTerminate() {
        super.onTerminate()
        FlowManager.destroy()
    }
}