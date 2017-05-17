package com.pauloavelar.inventory.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pauloavelar.inventory.model.InventoryItem;

import java.util.ArrayList;

public final class InventoryDAO {

    public static void save(Context context, InventoryItem item) {
        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(DbHelper.C_INV_PRODUCT, item.getProduct());
        values.put(DbHelper.C_INV_TIMESTAMP, item.getDateTime());
        values.put(DbHelper.C_INV_LOT_CODE, item.getLotCode());
        values.put(DbHelper.C_INV_BAG_COUNT, item.getBagCount());

        if (item.getId() > 0) {
            values.put(DbHelper.C_INV_ID, item.getId());
            String[] args = new String[] { String.valueOf(item.getId()) };
            db.update(DbHelper.T_INVENTORY, values, DbHelper.C_INV_ID + " = ?", args);
        } else {
            db.insert(DbHelper.T_INVENTORY, null, values);
        }
    }

    public static ArrayList<InventoryItem> getAll(Context context) {
        ArrayList<InventoryItem> items = new ArrayList<>();
        SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.query(DbHelper.T_INVENTORY, null, null, null, null, null,
                                 DbHelper.C_INV_TIMESTAMP + " DESC");
        while (cursor.moveToNext()) {
            items.add(new InventoryItem(
                cursor.getLong(cursor.getColumnIndex(DbHelper.C_INV_ID)),
                cursor.getString(cursor.getColumnIndex(DbHelper.C_INV_TIMESTAMP)),
                cursor.getString(cursor.getColumnIndex(DbHelper.C_INV_PRODUCT)),
                cursor.getString(cursor.getColumnIndex(DbHelper.C_INV_LOT_CODE)),
                cursor.getInt(cursor.getColumnIndex(DbHelper.C_INV_BAG_COUNT))
            ));
        }
        cursor.close();

        return items;
    }

    public static void delete(Context context, InventoryItem item) {
        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
        String[] args = new String[] { String.valueOf(item.getId()) };
        db.delete(DbHelper.T_INVENTORY, DbHelper.C_INV_ID + " = ?", args);
    }

    public static void deleteAll(Context context) {
        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
        db.delete(DbHelper.T_INVENTORY, null, null);
    }

    public static String getLastLotCode(Context context, String product) {
        SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();
        String[] columns = { DbHelper.C_INV_LOT_CODE };
        String[] args = { product };
        Cursor cursor = db.query(DbHelper.T_INVENTORY, columns, DbHelper.C_INV_PRODUCT + " = ?",
                                 args, null, null, DbHelper.C_INV_TIMESTAMP + " DESC", "1");

        String lastLot = "";
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            lastLot = cursor.getString(cursor.getColumnIndex(DbHelper.C_INV_LOT_CODE));
        }
        cursor.close();
        return lastLot;
    }

    public static String exportAllToCSV(Context context) {
        StringBuilder csv = new StringBuilder();
        csv.append("Datetime,Product,Lot Code,Bag count").append(System.getProperty("line.separator"));

        ArrayList<InventoryItem> items = getAll(context);
        for (int i = 0; i < items.size(); i++) {
            InventoryItem item = items.get(i);
            csv.append(item.getDateTime()).append(",");
            csv.append(item.getProduct()).append(",");
            csv.append(item.getLotCode()).append(",");
            csv.append(item.getBagCount()).append(System.getProperty("line.separator"));
        }

        return csv.toString();
    }

    static void renameProducts(Context context, String oldName, String newName) {
        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbHelper.C_INV_PRODUCT, newName);
        String[] args = { oldName };
        db.update(DbHelper.T_INVENTORY, values, DbHelper.C_INV_PRODUCT + " = ?", args);
    }

}