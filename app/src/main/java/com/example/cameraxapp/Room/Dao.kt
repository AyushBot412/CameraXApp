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

// TODO: Will need to revamp some of the functions here to create CRUD methodology
@Dao
interface Dao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(medicineEntity: Entity): Long
    @Transaction
     fun insertAll(medicineEntities: List<Entity>) {
         medicineEntities.forEach {
             insert(it)
         }
     }

    @Query("SELECT * FROM medicines_table")
     fun getPrescription(): Flow<List<Entity>>

     // getMedicine() is for the expiration date
    @Query("SELECT * FROM medicines_table WHERE medicineName = :medicineName LIMIT 1")
    fun getMedicine(medicineName: String): Entity?
    @Query("UPDATE medicines_table SET expirationDate = :newExpirationDate WHERE medicineName = :medicineName")
    suspend fun editExpirationDate(medicineName: String, newExpirationDate: String)

    // CRUD: (Prescriptions & Medicine)

    // C:
    // insertPrescription()
    // One Scan QR Code button is pressed, the prescription should be inserted into DB
    // If button is pressed again, a new prescription overwrites old one.

    // insertMedicine()
    // todo: write definition of what this function does

    // R:
    // getPrescription()
    // When user navigates to instruction fragments, they should see their prescription
    // getMedicine()
    // todo: write definition of what this function does

    // U:
    // updatePrescription()
    // todo: write definition of what this function does
    // updateMedicine()
    // todo: definition of what this function does

    // D:
    // deletePrescription()
    // There should be a delete button in the top corner of the prescription card, user presses it and the prescription is deleted.
    // deleteMedicine()
    // There should be a delete button in front of every medicine card, user presses it and the medicine is deleted






    // SCRATCH NOTES BELOW, DO NOT MIND

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
}