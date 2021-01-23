package com.pauloavelar.inventory.ui.component

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pauloavelar.inventory.R
import com.pauloavelar.inventory.model.Product

class ProductAdapter(
    private val listener: OnItemInteraction
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    private var mItems: List<Product> = emptyList()

    fun setItems(items: List<Product>) {
        mItems = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rowView = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_row, parent, false)
        return ViewHolder(rowView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mItems[position], listener)
    }

    override fun getItemId(i: Int): Long = i.toLong()

    override fun getItemCount(): Int = mItems.size

    interface OnItemInteraction {
        fun onClickEdit(product: Product)
        fun onClickDelete(product: Product)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtProductName: TextView = view.findViewById(R.id.product_name)
        var btnEdit: ImageButton = view.findViewById(R.id.button_product_edit)
        var btnDelete: ImageButton = view.findViewById(R.id.button_product_delete)

        fun bind(product: Product, listener: OnItemInteraction) {
            txtProductName.text = product.name
            btnEdit.setOnClickListener { listener.onClickEdit(product) }
            btnDelete.setOnClickListener { listener.onClickDelete(product) }
        }
    }

}
