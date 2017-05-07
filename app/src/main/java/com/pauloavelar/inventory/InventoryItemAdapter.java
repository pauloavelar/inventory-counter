package com.pauloavelar.inventory;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class InventoryItemAdapter extends RecyclerView.Adapter<InventoryItemAdapter.ViewHolder> {

    public interface OnItemInteraction {
        void onItemClick(InventoryItem item);
        void onItemLongPress(InventoryItem item);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView product, lotCode, bagCount;

        public ViewHolder(View itemView) {
            super(itemView);
            product  = (TextView) itemView.findViewById(R.id.list_product);
            lotCode  = (TextView) itemView.findViewById(R.id.list_lot_code);
            bagCount = (TextView) itemView.findViewById(R.id.list_bag_count);
        }

        public void bind(final InventoryItem item, final OnItemInteraction listener) {
            product.setText(item.getProduct());
            lotCode.setText(item.getLotCode());
            bagCount.setText(String.valueOf(item.getBagCount()));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                    listener.onItemClick(item);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override public boolean onLongClick(View view) {
                    listener.onItemLongPress(item);
                    return true;
                }
            });
        }
    }

    private ArrayList<InventoryItem> mItems;
    private OnItemInteraction mListener;

    public void clearAll() {
        if (mItems != null) mItems.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext())
                                     .inflate(R.layout.layout_list, parent, false);
        return new ViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        InventoryItem item = mItems.get(position);
        holder.bind(item, mListener);
    }

    @Override
    public long getItemId(int i) {
        return (mItems.get(i) == null ? 0 : mItems.get(i).getId());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public InventoryItemAdapter(OnItemInteraction listener) {
        mListener = listener;
    }

    public void setItems(ArrayList<InventoryItem> items) {
        mItems = items;
        notifyDataSetChanged();
    }

}