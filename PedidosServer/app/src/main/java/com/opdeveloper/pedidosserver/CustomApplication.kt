package com.opdeveloper.pedidosserver

import android.app.Application
import com.dbflow5.config.FlowManager

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