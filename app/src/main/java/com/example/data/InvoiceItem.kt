package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "invoice_items")
data class InvoiceItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val invoiceId: Int,
    val importerId: Int,
    val fishName: String,
    val quantity: Double,
    val sellingPrice: Double,
    val commissionPct: Double,
    val grossSale: Double,
    val commission: Double,
    val netPayable: Double,
    val isDeleted: Boolean = false
)
