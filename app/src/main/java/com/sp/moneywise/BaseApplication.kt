package com.sp.moneywise

import android.app.Application
import com.google.firebase.FirebaseApp
import com.sp.moneywise.datalayer.AppDatabase
import java.io.FileOutputStream
import java.io.IOException

class BaseApplication : Application() {

    var myAppDatabase: AppDatabase? = null

    override fun onCreate() {
        super.onCreate()
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        // Initialize Room database
        myAppDatabase = AppDatabase.getInMemoryDatabase(applicationContext)
        copyDatabaseIfNeeded()
    }

    private fun copyDatabaseIfNeeded() {
        val databaseName = "app_database.db"
        val destinationPath = getDatabasePath(databaseName)

        if (!destinationPath.exists()) {
            try {
                val inputStream = assets.open(databaseName)
                val outputStream = FileOutputStream(destinationPath)

                inputStream.copyTo(outputStream)

                inputStream.close()
                outputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

}