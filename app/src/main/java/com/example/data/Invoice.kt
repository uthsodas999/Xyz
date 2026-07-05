package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "invoices")
data class Invoice(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val invoiceNumber: String,
    val dateMs: Long,
    val customerId: Int,
    val amountReceived: Double,
    val paymentMethod: String,
    val remarks: String,
    val totalQuantity: Double,
    val grossSales: Double,
    val totalCommission: Double,
    val totalNetPayable: Double,
    val isDeleted: Boolean = false
)
