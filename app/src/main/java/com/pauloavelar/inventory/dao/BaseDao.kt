package com.pauloavelar.inventory.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update

@Dao
interface BaseDao<T> {
    @Insert
    fun insert(vararg items: T)

    @Delete
    fun delete(vararg items: T)
}
