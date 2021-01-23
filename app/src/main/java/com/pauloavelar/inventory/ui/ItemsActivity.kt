package com.pauloavelar.inventory.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.pauloavelar.inventory.R
import com.pauloavelar.inventory.config.FORMATTER_DEFAULT
import com.pauloavelar.inventory.databinding.ActivityItemsBinding
import com.pauloavelar.inventory.model.InventoryItem
import com.pauloavelar.inventory.model.SearchResults
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
import java.util.*

class ItemsActivity : AppCompatActivity(), KoinComponent {

    private val viewModel: ItemsViewModel by inject()
    private val dialogBuilder: DialogBuilder by inject()
    private val formatter: SimpleDateFormat by inject(named(FORMATTER_DEFAULT))

    private lateinit var binding: ActivityItemsBinding
    private lateinit var itemsAdapter: ItemAdapter
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityItemsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.toolbar))

        binding.fab.setOnClickListener {
            startActivity(Intent(this@ItemsActivity, DetailActivity::class.java))
            viewModel.clearSearch()
        }
        binding.searchInfo.buttonClearSearch.setOnClickListener {
            searchView.onActionViewCollapsed()
            viewModel.clearSearch()
        }

        itemsAdapter = ItemAdapter(object : OnItemInteraction {
            override fun onItemClick(item: InventoryItem) {
                startActivity(Intent(this@ItemsActivity, DetailActivity::class.java).apply {
                    putExtra(DetailActivity.EXTRA_ITEM, item)
                })
                viewModel.clearSearch()
            }

            override fun onItemLongPress(item: InventoryItem) {
                Snackbar.make(binding.root, formatter.format(item.dateTime), Snackbar.LENGTH_SHORT).show()
            }
        })

        binding.listItems.apply {
            adapter = itemsAdapter
            layoutManager = LinearLayoutManager(this@ItemsActivity)
            addItemDecoration(ListDividerDecoration(this@ItemsActivity))
        }

        viewModel.getItems().observe(this) {
            itemsAdapter.setItems(it.results)
            updateUi(it)
        }

        viewModel.clearSearch()
    }

    private fun updateUi(search: SearchResults): Unit = binding.run {
        if (search.query?.isNotEmpty() == true) {
            searchInfo.root.visibility = View.VISIBLE
            searchInfo.searchLabelMatches.text = search.matches.toString()
            searchInfo.searchLabelTotal.text = search.totalItems.toString()

            val middleId = if (search.matches == 1)
                R.string.search_label_middle_singular
            else
                R.string.search_label_middle_plural

            searchInfo.searchLabelMiddle.setText(middleId)
        } else {
            searchInfo.root.visibility = View.GONE
        }

        if (search.results.isEmpty()) {
            listItems.visibility = View.INVISIBLE
            emptyView.visibility = View.VISIBLE
        } else {
            listItems.visibility = View.VISIBLE
            emptyView.visibility = View.INVISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean = returnTrue {
        menuInflater.inflate(R.menu.menu_items, menu)
        searchView = (menu?.findItem(R.id.action_search)?.actionView as SearchView).apply {
            findViewById<EditText>(androidx.appcompat.R.id.search_src_text).setHint(R.string.search_items)

            setOnCloseListener {
                returnTrue {
                    viewModel.clearSearch()
                    onActionViewCollapsed()
                }
            }

            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?) = true

                override fun onQueryTextChange(query: String?): Boolean = returnTrue {
                    viewModel.searchItems(query)
                }
            })
        }
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

    override fun onBackPressed() {
        if (!searchView.isIconified) {
            searchView.onActionViewCollapsed()
        } else {
            super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != REQUEST_CREATE_CSV_FILE || resultCode != RESULT_OK || data?.data == null) {
            return super.onActivityResult(requestCode, resultCode, data)
        }

        try {
            val stream = contentResolver.openOutputStream(data.data!!)
            viewModel.buildCsvFile(stream!!).observeOnce(this) {
                if (it != true) {
                    Snackbar.make(binding.root, R.string.unable_to_save, Snackbar.LENGTH_SHORT).show()
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
            Snackbar.make(binding.root, R.string.unable_to_save, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun sendCsvViaEmail() {
        val intent = viewModel.buildCreateDocumentIntent()
        startActivityForResult(intent, REQUEST_CREATE_CSV_FILE)
    }

    companion object {
        private const val REQUEST_CREATE_CSV_FILE = 18382
    }

}
