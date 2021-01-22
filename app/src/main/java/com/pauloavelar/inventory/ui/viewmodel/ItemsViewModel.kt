package com.pauloavelar.inventory.ui.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pauloavelar.inventory.config.AppScope
import com.pauloavelar.inventory.config.FORMATTER_DEFAULT
import com.pauloavelar.inventory.dao.InventoryDao
import com.pauloavelar.inventory.model.InventoryItem
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.qualifier.named
import java.io.OutputStream
import java.text.SimpleDateFormat

class ItemsViewModel : ViewModel(), KoinComponent {

    private val appScope: AppScope by inject()
    private val context: Context by inject()
    private val dao: InventoryDao by inject()
    private val formatter: SimpleDateFormat by inject(named(FORMATTER_DEFAULT))

    fun getItems(): LiveData<List<InventoryItem>> = dao.findAll()

    fun showItemDetails(item: InventoryItem) {
        Toast.makeText(context, formatter.format(item.dateTime), Toast.LENGTH_SHORT).show()
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

    companion object {
        private const val CSV_SEPARATOR = ","
        private val CSV_FIELDS = arrayOf("Datetime", "Product", "Lot Number", "Count")
        private val CSV_HEADERS = CSV_FIELDS.joinToString(CSV_SEPARATOR)
    }

}
