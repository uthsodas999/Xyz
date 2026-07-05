package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // Customer
    @Query("SELECT * FROM customers WHERE isDeleted = 0")
    fun getAllCustomers(): Flow<List<Customer>>

    @Insert
    suspend fun insertCustomer(customer: Customer)

    @Update
    suspend fun updateCustomer(customer: Customer)
    
    @Query("SELECT * FROM customers WHERE id = :id AND isDeleted = 0")
    suspend fun getCustomerById(id: Int): Customer?

    // Importer
    @Query("SELECT * FROM importers WHERE isDeleted = 0")
    fun getAllImporters(): Flow<List<Importer>>

    @Insert
    suspend fun insertImporter(importer: Importer)

    @Update
    suspend fun updateImporter(importer: Importer)
    
    @Query("SELECT * FROM importers WHERE id = :id AND isDeleted = 0")
    suspend fun getImporterById(id: Int): Importer?

    // Invoices
    @Query("SELECT * FROM invoices WHERE isDeleted = 0 ORDER BY dateMs DESC")
    fun getAllInvoices(): Flow<List<Invoice>>

    @Insert
    suspend fun insertInvoice(invoice: Invoice): Long

    @Insert
    suspend fun insertInvoiceItems(items: List<InvoiceItem>)

    @Query("SELECT * FROM invoice_items WHERE invoiceId = :invoiceId AND isDeleted = 0")
    suspend fun getInvoiceItems(invoiceId: Int): List<InvoiceItem>
    
    @Query("SELECT * FROM invoice_items WHERE isDeleted = 0")
    fun getAllInvoiceItems(): Flow<List<InvoiceItem>>

    @Transaction
    suspend fun saveFullInvoice(invoice: Invoice, items: List<InvoiceItem>) {
        val invoiceId = insertInvoice(invoice).toInt()
        val itemsWithInvoiceId = items.map { it.copy(invoiceId = invoiceId) }
        insertInvoiceItems(itemsWithInvoiceId)
    }

    // Payments
    @Query("SELECT * FROM customer_payments WHERE isDeleted = 0 ORDER BY dateMs DESC")
    fun getAllCustomerPayments(): Flow<List<CustomerPayment>>

    @Insert
    suspend fun insertCustomerPayment(payment: CustomerPayment)

    @Query("SELECT * FROM importer_payments WHERE isDeleted = 0 ORDER BY dateMs DESC")
    fun getAllImporterPayments(): Flow<List<ImporterPayment>>

    @Insert
    suspend fun insertImporterPayment(payment: ImporterPayment)
    
    // Aggregations could be done in memory to avoid complex queries since this is a simple local app,
    // or we can use specific queries.
}
