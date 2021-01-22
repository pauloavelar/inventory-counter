package com.pauloavelar.inventory.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.pauloavelar.inventory.config.AppScope
import com.pauloavelar.inventory.dao.InventoryDao
import com.pauloavelar.inventory.dao.ProductDao
import com.pauloavelar.inventory.model.InventoryItem
import com.pauloavelar.inventory.model.Product
import org.koin.core.KoinComponent
import org.koin.core.inject

class DetailViewModel : ViewModel(), KoinComponent {

    private val appScope: AppScope by inject()
    private val inventoryDao: InventoryDao by inject()
    private val productDao: ProductDao by inject()

    private var item: InventoryItem? = null
    private var editMode: Boolean = false

    fun bindItem(item: InventoryItem) {
        this.item = item
        editMode = true
    }

    fun getItem(): InventoryItem? = item

    fun deleteItem() = appScope.runOnIoThread {
        item?.let { inventoryDao.delete(item!!) }
    }

    fun getProducts(): LiveData<List<Product>> = productDao.findAll()

    fun getLastLotNumber(productName: String): LiveData<String?> = inventoryDao.getLastLotNumber(productName)

    fun saveItem(item: InventoryItem) = appScope.runOnIoThread {
        if (this@DetailViewModel.item != null) {
            inventoryDao.update(item.copy(id = this@DetailViewModel.item!!.id))
        } else {
            inventoryDao.insert(item)
        }
    }

    fun addProduct(productName: String) = appScope.runOnIoThread {
        productDao.insert(Product(name = productName))
    }

}
