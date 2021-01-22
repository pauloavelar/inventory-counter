package com.pauloavelar.inventory.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.pauloavelar.inventory.R
import com.pauloavelar.inventory.config.FORMATTER_FILENAME
import com.pauloavelar.inventory.databinding.ActivityItemsBinding
import com.pauloavelar.inventory.model.InventoryItem
import com.pauloavelar.inventory.ui.component.ItemAdapter
import com.pauloavelar.inventory.ui.component.ItemAdapter.OnItemInteraction
import com.pauloavelar.inventory.ui.component.ListDividerDecoration
import com.pauloavelar.inventory.ui.viewmodel.ItemsViewModel
import com.pauloavelar.inventory.utils.observeOnce
import com.pauloavelar.inventory.utils.returnTrue
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.qualifier.named
import java.text.SimpleDateFormat
import java.util.Date

class ItemsActivity : AppCompatActivity(), KoinComponent {

    private val viewModel: ItemsViewModel by inject()
    private val dialogBuilder: DialogBuilder by inject()
    private val formatter: SimpleDateFormat by inject(named(FORMATTER_FILENAME))

    private lateinit var binding: ActivityItemsBinding
    private lateinit var itemsAdapter: ItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityItemsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.toolbar))

        binding.fab.setOnClickListener {
            startActivity(Intent(this@ItemsActivity, DetailActivity::class.java))
        }

        itemsAdapter = ItemAdapter(object : OnItemInteraction {
            override fun onItemClick(item: InventoryItem) {
                startActivity(Intent(this@ItemsActivity, DetailActivity::class.java).apply {
                    putExtra(DetailActivity.EXTRA_ITEM, item)
                })
            }

            override fun onItemLongPress(item: InventoryItem) {
                viewModel.showItemDetails(item)
            }
        })

        binding.listItems.apply {
            adapter = itemsAdapter
            layoutManager = LinearLayoutManager(this@ItemsActivity)
            addItemDecoration(ListDividerDecoration(this@ItemsActivity))
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getItems().observe(this) {
            itemsAdapter.setItems(it)
            binding.apply {
                listItems.visibility = if (it.isEmpty()) View.INVISIBLE else View.VISIBLE
                inventoryEmpty.visibility = if (it.isEmpty()) View.VISIBLE else View.INVISIBLE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean = returnTrue {
        menuInflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_save -> returnTrue {
            sendCsvViaEmail()
        }
        R.id.action_clear_all -> returnTrue {
            dialogBuilder.showClearAllItemsDialog(this@ItemsActivity)
        }
        R.id.action_products -> returnTrue {
            startActivity(Intent(this@ItemsActivity, ProductsActivity::class.java))
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != REQUEST_CREATE_CSV_FILE || resultCode != RESULT_OK || data?.data == null) {
            return super.onActivityResult(requestCode, resultCode, data)
        }

        try {
            val stream = contentResolver.openOutputStream(data.data!!)
            viewModel.buildCsvFile(stream!!).observeOnce(this) {
                if (it != true) {
                    Toast.makeText(this, R.string.unable_to_save, Toast.LENGTH_SHORT).show()
                    return@observeOnce
                }

                val emailIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "vnd.android.cursor.dir/email"
                    putExtra(Intent.EXTRA_STREAM, data.data!!)
                    putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject))
                }

                startActivity(Intent.createChooser(emailIntent, getString(R.string.chooser_title)))
            }
        } catch (e: Exception) {
            Toast.makeText(this, R.string.unable_to_save, Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendCsvViaEmail() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/csv"
            putExtra(Intent.EXTRA_TITLE, "inventory_${formatter.format(Date())}.csv")
        }
        startActivityForResult(intent, REQUEST_CREATE_CSV_FILE)
    }

    companion object {
        private const val REQUEST_CREATE_CSV_FILE = 18382
    }

}
