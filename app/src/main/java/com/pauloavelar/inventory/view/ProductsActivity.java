package com.pauloavelar.inventory.view;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.pauloavelar.inventory.R;
import com.pauloavelar.inventory.dao.ProductDAO;

public class ProductsActivity extends AppCompatActivity implements DialogInterface.OnClickListener {

    private BroadcastReceiver mReceiver;
    private ProductAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.manage_products);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        }

        RecyclerView list = (RecyclerView) findViewById(R.id.list_products);
        mAdapter = new ProductAdapter(new ProductAdapter.OnInteraction() {
            @Override
            public void onClickEdit(String product) {
                ProductDialogBuilder.showProductDialog(ProductsActivity.this, product);
            }

            @Override
            public void onClickDelete(String product) {
                ProductDAO.delete(ProductsActivity.this, product);
            }
        });
        list.setAdapter(mAdapter);
        list.addItemDecoration(new DividerItemDecoration(this));
        list.setLayoutManager(new LinearLayoutManager(this));
        loadProductList();

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                loadProductList();
            }
        };

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(mReceiver, new IntentFilter(ProductDAO.REFRESH_PRODUCT_LIST));
    }

    private void loadProductList() {
        mAdapter.setItems(ProductDAO.findAll(this));
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_products, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                ProductDialogBuilder.showProductDialog(this);
                return true;
            case R.id.action_clear_all:
                clickClearAll();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void clickClearAll() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.are_you_sure);
        builder.setMessage(R.string.delete_products_message);
        builder.setPositiveButton(R.string.yes_delete, this);
        builder.setNegativeButton(R.string.cancel, null);
        builder.show();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int which) {
        ProductDAO.deleteAll(this);
        mAdapter.clearAll();
    }

}