package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.viewmodel.MainViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesEntryScreen(viewModel: MainViewModel) {
    val customers by viewModel.customers.collectAsStateWithLifecycle()
    val importers by viewModel.importers.collectAsStateWithLifecycle()

    var selectedCustomerId by remember { mutableStateOf<Int?>(null) }
    var amountReceived by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("Cash") }
    var remarks by remember { mutableStateOf("") }

    // Line items
    data class DraftItem(
        val importerId: Int? = null,
        val fishName: String = "",
        val quantity: String = "",
        val sellingPrice: String = "",
        val commissionPct: String = ""
    )
    
    var draftItems by remember { mutableStateOf(listOf(DraftItem())) }

    // Calculations
    val parsedItems = draftItems.mapNotNull { item ->
        val qty = item.quantity.toDoubleOrNull() ?: 0.0
        val price = item.sellingPrice.toDoubleOrNull() ?: 0.0
        val commPct = item.commissionPct.toDoubleOrNull() ?: 0.0
        if (item.importerId != null && qty > 0 && price > 0) {
            val gross = qty * price
            val comm = gross * (commPct / 100)
            val net = gross - comm
            InvoiceItem(
                invoiceId = 0,
                importerId = item.importerId,
                fishName = item.fishName,
                quantity = qty,
                sellingPrice = price,
                commissionPct = commPct,
                grossSale = gross,
                commission = comm,
                netPayable = net
            )
        } else null
    }

    val totalQty = parsedItems.sumOf { it.quantity }
    val totalGross = parsedItems.sumOf { it.grossSale }
    val totalComm = parsedItems.sumOf { it.commission }
    val totalNet = parsedItems.sumOf { it.netPayable }
    val received = amountReceived.toDoubleOrNull() ?: 0.0
    val customerDue = totalGross - received

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if (selectedCustomerId != null && parsedItems.isNotEmpty()) {
                    val invoice = Invoice(
                        invoiceNumber = "INV-${System.currentTimeMillis()}",
                        dateMs = System.currentTimeMillis(),
                        customerId = selectedCustomerId!!,
                        amountReceived = received,
                        paymentMethod = paymentMethod,
                        remarks = remarks,
                        totalQuantity = totalQty,
                        grossSales = totalGross,
                        totalCommission = totalComm,
                        totalNetPayable = totalNet
                    )
                    viewModel.saveInvoice(invoice, parsedItems)
                    // Reset form
                    selectedCustomerId = null
                    amountReceived = ""
                    remarks = ""
                    draftItems = listOf(DraftItem())
                }
            }) {
                Icon(Icons.Default.Add, contentDescription = "Save Invoice")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Text("New Sales Entry", style = MaterialTheme.typography.headlineMedium) }
            
            item {
                Card {
                    Column(Modifier.padding(16.dp)) {
                        Text("Customer & Payment", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        // Customer Dropdown (Simplified as OutlinedTextField for now, ideally an ExposedDropdownMenu)
                        var expanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = customers.find { it.id == selectedCustomerId }?.name ?: "Select Customer",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                customers.forEach { customer ->
                                    DropdownMenuItem(
                                        text = { Text(customer.name) },
                                        onClick = {
                                            selectedCustomerId = customer.id
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                        
                        OutlinedTextField(
                            value = amountReceived,
                            onValueChange = { amountReceived = it },
                            label = { Text("Amount Received") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = remarks,
                            onValueChange = { remarks = it },
                            label = { Text("Remarks") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            
            item { Text("Items", style = MaterialTheme.typography.titleMedium) }

            items(draftItems.size) { index ->
                val item = draftItems[index]
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Item ${index + 1}")
                            IconButton(onClick = { draftItems = draftItems.toMutableList().apply { removeAt(index) } }) {
                                Icon(Icons.Default.Delete, "Delete")
                            }
                        }
                        
                        var expanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = importers.find { it.id == item.importerId }?.name ?: "Select Importer",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                importers.forEach { importer ->
                                    DropdownMenuItem(
                                        text = { Text(importer.name) },
                                        onClick = {
                                            val newItems = draftItems.toMutableList()
                                            newItems[index] = item.copy(
                                                importerId = importer.id,
                                                commissionPct = importer.defaultCommissionPct.toString()
                                            )
                                            draftItems = newItems
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                        
                        OutlinedTextField(
                            value = item.fishName,
                            onValueChange = { v -> draftItems = draftItems.toMutableList().apply { this[index] = item.copy(fishName = v) } },
                            label = { Text("Fish Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = item.quantity,
                                onValueChange = { v -> draftItems = draftItems.toMutableList().apply { this[index] = item.copy(quantity = v) } },
                                label = { Text("Qty (Kg)") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = item.sellingPrice,
                                onValueChange = { v -> draftItems = draftItems.toMutableList().apply { this[index] = item.copy(sellingPrice = v) } },
                                label = { Text("Price/Kg") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = item.commissionPct,
                                onValueChange = { v -> draftItems = draftItems.toMutableList().apply { this[index] = item.copy(commissionPct = v) } },
                                label = { Text("Comm %") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
            
            item {
                Button(onClick = { draftItems = draftItems + DraftItem() }, modifier = Modifier.fillMaxWidth()) {
                    Text("Add Item")
                }
            }

            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                    Column(Modifier.padding(16.dp).fillMaxWidth()) {
                        Text("Invoice Summary", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        Text("Gross Sale: $${String.format(Locale.US, "%.2f", totalGross)}")
                        Text("Commission: $${String.format(Locale.US, "%.2f", totalComm)}")
                        Text("Net to Importers: $${String.format(Locale.US, "%.2f", totalNet)}")
                        Text("Customer Due: $${String.format(Locale.US, "%.2f", customerDue)}")
                    }
                }
                Spacer(Modifier.height(80.dp)) // space for fab
            }
        }
    }
}
