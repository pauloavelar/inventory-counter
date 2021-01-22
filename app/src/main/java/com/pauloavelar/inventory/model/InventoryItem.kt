package com.pauloavelar.inventory.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
@Entity(tableName = "item")
data class InventoryItem(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val dateTime: Date,
    val product: String,
    val lotNumber: String,
    val count: Int
) : Parcelable
