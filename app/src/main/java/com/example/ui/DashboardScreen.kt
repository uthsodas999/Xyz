package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.viewmodel.MainViewModel
import java.util.*
import java.text.SimpleDateFormat

@Composable
fun DashboardScreen(viewModel: MainViewModel) {
    val invoices by viewModel.invoices.collectAsStateWithLifecycle()
    val invoiceItems by viewModel.invoiceItems.collectAsStateWithLifecycle()
    val customerPayments by viewModel.customerPayments.collectAsStateWithLifecycle()
    val importerPayments by viewModel.importerPayments.collectAsStateWithLifecycle()
    val customerBalances by viewModel.customerBalances.collectAsStateWithLifecycle()
    val importerBalances by viewModel.importerBalances.collectAsStateWithLifecycle()

    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val todaysInvoices = invoices.filter { it.dateMs >= today }
    val todaysItems = invoiceItems.filter { item -> todaysInvoices.any { it.id == item.invoiceId } }
    val todaysCustomerPayments = customerPayments.filter { it.dateMs >= today }
    val todaysImporterPayments = importerPayments.filter { it.dateMs >= today }

    val todaySales = todaysInvoices.sumOf { it.grossSales }
    val todayCommission = todaysItems.sumOf { it.commission }
    val todayCollections = todaysCustomerPayments.sumOf { it.amount } + todaysInvoices.sumOf { it.amountReceived }
    val todayPayments = todaysImporterPayments.sumOf { it.amountPaid }

    val outstandingCustomer = customerBalances.values.sum()
    val outstandingImporter = importerBalances.values.sum()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Text("Dashboard", style = MaterialTheme.typography.headlineMedium) }
        
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DashboardCard("Today's Sales", todaySales, Modifier.weight(1f))
                DashboardCard("Today's Comm.", todayCommission, Modifier.weight(1f))
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DashboardCard("Collections", todayCollections, Modifier.weight(1f))
                DashboardCard("Payments", todayPayments, Modifier.weight(1f))
            }
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                Column(Modifier.padding(16.dp).fillMaxWidth()) {
                    Text("Outstanding Balances", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Text("Customer Due: $${String.format(Locale.US, "%.2f", outstandingCustomer)}")
                    Text("Importer Payable: $${String.format(Locale.US, "%.2f", outstandingImporter)}")
                }
            }
        }
    }
}

@Composable
fun DashboardCard(title: String, amount: Double, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(4.dp))
            Text("$${String.format(Locale.US, "%.2f", amount)}", style = MaterialTheme.typography.titleLarge)
        }
    }
}
