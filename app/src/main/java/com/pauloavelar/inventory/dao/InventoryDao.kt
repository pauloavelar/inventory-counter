package com.pauloavelar.inventory.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import com.pauloavelar.inventory.model.InventoryItem

@Dao
interface InventoryDao : BaseDao<InventoryItem> {

    @Update
    fun update(item: InventoryItem)

    @Query("DELETE FROM item")
    fun deleteAll()

    @Query("SELECT * FROM item ORDER BY dateTime DESC")
    fun findAll(): LiveData<List<InventoryItem>>

    @Query("SELECT * FROM item ORDER BY dateTime ASC")
    fun findAllSync(): List<InventoryItem>

    @Query("SELECT lotNumber FROM item WHERE product = :productName ORDER BY dateTime DESC LIMIT 1")
    fun getLastLotNumber(productName: String): LiveData<String?>

    @Query("UPDATE item SET product = :newProductName WHERE product = :oldProductName")
    fun updateProductName(oldProductName: String, newProductName: String)

}
