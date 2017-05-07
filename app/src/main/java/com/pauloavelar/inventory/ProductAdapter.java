package com.pauloavelar.inventory;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    public interface OnInteraction {
        void onClickEdit(String product);
        void onClickDelete(String product);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView product;
        ImageButton edit, delete;

        public ViewHolder(View itemView) {
            super(itemView);
            product = (TextView)    itemView.findViewById(R.id.product_name);
            edit    = (ImageButton) itemView.findViewById(R.id.button_product_edit);
            delete  = (ImageButton) itemView.findViewById(R.id.button_product_delete);
        }

        public void bind(final String productName, final OnInteraction listener) {
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

    public void clearAll() {
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

    public ProductAdapter(OnInteraction listener) {
        mListener = listener;
    }

    public void setItems(ArrayList<String> items) {
        mItems = items;
        notifyDataSetChanged();
    }

}