package com.pauloavelar.inventory.dao;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;

public final class ProductDAO {

    public static final String REFRESH_PRODUCT_LIST = "REFRESH";

    public static ArrayList<String> findAll(Context context) {
        ArrayList<String> products = new ArrayList<>();

        SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.query(DbHelper.T_PRODUCT, null, null, null, null, null,
                                 DbHelper.C_PRODUCT_NAME);
        while (cursor.moveToNext()) {
            products.add(cursor.getString(cursor.getColumnIndex(DbHelper.C_PRODUCT_NAME)));
        }
        cursor.close();

        return products;
    }

    public static void insert(Context context, String product) {
        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbHelper.C_PRODUCT_NAME, product);
        db.insert(DbHelper.T_PRODUCT, null, values);

        notifyUpdate(context);
    }

    public static void rename(Context context, String oldName, String newName) {
        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbHelper.C_PRODUCT_NAME, newName);
        String[] args = { oldName };
        db.update(DbHelper.T_PRODUCT, values, DbHelper.C_PRODUCT_NAME + " = ?", args);
        InventoryDAO.renameProducts(context, oldName, newName);

        notifyUpdate(context);
    }

    public static void delete(Context context, String product) {
        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
        String[] args = { product };
        db.delete(DbHelper.T_PRODUCT, DbHelper.C_PRODUCT_NAME + " = ?", args);

        notifyUpdate(context);
    }

    public static void deleteAll(Context context) {
        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
        db.delete(DbHelper.T_PRODUCT, null, null);
    }

    private static void notifyUpdate(Context c) {
        LocalBroadcastManager.getInstance(c).sendBroadcast(new Intent(REFRESH_PRODUCT_LIST));
    }

}