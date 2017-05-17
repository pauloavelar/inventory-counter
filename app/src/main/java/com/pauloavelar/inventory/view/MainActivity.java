package com.pauloavelar.inventory.view;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.pauloavelar.inventory.R;
import com.pauloavelar.inventory.dao.InventoryDAO;
import com.pauloavelar.inventory.model.InventoryItem;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
                       implements AlertDialog.OnClickListener, View.OnClickListener {

    private static final int PERMISSION_EXTERNAL_STORAGE = 15444;

    private InventoryItemAdapter mAdapter;
    private View mEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        findViewById(R.id.fab).setOnClickListener(this);

        mEmptyView = findViewById(R.id.inventory_empty);
        mAdapter = new InventoryItemAdapter(new InventoryItemAdapter.OnItemInteraction() {
            @Override public void onItemClick(InventoryItem item) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra(DetailActivity.EXTRA_ITEM, item);
                startActivity(intent);
            }

            @Override public void onItemLongPress(InventoryItem item) {
                Toast.makeText(MainActivity.this, item.getDateTime(), Toast.LENGTH_SHORT).show();
            }
        });

        RecyclerView list = (RecyclerView) findViewById(R.id.list_items);
        list.setAdapter(mAdapter);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.addItemDecoration(new DividerItemDecoration(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // It should be async, but the data structure is too simple to make it hang noticeably
        // Also there is no need to reload the entire dataset after changing one item
        // But it would not boost performance and this has been tested in low end devices (Moto G1)
        mAdapter.setItems(InventoryDAO.getAll(this));
        // damn you Google and your lack of empty view support on ReciclerViews!
        mEmptyView.setVisibility(mAdapter.getItemCount() == 0 ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                sendCsvViaEmail();
                return true;
            case R.id.action_clear_all:
                clickClearAll();
                return true;
            case R.id.action_products:
                startActivity(new Intent(this, ProductsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void clickClearAll() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirm_clear_all);
        builder.setPositiveButton(R.string.yes_delete, this);
        builder.setNegativeButton(R.string.no, null);
        builder.show();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void sendCsvViaEmail() {
        if (!isExternalStorageWritable()) {
            Toast.makeText(this, R.string.unable_to_save, Toast.LENGTH_SHORT).show();
        } else if (!requestPermissions()) {
            return;
        }

        String now = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss", Locale.US).format(new Date());
        String filename = "inventory." + now + ".csv";
        try {
            File d = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            d.mkdirs();

            File csv = new File(d, filename);
            FileOutputStream fos = new FileOutputStream(csv);
            String content = InventoryDAO.exportAllToCSV(this);
            fos.write(content.getBytes());
            fos.close();

            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("vnd.android.cursor.dir/email");
            emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(csv));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject));

            startActivity(Intent.createChooser(emailIntent, getString(R.string.chooser_title)));
        } catch (Exception e) {
            Toast.makeText(this, R.string.error_csv, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    private boolean requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                                                             PackageManager.PERMISSION_GRANTED) {

            // async call requesting permissions
            ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            }, PERMISSION_EXTERNAL_STORAGE);

            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                                            @NonNull int[] results) {
        switch (requestCode) {
            case PERMISSION_EXTERNAL_STORAGE: {
                if (results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED) {
                    sendCsvViaEmail();
                } else {
                    Toast.makeText(this, R.string.hint_permissions, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int buttonId) {
        InventoryDAO.deleteAll(this);
        mAdapter.clearAll();
        mEmptyView.setVisibility(mAdapter.getItemCount() == 0 ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                startActivity(new Intent(MainActivity.this, DetailActivity.class));
        }
    }

}