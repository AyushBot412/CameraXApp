package com.example.cameraxapp.PrescriptionList

import com.example.cameraxapp.Room.PrescriptionEntity

/*
This class is the model and is structured this way to help in creating an Expandable RecyclerView
 */
data class PrescriptionModel(
    val medicineName: String,
    val details: Details,
    var isExpanded: Boolean = false // Initially set to false for collapsed state
) {
    data class Details(
        val eye: String?,
        val frequency: String,
        val specialInstruction: String,
        val expirationDate: String
    ){
        companion object {
            fun fromPrescriptionEntity(entity: PrescriptionEntity): Details {
                val eye = when {
                    entity.leftEyeSelected -> "Left"
                    entity.rightEyeSelected -> "Right"
                    entity.bothEyesSelected -> "Both"
                    else -> null
                }
                return Details(
                    eye = eye,
                    frequency = entity.frequency,
                    specialInstruction = entity.specialInstruction,
                    expirationDate = entity.expirationDate?: ""
                )
            }
        }
    }
}




