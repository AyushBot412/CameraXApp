package com.example.cameraxapp.Room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

/*
The following class defines and provides methods that our app can use to query, update, insert, and delete data in our created database.
 */
@Dao
interface Dao {

    // Create Methods
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(medicineEntity: Entity): Long
    @Transaction
     fun insertAll(medicineEntities: List<Entity>) {
         medicineEntities.forEach {
             insert(it)
         }
     }

    // Read Methods:
    @Query("SELECT * FROM medicines_table")
     fun getPrescription(): Flow<List<Entity>>

     // getMedicine() is for the expiration date
    @Query("SELECT * FROM medicines_table WHERE medicineName = :medicineName LIMIT 1")
    fun getMedicine(medicineName: String): Entity?
    @Query("UPDATE medicines_table SET expirationDate = :newExpirationDate WHERE medicineName = :medicineName")
    suspend fun editExpirationDate(medicineName: String, newExpirationDate: String)

     // Update Methods:

     // Delete Methods:





     // CRUD: (Prescriptions & Medicine)
     // C:
            // scan qr code,
                    // insertPrescription()
            // add medicine button in instruction:
                    // insertMedicine()?
                    // - Ask Dr. Lin if users will ever add medicine by itself

            // - Scan QR Code Fragment Button:
            // When pressed, a new list
            //  a, c, v (existing) + a, d, v (new) = update to a, d, v
            // dialog box: doing this will delete old prescription, do you want to update?
            // yes:
            // no:

            // - Instruction Fragment:
            // - No Prescription Added when fragment empty
            // if fragment.isnotempty()
            //   - Add New Medicine To Current Prescription Button
            // a,c,v + d = a, c, d, v
            // a, c, v + a, d, v = a, c, d, v

     // R:
            // getPrescription()
     // U:
            // updateMedicine()?
                // in front of each field, edit button
                // - Ask Dr. Lin if user will individually update medicine
     // D:
            // deletePrescription()? (or delete the app)
                // - Ask Dr. Lin if they ever want to delete the prescription
            // deleteMedicine()?
                // - Ask Dr. Lin if they ever want to medicine or will the clinic just make a new prescription?




     // tests
    // 1. Testing scanning same medicine twice: reinserts the same medicine with updated fields (exp date is gone)
    // TODO: Update if medicine is already in there instead of re-inserting
}