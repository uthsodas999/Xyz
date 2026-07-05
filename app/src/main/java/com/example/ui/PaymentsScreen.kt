package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.CustomerPayment
import com.example.data.ImporterPayment
import com.example.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentsScreen(viewModel: MainViewModel) {
    val customers by viewModel.customers.collectAsStateWithLifecycle()
    val importers by viewModel.importers.collectAsStateWithLifecycle()

    var isCustomerPayment by remember { mutableStateOf(true) }
    
    var selectedId by remember { mutableStateOf<Int?>(null) }
    var amount by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("Cash") }
    var remarks by remember { mutableStateOf("") }

    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { Text("Record Payment", style = MaterialTheme.typography.headlineMedium) }
        
        item {
            Row(Modifier.fillMaxWidth()) {
                FilterChip(
                    selected = isCustomerPayment,
                    onClick = { isCustomerPayment = true; selectedId = null },
                    label = { Text("Customer Payment") },
                    modifier = Modifier.padding(end = 8.dp)
                )
                FilterChip(
                    selected = !isCustomerPayment,
                    onClick = { isCustomerPayment = false; selectedId = null },
                    label = { Text("Importer Payment") }
                )
            }
        }
        
        item {
            var expanded by remember { mutableStateOf(false) }
            val label = if (isCustomerPayment) "Select Customer" else "Select Importer"
            val selectedName = if (isCustomerPayment) {
                customers.find { it.id == selectedId }?.name ?: label
            } else {
                importers.find { it.id == selectedId }?.name ?: label
            }
            
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedName,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    if (isCustomerPayment) {
                        customers.forEach { customer ->
                            DropdownMenuItem(
                                text = { Text(customer.name) },
                                onClick = { selectedId = customer.id; expanded = false }
                            )
                        }
                    } else {
                        importers.forEach { importer ->
                            DropdownMenuItem(
                                text = { Text(importer.name) },
                                onClick = { selectedId = importer.id; expanded = false }
                            )
                        }
                    }
                }
            }
        }
        
        item {
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        item {
            OutlinedTextField(
                value = remarks,
                onValueChange = { remarks = it },
                label = { Text("Remarks") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        item {
            Button(
                onClick = {
                    val amt = amount.toDoubleOrNull() ?: 0.0
                    if (selectedId != null && amt > 0) {
                        if (isCustomerPayment) {
                            viewModel.saveCustomerPayment(
                                CustomerPayment(
                                    customerId = selectedId!!,
                                    amount = amt,
                                    paymentMethod = paymentMethod,
                                    dateMs = System.currentTimeMillis(),
                                    remarks = remarks
                                )
                            )
                        } else {
                            viewModel.saveImporterPayment(
                                ImporterPayment(
                                    importerId = selectedId!!,
                                    amountPaid = amt,
                                    paymentMethod = paymentMethod,
                                    dateMs = System.currentTimeMillis(),
                                    remarks = remarks
                                )
                            )
                        }
                        selectedId = null
                        amount = ""
                        remarks = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Payment")
            }
        }
    }
}
