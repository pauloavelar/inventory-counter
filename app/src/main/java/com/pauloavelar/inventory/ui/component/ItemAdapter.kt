package com.pauloavelar.inventory.ui.component

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pauloavelar.inventory.R
import com.pauloavelar.inventory.model.InventoryItem

class ItemAdapter(
    private val listener: OnItemInteraction
) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    private var mItems: List<InventoryItem> = emptyList()

    fun setItems(items: List<InventoryItem>) {
        mItems = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rowView = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_list, parent, false)
        return ViewHolder(rowView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mItems[position], listener)
    }

    override fun getItemId(i: Int): Long = mItems[i].id!!

    override fun getItemCount(): Int = mItems.size

    interface OnItemInteraction {
        fun onItemClick(item: InventoryItem)
        fun onItemLongPress(item: InventoryItem)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var txtProduct: TextView = view.findViewById(R.id.list_product)
        private var txtLotNumber: TextView = view.findViewById(R.id.list_lot_number)
        private var txtItemCount: TextView = view.findViewById(R.id.list_item_count)

        fun bind(item: InventoryItem, listener: OnItemInteraction) {
            txtProduct.text = item.product
            txtLotNumber.text = item.lotNumber
            txtItemCount.text = item.count.toString()

            itemView.setOnClickListener {
                listener.onItemClick(item)
            }

            itemView.setOnLongClickListener {
                listener.onItemLongPress(item)
                true
            }
        }
    }

}
