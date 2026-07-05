package com.example.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        Customer::class,
        Importer::class,
        Invoice::class,
        InvoiceItem::class,
        CustomerPayment::class,
        ImporterPayment::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao
}
