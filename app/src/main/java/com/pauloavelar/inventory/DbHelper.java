package com.pauloavelar.inventory;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    // database information
    public static final int DATABASE_VERSION = 6;
    public static final String DATABASE_NAME = "inventory.db";

    // table names
    public static final String T_INVENTORY = "inventory";
    public static final String T_PRODUCT   = "product";

    // column names
    public static final String C_INV_ID        = "_id";
    public static final String C_INV_PRODUCT   = "product";
    public static final String C_INV_LOT_CODE  = "lot_code";
    public static final String C_INV_BAG_COUNT = "bag_count";
    public static final String C_INV_TIMESTAMP = "date_time";

    public static final String C_PRODUCT_NAME  = "name";

    // table creation
    private static final String CREATE_INVENTORY =
            "create table " + T_INVENTORY + " (" +
                    C_INV_ID   + " integer primary key, " +
                    C_INV_TIMESTAMP + " text, " +
                    C_INV_PRODUCT   + " text, " +
                    C_INV_LOT_CODE  + " text, " +
                    C_INV_BAG_COUNT + " integer)";

    private static final String CREATE_PRODUCT =
            "create table " + T_PRODUCT + " (" +
                    C_PRODUCT_NAME + " text unique not null)";

    private static DbHelper mInstance;

    public synchronized static DbHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DbHelper(context.getApplicationContext());
        }
        return mInstance;
    }

    private DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_INVENTORY);
        db.execSQL(CREATE_PRODUCT);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS inventory");
        db.execSQL("DROP TABLE IF EXISTS product");
        onCreate(db);
    }

}