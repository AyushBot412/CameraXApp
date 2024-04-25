package com.example.cameraxapp.PrescriptionList

/*
This class is the model and is structured this way to help in creating an Expandable RecyclerView
 */
data class PrescriptionModel(
    val name: String,
    val details: Details,
    var isExpanded: Boolean = false // Initially set to false for collapsed state
) {
    data class Details(
        val eye: String,
        val frequency: String,
        val specialInstructions: String,
        val expiryDate: String
    )
}