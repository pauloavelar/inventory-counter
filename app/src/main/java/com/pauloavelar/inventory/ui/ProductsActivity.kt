package com.pauloavelar.inventory.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pauloavelar.inventory.R
import com.pauloavelar.inventory.databinding.ActivityProductsBinding
import com.pauloavelar.inventory.model.Product
import com.pauloavelar.inventory.ui.component.ListDividerDecoration
import com.pauloavelar.inventory.ui.component.ProductAdapter
import com.pauloavelar.inventory.ui.viewmodel.ProductsViewModel
import com.pauloavelar.inventory.utils.returnTrue
import org.koin.core.KoinComponent
import org.koin.core.inject

class ProductsActivity : AppCompatActivity(), KoinComponent {

    private lateinit var binding: ActivityProductsBinding
    private var productsAdapter: ProductAdapter? = null

    private val viewModel: ProductsViewModel by inject()
    private val dialogBuilder: DialogBuilder by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            setTitle(R.string.manage_products)
            setDisplayShowHomeEnabled(true)
        }

        productsAdapter = ProductAdapter(object : ProductAdapter.OnItemInteraction {
            override fun onClickEdit(product: Product) {
                dialogBuilder.showProductDialog(this@ProductsActivity, product)
            }

            override fun onClickDelete(product: Product) = viewModel.deleteProduct(product)
        })

        findViewById<RecyclerView>(R.id.list_products).apply {
            adapter = productsAdapter
            addItemDecoration(ListDividerDecoration(this@ProductsActivity))
            layoutManager = LinearLayoutManager(this@ProductsActivity)
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.getProducts().observe(this) {
            productsAdapter!!.setItems(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_products, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_add -> returnTrue {
            dialogBuilder.showProductDialog(this@ProductsActivity)
        }
        R.id.action_clear_all -> returnTrue {
            dialogBuilder.showClearAllProductsDialog(this@ProductsActivity)
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

}
