package com.pauloavelar.inventory.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.NavUtils;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.pauloavelar.inventory.R;
import com.pauloavelar.inventory.dao.InventoryDAO;
import com.pauloavelar.inventory.dao.ProductDAO;
import com.pauloavelar.inventory.model.InventoryItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String EXTRA_ITEM  = "item";

    private Spinner mSpProduct;
    private EditText mEtLotCode;
    private EditText mEtBagCount;
    private List<String> mProducts;
    private BroadcastReceiver mReceiver;
    private InventoryItem mItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        }

        mSpProduct  = (Spinner)  findViewById(R.id.spinner_product);
        mEtLotCode  = (EditText) findViewById(R.id.edit_lot_code);
        mEtBagCount = (EditText) findViewById(R.id.edit_bag_count);
        findViewById(R.id.button_save).setOnClickListener(this);
        findViewById(R.id.button_last_lot).setOnClickListener(this);
        Button btDelete = (Button) findViewById(R.id.button_delete);

        loadProductList();
        mEtLotCode.setFilters(new InputFilter[] { new InputFilter.AllCaps() });
        btDelete.setOnClickListener(this);

        if (savedInstanceState != null) {
            mItem = savedInstanceState.getParcelable(EXTRA_ITEM);
        } else if (getIntent().getExtras() != null) {
            mItem = getIntent().getExtras().getParcelable(EXTRA_ITEM);
        }

        if (mItem != null) {
            getSupportActionBar().setTitle(R.string.edit_product);
            if (mProducts.indexOf(mItem.getProduct()) == -1) {
                mProducts.add(mProducts.size() - 1, mItem.getProduct());
                ProductDAO.insert(this, mItem.getProduct());
            }
            mSpProduct.setSelection(mProducts.indexOf(mItem.getProduct()), true);
            mEtLotCode.setText(mItem.getLotCode());
            mEtBagCount.setText(String.valueOf(mItem.getBagCount()));
        } else {
            getSupportActionBar().setTitle(R.string.add_product);
            mItem = new InventoryItem();
            btDelete.setText(R.string.cancel);
        }

        mReceiver = new BroadcastReceiver() {
            @Override public void onReceive(Context context, Intent intent) {
                loadProductList();
            }
        };

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(mReceiver, new IntentFilter(ProductDAO.REFRESH_PRODUCT_LIST));
    }

    private void loadProductList() {
        if (mSpProduct.getSelectedItemPosition() > 0 && mItem != null) {
            mItem.setProduct(mSpProduct.getSelectedItem().toString());
        }

        mProducts = ProductDAO.findAll(this);
        mProducts.add(0, getString(R.string.select_product));
        mProducts.add(getString(R.string.add_new_product));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.product_item, mProducts);
        adapter.setDropDownViewResource(R.layout.product_item_dropdown);
        mSpProduct.setAdapter(adapter);

        mSpProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View v, int position, long id) {
                if (position == mProducts.size() - 1) {
                    mSpProduct.setSelection(0);
                    ProductDialogBuilder.showProductDialog(DetailActivity.this);
                }
            }

            @Override public void onNothingSelected(AdapterView<?> adapterView) { }
        });
        if (mItem != null) {
            mSpProduct.setSelection(mProducts.indexOf(mItem.getProduct()));
        }
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.button_save:
                clickSave();
                break;
            case R.id.button_delete:
                clickDelete();
                break;
            case R.id.button_last_lot:
                clickFillLastLot();
                break;
        }
    }

    private void clickFillLastLot() {
        mEtLotCode.setText(
                      InventoryDAO.getLastLotCode(this, mSpProduct.getSelectedItem().toString()));
    }

    private void clickDelete() {
        InventoryDAO.delete(this, mItem);
        finish();
    }

    private void clickSave() {
        if (mSpProduct.getSelectedItemPosition() == 0) {
            Toast.makeText(this, R.string.error_no_product, Toast.LENGTH_SHORT).show();
        } else if (mEtLotCode.getText().length() == 0) {
            Toast.makeText(this, R.string.error_no_lot_code, Toast.LENGTH_SHORT).show();
        } else if (mEtBagCount.getText().length() == 0) {
            Toast.makeText(this, R.string.error_no_quantity, Toast.LENGTH_SHORT).show();
        } else {
            mItem.setDateTime(new SimpleDateFormat(DATE_FORMAT, Locale.US).format(new Date()));
            mItem.setProduct(mSpProduct.getSelectedItem().toString());
            mItem.setLotCode(mEtLotCode.getText().toString());
            mItem.setBagCount(Integer.parseInt(mEtBagCount.getText().toString()));
            InventoryDAO.save(this, mItem);
            finish();
        }
    }

}