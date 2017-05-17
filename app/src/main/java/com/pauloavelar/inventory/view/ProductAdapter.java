package com.pauloavelar.inventory.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.pauloavelar.inventory.R;

import java.util.ArrayList;

class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    interface OnInteraction {
        void onClickEdit(String product);
        void onClickDelete(String product);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView product;
        ImageButton edit, delete;

        ViewHolder(View itemView) {
            super(itemView);
            product = (TextView)    itemView.findViewById(R.id.product_name);
            edit    = (ImageButton) itemView.findViewById(R.id.button_product_edit);
            delete  = (ImageButton) itemView.findViewById(R.id.button_product_delete);
        }

        void bind(final String productName, final OnInteraction listener) {
            product.setText(productName);
            edit.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                    listener.onClickEdit(productName);
                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                    listener.onClickDelete(productName);
                }
            });
        }
    }

    private ArrayList<String> mItems;
    private OnInteraction mListener;

    void clearAll() {
        if (mItems != null) mItems.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext())
                                     .inflate(R.layout.product_row, parent, false);
        return new ViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String item = mItems.get(position);
        holder.bind(item, mListener);
    }

    @Override
    public long getItemId(int i) { return 0; }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    ProductAdapter(OnInteraction listener) {
        mListener = listener;
    }

    void setItems(ArrayList<String> items) {
        mItems = items;
        notifyDataSetChanged();
    }

}