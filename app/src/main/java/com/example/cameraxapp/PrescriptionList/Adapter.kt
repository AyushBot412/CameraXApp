import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cameraxapp.PrescriptionList.Model
import com.example.cameraxapp.R

/*
This class acts as a bridge between the data source and the UI component.
It converts data from the data sources into view items that can be displayed in the UI component.

Expandable Recyclerview has also been created and this class shows two ViewHolders for it.
 */
class Adapter(
    private val onItemClick: (Model) -> Unit, private val onExpirationButtonClick: (Model) -> Unit,  // Callback for item click events
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val modelList: MutableList<Model> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            COLLAPSED_VIEW_TYPE -> {
                CollapsedViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.medicine_item_collapsed, parent, false))
            }
            EXPANDED_VIEW_TYPE -> {
                ExpandedViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.medicine_item_expanded, parent, false))
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int = modelList.size
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val prescription = modelList[position]

        when (holder) {
            is CollapsedViewHolder -> holder.bind(prescription)
            is ExpandedViewHolder -> holder.bind(prescription)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (modelList[position].isExpanded) EXPANDED_VIEW_TYPE else COLLAPSED_VIEW_TYPE
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<Model>) {
        modelList.clear()
        modelList.addAll(newList)
        notifyDataSetChanged()
    }

    inner class CollapsedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val model = modelList[position]
                    model.isExpanded = !model.isExpanded
                    notifyItemChanged(position)
                }
            }
        }

        fun bind(model: Model) {
            // Bind data to views for collapsed state
            itemView.findViewById<TextView>(R.id.medicineNameTextView).text = model.medicineName
        }
    }

    inner class ExpandedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val addExpirationDateButton: Button = itemView.findViewById(R.id.expirationDateButton)
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val model = modelList[position]
                    model.isExpanded = !model.isExpanded
                    notifyItemChanged(position)
                }
            }
        }

        fun bind(model: Model) {
            // Bind data to views for expanded state
            itemView.findViewById<TextView>(R.id.medicineNameTextView).text = model.medicineName
            itemView.findViewById<TextView>(R.id.eyeTextView).text = model.details.eye
            itemView.findViewById<TextView>(R.id.frequencyTextView).text = model.details.frequency
            itemView.findViewById<TextView>(R.id.specialInstructionsTextView).text = model.details.specialInstruction
            itemView.findViewById<TextView>(R.id.expirationDateTextView).text = model.details.expirationDate
            addExpirationDateButton.setOnClickListener {
                onExpirationButtonClick(model)
            }
        }
    }

    companion object {
        private const val COLLAPSED_VIEW_TYPE = 0
        private const val EXPANDED_VIEW_TYPE = 1
    }

}

