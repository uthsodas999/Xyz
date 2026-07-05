package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Customer
import com.example.viewmodel.MainViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomersScreen(viewModel: MainViewModel) {
    val customers by viewModel.customers.collectAsStateWithLifecycle()
    val balances by viewModel.customerBalances.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, "Add Customer")
            }
        }
    ) { padding ->
        LazyColumn(Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            item { Text("Customers", style = MaterialTheme.typography.headlineMedium) }
            item { Spacer(Modifier.height(16.dp)) }
            
            items(customers) { customer ->
                val balance = balances[customer.id] ?: customer.openingBalance
                Card(Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Text(customer.name, style = MaterialTheme.typography.titleMedium)
                        Text(customer.phone, style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(8.dp))
                        val color = if (balance > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        Text("Balance: $${String.format(Locale.US, "%.2f", balance)}", color = color)
                    }
                }
            }
        }
    }

    if (showDialog) {
        var name by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var address by remember { mutableStateOf("") }
        var openingBalance by remember { mutableStateOf("0") }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add Customer") },
            text = {
                Column {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                    OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") })
                    OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address") })
                    OutlinedTextField(value = openingBalance, onValueChange = { openingBalance = it }, label = { Text("Opening Balance") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val bal = openingBalance.toDoubleOrNull() ?: 0.0
                    viewModel.saveCustomer(Customer(name = name, phone = phone, address = address, openingBalance = bal))
                    showDialog = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            }
        )
    }
}
