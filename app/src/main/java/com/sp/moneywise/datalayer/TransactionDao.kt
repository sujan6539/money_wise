package com.sp.moneywise.datalayer

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.sp.moneywise.datalayer.model.TransactionEntity
import com.sp.moneywise.datalayer.model.TransactionType

@Dao
interface TransactionDao {
    @Insert
    suspend fun insert(note: TransactionEntity)

    @Update
    suspend fun update(note: TransactionEntity)

    @Query("SELECT * FROM table_transaction ORDER BY dateTimestamp DESC")
    fun getAllNotes(): LiveData<List<TransactionEntity>>

    @Query("SELECT SUM(amount) AS total_amount FROM table_transaction WHERE strftime('%Y-%m', dateTimestamp / 1000, 'unixepoch') = strftime('%Y-%m', 'now', 'localtime') AND type=:category")
    fun getCurrMonthSummary(category: TransactionType): LiveData<String>

    @Query("SELECT type, SUM(amount) AS totalAmount FROM table_transaction GROUP BY type")
    fun getAccountBalance(): LiveData<List<TransactionSummary>>
}


data class TransactionSummary(
    val type: TransactionType,
    val totalAmount: Double
)