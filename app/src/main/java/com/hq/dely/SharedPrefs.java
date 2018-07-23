package com.hq.dely;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by korir on 1/19/2018.
 */

public class SharedPrefs {
    private static SharedPrefs mInstance;
    private static Context mCtx;

    private static final String SHARED_PREFS ="MySharedPrefs";
    private static final String SHARED_ID ="Id";
    private static final String SHARED_NAME ="Username";
    private static final String SHARED_GENDER ="Gender";

    private SharedPrefs(Context context) {
        mCtx = context;

    }
    public static synchronized SharedPrefs getmInstance(Context context){
        if(mInstance == null){
            mInstance=new SharedPrefs(context);
        }
        return mInstance;
    }
    public boolean userlogIn(String Username,String Gender){
        SharedPreferences SharedPreferences = mCtx.getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= SharedPreferences.edit();

        editor.putString(SHARED_NAME, Username);
        editor.putString(SHARED_GENDER, Gender);
        editor.apply();
        return true;
    }

    public boolean UserIsLoged(){
        SharedPreferences SharedPreferences = mCtx.getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE);
        if(SharedPreferences.getString(SHARED_NAME ,null) != null)
            return true;
        return false;
    }

    public void userlogout(){
        SharedPreferences SharedPreferences = mCtx.getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= SharedPreferences.edit();
        editor.clear();
        editor.apply();
        editor.commit();
    }

    public boolean userChecked(Boolean check){
        SharedPreferences SharedPreferences = mCtx.getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= SharedPreferences.edit();

        editor.putBoolean("Check",check);
        editor.apply();
        return check;
    }

    public void userDetails(int id,String name,String pass,String email,int phone){
        SharedPreferences SharedPreferences = mCtx.getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= SharedPreferences.edit();

        editor.putInt(SHARED_ID, id);
        editor.putString("Name", name);
        editor.putString("Password", pass);
        editor.putString("Email", email);
        editor.putInt("Phone", phone);
        editor.apply();
    }

    public void changePass(String pass){
        SharedPreferences SharedPreferences = mCtx.getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= SharedPreferences.edit();
        editor.putString("Password", pass);
        editor.apply();
    }

    public void changeEmail(String email){
        SharedPreferences SharedPreferences = mCtx.getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= SharedPreferences.edit();

        editor.putString("Email", email);
        editor.apply();
    }

    public void changePhone(int phone){
        SharedPreferences SharedPreferences = mCtx.getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= SharedPreferences.edit();

        editor.putInt("Phone", phone);
        editor.apply();
    }
}
