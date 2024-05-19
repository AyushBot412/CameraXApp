package com.example.cameraxapp.MedicineList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cameraxapp.R

/*
This class acts as a bridge between the data source and the UI component.
It converts data from the data sources into view items that can be displayed in the UI component.

Expandable Recyclerview has also been created and this class shows two ViewHolders for it.
 */

class Adapter(private val modelList: MutableList<Model>, private val onExpirationButtonClick: (Model) -> Unit) : RecyclerView.Adapter<Adapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val medicineName: TextView = itemView.findViewById(R.id.medicineNameTextView)
        val layoutDetails: LinearLayout = itemView.findViewById(R.id.layout_details)
        val eye: TextView = itemView.findViewById(R.id.eyeTextView)
        val frequency: TextView = itemView.findViewById(R.id.frequencyTextView)
        val specialInst: TextView = itemView.findViewById(R.id.specialInstructionsTextView)
        val expDate: TextView = itemView.findViewById(R.id.expirationDateTextView)
        val addExpirationDateButton: Button = itemView.findViewById(R.id.expirationDateButton)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.medicine_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = modelList[position]

        holder.medicineName.text = model.medicineName
        holder.eye.text = model.details.eye ?: "N/A"
        holder.frequency.text = model.details.frequency
        holder.specialInst.text = model.details.specialInstruction
        holder.expDate.text = model.details.expirationDate
        holder.addExpirationDateButton.setOnClickListener {
                onExpirationButtonClick(model)
            }

        // Set the visibility of details layout based on the expand/collapse state
        if (model.isExpanded) {
            holder.layoutDetails.visibility = View.VISIBLE
        } else {
            holder.layoutDetails.visibility = View.GONE
        }

        // Toggle expand/collapse when the name is clicked
        holder.medicineName.setOnClickListener {
            model.isExpanded = !model.isExpanded
            notifyItemChanged(position)
        }
    }
    override fun getItemCount(): Int = modelList.size
}

