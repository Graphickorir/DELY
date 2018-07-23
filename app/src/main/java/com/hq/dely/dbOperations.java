package com.hq.dely;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import static com.hq.dely.myDbHelper.TABLE_FAV;

public class dbOperations {
    private myDbHelper helper;

    dbOperations(myDbHelper helper) {
        this.helper = helper;
    }

    public void addFavItem(int favid,String favitem,int favprice,String favpart) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(myDbHelper.FAV_ID, favid);
        values.put(myDbHelper.FAV_ITEM, favitem);
        values.put(myDbHelper.FAV_PRICE, favprice);
        values.put(myDbHelper.FAV_PARTNER, favpart);

        db.insert(TABLE_FAV, null, values);
        db.close();
    }
    public void addCartItem(int cartid,String cartitem,int cartprice,String cartpart) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(myDbHelper.CART_ID, cartid);
        values.put(myDbHelper.CART_ITEM, cartitem);
        values.put(myDbHelper.CART_PRICE, cartprice);
        values.put(myDbHelper.CART_PARTNER, cartpart);

        db.insert(myDbHelper.TABLE_CART, null, values);
        db.close();
    }

    public void removeFromFav(int favid) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(TABLE_FAV, myDbHelper.FAV_ID + " = ? ",
                new String[]{String.valueOf(favid)});
        db.close();
    }
    public void removeFromCart(int cartid) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(myDbHelper.TABLE_CART, myDbHelper.CART_ID + " = ? ",
                new String[]{String.valueOf(cartid)});
        db.close();
    }

    public boolean checkFav(int favid){
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor mCursor = db.rawQuery("SELECT * FROM "
                + TABLE_FAV + " WHERE " + myDbHelper.FAV_ID + "=?", new String[]{String.valueOf(favid)});
        if (mCursor.getCount() <= 0){
            return false;}//record exist
        else{
            return true;}//record not exist
    }
    public boolean checkCart(int cartid){
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor mCursor = db.rawQuery("SELECT * FROM "
                + myDbHelper.TABLE_CART + " WHERE " + myDbHelper.CART_ID + "=?", new String[]{String.valueOf(cartid)});
        if (mCursor.getCount() <= 0){
            return false;}//record exist
        else{
            return true;}//record not exist
    }

    public long getProfilesCount() {
        SQLiteDatabase db = helper.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, myDbHelper.TABLE_CART);
        db.close();
        return count;
    }

    public int getcarttotal(){
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + helper.CART_PRICE + ") AS Total FROM " + helper.TABLE_CART, null);
        int total = 0;
        if (cursor.moveToFirst())
            total = cursor.getInt(cursor.getColumnIndex("Total"));
        return total;
    }
}
