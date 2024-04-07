package com.sp.moneywise.datalayer.model

import androidx.room.TypeConverter
import java.util.Date

object Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromTransactionType(value: String?): TransactionType? {
        return if (value == null) {
            null
        } else {
            if (value.equals("INCOME", true)) {
                TransactionType.INCOME
            } else {
                TransactionType.EXPENSE
            }
        }
    }

    @TypeConverter
    fun transactionTypeToValue(date: TransactionType?): String? {
        return date?.value
    }

    @TypeConverter
    fun fromCategoryType(value: String?): Category {
        return if (value == null) {
            Category.MISC
        } else {
            when {
                value.equals("TRANSPORT", ignoreCase = true) -> Category.TRANSPORT
                value.equals("SHOPPING", ignoreCase = true) -> Category.SHOPPING
                value.equals("RESTAURANT", ignoreCase = true) -> Category.RESTAURANT
                value.equals("SALARY", ignoreCase = true) -> Category.SALARY
                else -> Category.MISC
            }
        }
    }

    @TypeConverter
    fun CategoryToValue(category: Category): String {
        return category.value
    }
}