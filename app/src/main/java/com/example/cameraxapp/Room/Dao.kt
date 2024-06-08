package com.example.cameraxapp.Room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.cameraxapp.MedicineList.Model
import kotlinx.coroutines.flow.Flow

/*
The following class defines and provides methods that our app can use to query, update, insert, and delete data in our created database.
 */

// TODO: Will need to revamp some of the functions here to create CRUD methodology
@Dao
interface Dao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(medicineEntity: Entity): Long
    @Transaction
     fun insertPrescription(medicineEntities: List<Entity>) {
         medicineEntities.forEach {
             insert(it)
         }
     }

    @Query("SELECT * FROM medicines_table")
     fun getPrescription(): Flow<List<Entity>>

     //getMedicine() is used for the expiration date
    @Query("SELECT * FROM medicines_table WHERE medicineName = :medicineName LIMIT 1")
    fun getMedicine(medicineName: String): Entity?

    @Query("UPDATE medicines_table SET expirationDate = :newExpirationDate WHERE medicineName = :medicineName")
    suspend fun editExpirationDate(medicineName: String, newExpirationDate: String)

    @Query("DELETE FROM medicines_table WHERE medicineName IN (:medicines)")
    suspend fun deletePrescription(medicines: List<String>)

    @Query("DELETE FROM medicines_table WHERE medicineName = :medicineName")
    suspend fun deleteMedicine(medicineName: String)


    // TODO: updateMedicine()
    // This feature will take quite a while, thus will be continued in fall or summer,
    // depending on who works on it.
}