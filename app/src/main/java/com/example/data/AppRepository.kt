package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class AppRepository(private val dao: AppDao) {
    val customers = dao.getAllCustomers()
    val importers = dao.getAllImporters()
    val invoices = dao.getAllInvoices()
    val invoiceItems = dao.getAllInvoiceItems()
    val customerPayments = dao.getAllCustomerPayments()
    val importerPayments = dao.getAllImporterPayments()

    // Real-time calculated balances
    val customerBalances: Flow<Map<Int, Double>> = combine(
        customers, invoices, customerPayments
    ) { custs, invs, pmts ->
        val balanceMap = mutableMapOf<Int, Double>()
        for (c in custs) {
            val totalInvoiced = invs.filter { it.customerId == c.id }.sumOf { it.grossSales }
            val totalReceivedInInvoice = invs.filter { it.customerId == c.id }.sumOf { it.amountReceived }
            val totalPayments = pmts.filter { it.customerId == c.id }.sumOf { it.amount }
            balanceMap[c.id] = c.openingBalance + totalInvoiced - totalReceivedInInvoice - totalPayments
        }
        balanceMap
    }

    val importerBalances: Flow<Map<Int, Double>> = combine(
        importers, invoiceItems, importerPayments
    ) { imps, items, pmts ->
        val balanceMap = mutableMapOf<Int, Double>()
        for (i in imps) {
            val totalPayable = items.filter { it.importerId == i.id }.sumOf { it.netPayable }
            val totalPayments = pmts.filter { it.importerId == i.id }.sumOf { it.amountPaid }
            // Assuming no opening balance for importers based on requirements, 
            // but we can just use 0 as base.
            balanceMap[i.id] = totalPayable - totalPayments
        }
        balanceMap
    }

    suspend fun saveCustomer(customer: Customer) {
        if (customer.id == 0) dao.insertCustomer(customer) else dao.updateCustomer(customer)
    }

    suspend fun saveImporter(importer: Importer) {
        if (importer.id == 0) dao.insertImporter(importer) else dao.updateImporter(importer)
    }

    suspend fun saveInvoice(invoice: Invoice, items: List<InvoiceItem>) {
        dao.saveFullInvoice(invoice, items)
    }

    suspend fun saveCustomerPayment(payment: CustomerPayment) {
        dao.insertCustomerPayment(payment)
    }

    suspend fun saveImporterPayment(payment: ImporterPayment) {
        dao.insertImporterPayment(payment)
    }
}
