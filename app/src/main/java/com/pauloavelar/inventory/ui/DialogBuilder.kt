package com.pauloavelar.inventory.ui

import android.content.Context
import android.content.DialogInterface.OnClickListener
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
        val dialog = getBuilder(context)
            .setTitle(if (product == null) R.string.add_new_product else R.string.rename_product)
            .setView(R.layout.product_dialog)
            .setPositiveButton(if (product == null) R.string.add_product else R.string.rename, null)
            .setNegativeButton(R.string.cancel, null)
            .create()

        dialog.setOnShowListener { d ->
            (d as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                handleOnProductConfirmed(d, product?.name)
            }
            product?.name.let {
                d.findViewById<EditText>(R.id.edit_new_product)?.setText(it)
            }
        }

        dialog.show()
    }

    fun showClearAllItemsDialog(context: Context) {
        getBuilder(context)
            .setMessage(R.string.confirm_clear_all)
            .setPositiveButton(R.string.yes_delete, buildIoListener { inventoryDao.deleteAll() })
            .setNegativeButton(R.string.no, null)
            .show()
    }

    fun showClearAllProductsDialog(context: Context) {
        getBuilder(context)
            .setTitle(R.string.are_you_sure)
            .setMessage(R.string.delete_products_message)
            .setPositiveButton(R.string.yes_delete, buildIoListener { productDao.deleteAll() })
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun handleOnProductConfirmed(dialog: AlertDialog, oldProductName: String? = null) {
        val editProduct = dialog.findViewById<EditText>(R.id.edit_new_product)

        if (editProduct?.text?.isEmpty() == true) {
            editProduct.error = dialog.context.getString(R.string.empty_product)
        } else {
            dialog.dismiss()

            appScope.runOnIoThread {
                val product = Product(name = editProduct!!.text.toString())
                if (oldProductName == null) {
                    productDao.insert(product)
                } else {
                    productDao.update(oldProductName, product.name)
                    inventoryDao.updateProductName(oldProductName, product.name)
                }
            }
        }
    }

    private fun getBuilder(c: Context) = MaterialAlertDialogBuilder(c, R.style.AppTheme_AlertDialog)

    private fun buildIoListener(block: () -> Unit) = OnClickListener {
        _, _ -> appScope.runOnIoThread { block() }
    }

}
