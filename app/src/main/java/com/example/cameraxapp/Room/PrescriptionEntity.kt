package com.example.cameraxapp.Room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prescriptions_table", primaryKeys = ["name"])
data class PrescriptionEntity(
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "eye") val eye: String,
    @ColumnInfo(name = "frequency") val frequency: String,
    @ColumnInfo(name = "specialInstructions") val specialInstructions: String,
    @ColumnInfo(name = "expirationDate") val expirationDate: String? = null // This will be initially null and later updated by the user
)