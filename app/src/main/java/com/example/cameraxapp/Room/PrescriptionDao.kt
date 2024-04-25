package com.example.cameraxapp.Room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/*
The following class defines and provides methods that our app can use to query, update, insert, and delete data in our created database.
 */
@Dao
interface PrescriptionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(prescription: PrescriptionEntity): Long

    @Transaction
     fun insertAll(prescriptions: List<PrescriptionEntity>) {
         prescriptions.forEach {
             insert(it)
         }
     }
    @Query("SELECT * FROM prescriptions_table")
     fun getAllPrescriptions(): Flow<List<PrescriptionEntity>>

    @Query("UPDATE prescriptions_table SET expirationDate = :newExpirationDate WHERE name = :prescriptionName")
    suspend fun updateExpirationDateByName(prescriptionName: String, newExpirationDate: String)

    @Query("SELECT * FROM prescriptions_table WHERE name = :prescriptionName LIMIT 1")
     fun getPrescriptionByName(prescriptionName: String): PrescriptionEntity?

}