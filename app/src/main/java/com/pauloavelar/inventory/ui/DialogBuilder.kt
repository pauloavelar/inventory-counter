package com.pauloavelar.inventory.ui

import android.content.Context
import android.content.DialogInterface.OnClickListener
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.pauloavelar.inventory.R
import com.pauloavelar.inventory.config.AppScope
import com.pauloavelar.inventory.dao.InventoryDao
import com.pauloavelar.inventory.dao.ProductDao
import com.pauloavelar.inventory.model.Product
import org.koin.core.KoinComponent
import org.koin.core.inject

class DialogBuilder : KoinComponent {

    private val appScope: AppScope by inject()
    private val inventoryDao: InventoryDao by inject()
    private val productDao: ProductDao by inject()

    fun showProductDialog(context: Context, product: Product? = null) {
        AlertDialog.Builder(context)
            .setTitle(if (product == null) R.string.add_new_product else R.string.rename_product)
            .setView(R.layout.product_dialog)
            .setPositiveButton(
                if (product == null) R.string.add_product else R.string.rename,
                buildProductListener(context, product?.name)
            )
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    fun showClearAllItemsDialog(context: Context) {
        AlertDialog.Builder(context)
            .setMessage(R.string.confirm_clear_all)
            .setPositiveButton(R.string.yes_delete, buildIoListener { inventoryDao.deleteAll() })
            .setNegativeButton(R.string.no, null)
            .show()
    }

    fun showClearAllProductsDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle(R.string.are_you_sure)
            .setMessage(R.string.delete_products_message)
            .setPositiveButton(R.string.yes_delete, buildIoListener { productDao.deleteAll() })
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun buildProductListener(context: Context, productName: String? = null) = OnClickListener { d, _ ->
        val editProduct = (d as AlertDialog).findViewById<EditText>(R.id.edit_new_product)

        if (editProduct?.text?.isEmpty() == true) {
            Toast.makeText(context, R.string.empty_product, Toast.LENGTH_SHORT).show()
            return@OnClickListener
        }

        appScope.runOnIoThread {
            val product = Product(name = editProduct!!.text.toString())
            if (productName == null) {
                productDao.insert(product)
            } else {
                productDao.update(productName, product.name)
                inventoryDao.updateProductName(productName, product.name)
            }
        }
    }

    private fun buildIoListener(block: () -> Unit) = OnClickListener {
        _, _ -> appScope.runOnIoThread { block() }
    }

}
