package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "importer_payments")
data class ImporterPayment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val importerId: Int,
    val amountPaid: Double,
    val paymentMethod: String,
    val dateMs: Long,
    val remarks: String,
    val isDeleted: Boolean = false
)
