package com.example.cameraxapp.Room

import androidx.room.ColumnInfo
import androidx.room.Entity

/*
The follow class represents tables in our app's database.
Each instance of an Entity data class represents a row in a table for medicines in the app's database.
Essentially, we're creating a "template" for each row in our future database table, by specifying the columns.
 */

//TODO: change to medicine
@Entity(tableName = "medicines_table", primaryKeys = ["medicineName"])
data class Entity(
    @ColumnInfo(name = "medicineName") val medicineName: String,
    @ColumnInfo(name = "leftEyeSelected") val leftEyeSelected: Boolean,
    @ColumnInfo(name = "rightEyeSelected") val rightEyeSelected: Boolean,
    @ColumnInfo(name = "bothEyesSelected") val bothEyesSelected: Boolean,
    @ColumnInfo(name = "frequency") val frequency: String,
    @ColumnInfo(name = "specialInstruction") val specialInstruction: String? = null,
    @ColumnInfo(name = "expirationDate") val expirationDate: String? = null // This will be initially null and later updated by the user
)