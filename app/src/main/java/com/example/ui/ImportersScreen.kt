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
import com.example.data.Importer
import com.example.viewmodel.MainViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportersScreen(viewModel: MainViewModel) {
    val importers by viewModel.importers.collectAsStateWithLifecycle()
    val balances by viewModel.importerBalances.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, "Add Importer")
            }
        }
    ) { padding ->
        LazyColumn(Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            item { Text("Importers", style = MaterialTheme.typography.headlineMedium) }
            item { Spacer(Modifier.height(16.dp)) }
            
            items(importers) { importer ->
                val balance = balances[importer.id] ?: 0.0
                Card(Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Text(importer.name, style = MaterialTheme.typography.titleMedium)
                        Text(importer.phone, style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(8.dp))
                        Text("Payable Balance: $${String.format(Locale.US, "%.2f", balance)}")
                    }
                }
            }
        }
    }

    if (showDialog) {
        var name by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var address by remember { mutableStateOf("") }
        var defaultComm by remember { mutableStateOf("0") }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add Importer") },
            text = {
                Column {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                    OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") })
                    OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address") })
                    OutlinedTextField(value = defaultComm, onValueChange = { defaultComm = it }, label = { Text("Default Comm %") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val comm = defaultComm.toDoubleOrNull() ?: 0.0
                    viewModel.saveImporter(Importer(name = name, phone = phone, address = address, defaultCommissionPct = comm))
                    showDialog = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            }
        )
    }
}
