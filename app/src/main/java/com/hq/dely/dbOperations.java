package com.hq.dely;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static com.hq.dely.myDbHelper.TABLE_FAV;

public class dbOperations {
    myDbHelper helper;

    public dbOperations(myDbHelper helper) {
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

    public String getData()
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = {myDbHelper.FAV_ID,myDbHelper.FAV_ITEM,myDbHelper.FAV_PRICE,myDbHelper.FAV_PARTNER};
        Cursor cursor =db.query(TABLE_FAV,columns,null,null,null,null,null);
        StringBuffer buffer= new StringBuffer();
        while (cursor.moveToNext())
        {
            int id =cursor.getInt(cursor.getColumnIndex(myDbHelper.FAV_ID));
            String name =cursor.getString(cursor.getColumnIndex(myDbHelper.FAV_ITEM));
            int price =cursor.getInt(cursor.getColumnIndex(myDbHelper.FAV_PRICE));
            String  part =cursor.getString(cursor.getColumnIndex(myDbHelper.FAV_PARTNER));
            buffer.append(id+ "   " + name + "   " + price +"  "+ part +"\n");
        }
        Log.d("items", buffer.toString());
        return buffer.toString();
    }

}
