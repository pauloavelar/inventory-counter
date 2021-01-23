package com.pauloavelar.inventory.ui.viewmodel

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pauloavelar.inventory.config.AppScope
import com.pauloavelar.inventory.config.FORMATTER_DEFAULT
import com.pauloavelar.inventory.config.FORMATTER_FILENAME
import com.pauloavelar.inventory.dao.InventoryDao
import com.pauloavelar.inventory.model.InventoryItem
import com.pauloavelar.inventory.model.SearchResults
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.qualifier.named
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.max

class ItemsViewModel : ViewModel(), KoinComponent {

    private val appScope: AppScope by inject()
    private val dao: InventoryDao by inject()
    private val formatter: SimpleDateFormat by inject(named(FORMATTER_DEFAULT))
    private val fileFormatter: SimpleDateFormat by inject(named(FORMATTER_FILENAME))

    private var currentSearch: LiveData<List<InventoryItem>>? = null
    private val items: MediatorLiveData<SearchResults> = MediatorLiveData()

    init {
        items.value = SearchResults.EMPTY
        items.addSource(dao.countAll()) {
            items.postValue(items.value?.copy(totalItems = it))
        }
    }

    fun getItems(): LiveData<SearchResults> = items

    fun clearSearch() = searchItems()

    fun searchItems(query: String? = null) {
        currentSearch?.run {
            items.removeSource(this)
        }

        val search = if (query?.isNotEmpty() == true) {
            dao.queryItems(query)
        } else {
            dao.findAll()
        }

        currentSearch = search
        items.addSource(search) {
            items.postValue(items.value!!.copy(
                query = query,
                results = it,
                matches = it.size,
                totalItems = max(it.size, items.value?.totalItems ?: 0),
            ))
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    fun buildCsvFile(stream: OutputStream): MutableLiveData<Boolean> {
        val result = MutableLiveData<Boolean>()

        appScope.runOnIoThread {
            try {
                stream.use { it.write(buildCsvContents().toByteArray()) }
                result.postValue(true)
            } catch (e: Exception) {
                result.postValue(false)
            }
        }

        return result
    }

    private fun buildCsvContents(): String {
        val lineSeparator = System.getProperty("line.separator") ?: "\n"

        return CSV_HEADERS + lineSeparator + dao.findAllSync().joinToString(lineSeparator) {
            "${formatter.format(it.dateTime)},${it.product},${it.lotNumber},${it.count}"
        }
    }

    fun buildCreateDocumentIntent() = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
        type = "text/csv"
        addCategory(Intent.CATEGORY_OPENABLE)
        putExtra(Intent.EXTRA_TITLE, "inventory_${fileFormatter.format(Date())}.csv")
    }

    companion object {
        private const val CSV_SEPARATOR = ","
        private val CSV_FIELDS = arrayOf("Datetime", "Product", "Lot Number", "Count")
        private val CSV_HEADERS = CSV_FIELDS.joinToString(CSV_SEPARATOR)
    }

}
