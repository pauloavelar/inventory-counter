package com.pauloavelar.inventory.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.pauloavelar.inventory.model.InventoryItem
import com.pauloavelar.inventory.model.Product
import java.util.*

@TypeConverters(AppDatabase.Converters::class)
@Database(entities = [InventoryItem::class, Product::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getInventoryDao(): InventoryDao
    abstract fun getProductDao(): ProductDao

    companion object {
        const val DB_NAME = "data.db"
    }

    class Converters {
        @TypeConverter fun fromDate(date: Date?) = date?.time
        @TypeConverter fun toDate(value: Long?) = value?.let { Date(it) }
    }
}
