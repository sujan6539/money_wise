package com.sp.moneywise.datalayer

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sp.moneywise.datalayer.model.Converters
import com.sp.moneywise.datalayer.model.TransactionEntity


@Database(entities = [TransactionEntity::class], version = 1)
@TypeConverters(
    Converters::class,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao?


    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInMemoryDatabase(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                INSTANCE = databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database.db"
                ).build()
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}