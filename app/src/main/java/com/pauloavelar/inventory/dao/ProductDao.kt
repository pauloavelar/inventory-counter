package com.pauloavelar.inventory.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.pauloavelar.inventory.model.Product

@Dao
interface ProductDao : BaseDao<Product> {

    @Query("SELECT * FROM product ORDER BY name ASC")
    fun findAll(): LiveData<List<Product>>

    @Query("DELETE FROM product")
    fun deleteAll()

    @Query("UPDATE product SET name = :newName WHERE name = :oldName")
    fun update(oldName: String, newName: String)

}
