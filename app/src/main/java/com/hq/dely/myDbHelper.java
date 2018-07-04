package com.hq.dely;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class myDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "delyAppDb";
    public static final String TABLE_FAV = "tableFav";
    public static final String TABLE_CART = "tableCart";
    public static final String KEY_FAVID = "_id";
    public static final String FAV_ID = "favId";
    public static final String FAV_ITEM = "favName";
    public static final String FAV_PRICE = "favPrice";
    public static final String FAV_PARTNER = "favPart";
    public static final String KEY_CARTID = "_id";
    public static final String CART_ID = "cartId";
    public static final String CART_ITEM = "cartName";
    public static final String CART_PRICE = "cartPrice";
    public static final String CART_PARTNER = "cartPart";
    private static final int DATABASE_Version = 1;

    private static final String CREATE_FAV_TABLE = "CREATE TABLE " + TABLE_FAV + "("
            + KEY_FAVID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + FAV_ID + " INTEGER,"
            + FAV_ITEM + " VARCHAR(100),"
            + FAV_PRICE + " INTEGER,"
            + FAV_PARTNER + " VARCHAR(100));";
    private static final String CREATE_CART_TABLE = "CREATE TABLE " + TABLE_CART + "("
            + KEY_CARTID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + CART_ID + " INTEGER,"
            + CART_ITEM + " VARCHAR(100),"
            + CART_PRICE + " INTEGER,"
            + CART_PARTNER + " VARCHAR(100));";

    public myDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_FAV_TABLE);
        db.execSQL(CREATE_CART_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAV);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        onCreate(db);
    }
}
