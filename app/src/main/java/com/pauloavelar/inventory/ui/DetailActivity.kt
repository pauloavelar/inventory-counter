package com.pauloavelar.inventory.ui

import android.os.Bundle
import android.text.InputFilter.AllCaps
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.pauloavelar.inventory.R
import com.pauloavelar.inventory.databinding.ActivityDetailBinding
import com.pauloavelar.inventory.model.InventoryItem
import com.pauloavelar.inventory.ui.viewmodel.DetailViewModel
import com.pauloavelar.inventory.utils.observeOnce
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.Date

class DetailActivity : AppCompatActivity(), KoinComponent {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var products: List<String>

    private val viewModel: DetailViewModel by inject()
    private val dialogBuilder: DialogBuilder by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        products = buildProductList()

        val item = intent.getParcelableExtra<InventoryItem>(EXTRA_ITEM)
        if (item != null) {
            supportActionBar?.setTitle(R.string.edit_product)

            binding.spinnerProduct.setSelection(products.indexOf(item.product), true)
            binding.editLotNumber.setText(item.lotNumber)
            binding.editCount.setText(item.count.toString())

            viewModel.bindItem(item)
        } else {
            supportActionBar?.setTitle(R.string.add_product)

            binding.buttonDelete.setText(R.string.cancel)
        }

        binding.apply {
            editLotNumber.filters = arrayOf(AllCaps())
            buttonSave.setOnClickListener {
                handleSave()
            }
            buttonDelete.setOnClickListener {
                viewModel.deleteItem()
                finish()
            }
            buttonLastLot.setOnClickListener {
                if (spinnerProduct.selectedItemPosition > 0) {
                    val productName = spinnerProduct.selectedItem.toString()
                    viewModel.getLastLotNumber(productName).observeOnce(this@DetailActivity) {
                        editLotNumber.setText(it ?: "")
                    }
                }
            }
        }

        val adapter = ArrayAdapter(this, R.layout.product_item, products)
        adapter.setDropDownViewResource(R.layout.product_item_dropdown)
        binding.spinnerProduct.adapter = adapter
        binding.spinnerProduct.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, v: View, pos: Int, id: Long) {
                if (pos == products.size - 1) {
                    binding.spinnerProduct.setSelection(0)
                    dialogBuilder.showProductDialog(this@DetailActivity)
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        viewModel.getProducts().observe(this) {
            val products = buildProductList(it.map { product -> product.name })

            val productName = if (binding.spinnerProduct.selectedItemId <= 0) {
                viewModel.getItem()?.product
            } else {
                binding.spinnerProduct.selectedItem.toString()
            }

            adapter.clear()
            adapter.addAll(products)

            if (products.contains(productName)) {
                binding.spinnerProduct.setSelection(products.indexOf(productName))
            } else if (productName != null) {
                viewModel.addProduct(productName)
            }
        }
    }

    private fun handleSave(): Unit = when {
        binding.spinnerProduct.selectedItemPosition == 0 -> {
            showValidationError(R.string.error_no_product)
        }
        binding.editLotNumber.text?.isEmpty() == true -> {
            showValidationError(R.string.error_no_lot_number)
        }
        binding.editCount.text?.isEmpty() == true -> {
            showValidationError(R.string.error_no_quantity)
        }
        else -> {
            viewModel.saveItem(InventoryItem(
                dateTime = Date(),
                product = binding.spinnerProduct.selectedItem.toString(),
                lotNumber = binding.editLotNumber.text.toString(),
                count = binding.editCount.text.toString().toInt(),
            ))
            finish()
        }
    }

    private fun showValidationError(@StringRes stringId: Int) = Snackbar
        .make(binding.root, stringId, Snackbar.LENGTH_SHORT)
        .setBackgroundTint(getColor(R.color.actionDanger))
        .show()

    private fun buildProductList(products: List<String> = emptyList()): List<String> =
        listOf(getString(R.string.select_product)) + products + listOf(getString(R.string.add_new_product))

    companion object {
        const val EXTRA_ITEM = "item"
    }

}
