package com.example.cameraxapp.Room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

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

    @Update
     fun updatePrescription(prescription: PrescriptionEntity)


}