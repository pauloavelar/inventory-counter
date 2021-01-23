package com.pauloavelar.inventory.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.pauloavelar.inventory.config.AppScope
import com.pauloavelar.inventory.dao.ProductDao
import com.pauloavelar.inventory.model.Product
import org.koin.core.KoinComponent
import org.koin.core.inject

class ProductsViewModel : ViewModel(), KoinComponent {

    private val appScope: AppScope by inject()
    private val productDao: ProductDao by inject()

    fun getProducts(): LiveData<List<Product>> = productDao.findAll()
    fun deleteProduct(product: Product) = appScope.runOnIoThread {
        productDao.delete(product)
    }

}
