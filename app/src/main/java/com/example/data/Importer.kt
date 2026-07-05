package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "importers")
data class Importer(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val phone: String,
    val address: String,
    val defaultCommissionPct: Double,
    val isDeleted: Boolean = false
)
