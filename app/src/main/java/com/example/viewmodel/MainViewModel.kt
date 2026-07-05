package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val repository: AppRepository) : ViewModel() {
    val customers: StateFlow<List<Customer>> = repository.customers.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val importers: StateFlow<List<Importer>> = repository.importers.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val invoices: StateFlow<List<Invoice>> = repository.invoices.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val invoiceItems: StateFlow<List<InvoiceItem>> = repository.invoiceItems.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val customerPayments: StateFlow<List<CustomerPayment>> = repository.customerPayments.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val importerPayments: StateFlow<List<ImporterPayment>> = repository.importerPayments.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val customerBalances: StateFlow<Map<Int, Double>> = repository.customerBalances.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyMap()
    )

    val importerBalances: StateFlow<Map<Int, Double>> = repository.importerBalances.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyMap()
    )

    fun saveCustomer(customer: Customer) = viewModelScope.launch { repository.saveCustomer(customer) }
    fun saveImporter(importer: Importer) = viewModelScope.launch { repository.saveImporter(importer) }
    fun saveInvoice(invoice: Invoice, items: List<InvoiceItem>) = viewModelScope.launch { repository.saveInvoice(invoice, items) }
    fun saveCustomerPayment(payment: CustomerPayment) = viewModelScope.launch { repository.saveCustomerPayment(payment) }
    fun saveImporterPayment(payment: ImporterPayment) = viewModelScope.launch { repository.saveImporterPayment(payment) }
}

class MainViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
