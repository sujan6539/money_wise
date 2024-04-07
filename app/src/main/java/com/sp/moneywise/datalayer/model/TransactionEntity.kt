package com.sp.moneywise.datalayer.model


import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.Date

@Keep
@Entity(tableName = "table_transaction")
data class TransactionEntity(
    @PrimaryKey
    @SerializedName("transactionId")
    val transactionId: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("type")
    val type: TransactionType,
    @SerializedName("amount")
    val amount: String,
    @SerializedName("categoriesCategoryId")
    val categoriesCategoryId: Category,
    @SerializedName("categoryName")
    val categoryName: String?,
    @SerializedName("dateTimestamp")
    val dateTimestamp: Date,
) : Serializable

enum class TransactionType(val value: String) {
    INCOME("INCOME"), EXPENSE("EXPENSE")
}

enum class Category(val value: String) {
    TRANSPORT("TRANSPORT"),
    SHOPPING("SHOPPING"),
    RESTAURANT("RESTAURANT"),
    SALARY("SALARY"),
    MISC("MISC")
}