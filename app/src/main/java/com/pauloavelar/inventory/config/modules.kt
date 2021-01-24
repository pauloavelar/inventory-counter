package com.pauloavelar.inventory.config

import android.app.Application
import androidx.room.Room
import com.pauloavelar.inventory.dao.AppDatabase
import com.pauloavelar.inventory.ui.DialogBuilder
import com.pauloavelar.inventory.ui.viewmodel.DetailViewModel
import com.pauloavelar.inventory.ui.viewmodel.ItemsViewModel
import com.pauloavelar.inventory.ui.viewmodel.ProductsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.text.SimpleDateFormat
import java.util.Locale

const val FORMATTER_DEFAULT = "yyyy-MM-dd HH:mm:ss"
const val FORMATTER_FILENAME = "yyyy-MM-dd_HH.mm.ss"

private val appModules = module(override = true) {
    single { DialogBuilder() }
    single(qualifier = named(FORMATTER_DEFAULT)) { SimpleDateFormat(FORMATTER_DEFAULT, Locale.US) }
    single(qualifier = named(FORMATTER_FILENAME)) { SimpleDateFormat(FORMATTER_FILENAME, Locale.US) }
}

private val viewModelsModule = module(override = true) {
    viewModel { ItemsViewModel() }
    viewModel { DetailViewModel() }
    viewModel { ProductsViewModel() }
}

private val concurrencyModule = module(override = true) {
    single { AppScope() }
    single<CoroutineScope> { GlobalScope }
    single<CoroutineDispatchers> { RuntimeDispatchers() }
}

private val persistenceModule = module(override = true) {
    fun provideDatabase(app: Application) = Room
        .databaseBuilder(app, AppDatabase::class.java, AppDatabase.DB_NAME)
        .fallbackToDestructiveMigration()
        .build()

    fun provideProductDao(db: AppDatabase) = db.getProductDao()
    fun provideInventoryDao(db: AppDatabase) = db.getInventoryDao()

    single { provideDatabase(get()) }
    single { provideProductDao(get()) }
    single { provideInventoryDao(get()) }
}

val allModules = listOf(
    appModules,
    viewModelsModule,
    concurrencyModule,
    persistenceModule
)
