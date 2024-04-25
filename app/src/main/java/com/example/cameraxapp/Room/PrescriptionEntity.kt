package com.example.cameraxapp.Room

import androidx.room.ColumnInfo
import androidx.room.Entity

/*
The follow class represents tables in our app's database.
Each instance of an Entity data class represents a row in a table for prescriptions in the app's database.
Essentially, we're creating a "template" for each row in our future database table, by specifying the columns.
 */

@Entity(tableName = "prescriptions_table", primaryKeys = ["name"])
data class PrescriptionEntity(
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "eye") val eye: String,
    @ColumnInfo(name = "frequency") val frequency: String,
    @ColumnInfo(name = "specialInstructions") val specialInstructions: String,
    @ColumnInfo(name = "expirationDate") val expirationDate: String? = null // This will be initially null and later updated by the user
)