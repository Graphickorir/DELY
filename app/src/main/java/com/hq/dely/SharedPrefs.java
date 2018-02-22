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
    private static final String SHARED_NAME ="Username";

    private SharedPrefs(Context context) {
        mCtx = context;

    }
    public static synchronized SharedPrefs getmInstance(Context context){
        if(mInstance == null){
            mInstance=new SharedPrefs(context);
        }
        return mInstance;
    }
    public boolean userlogIn(String Username){
        SharedPreferences SharedPreferences = mCtx.getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= SharedPreferences.edit();

        editor.putString(SHARED_NAME, Username);
        editor.apply();
        return true;
    }

    public boolean UserIsLoged(){
        SharedPreferences SharedPreferences = mCtx.getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE);
        if(SharedPreferences.getString(SHARED_NAME ,null) != null)
            return true;
        return false;
    }

    public boolean userlogout(){
        SharedPreferences SharedPreferences = mCtx.getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= SharedPreferences.edit();

        editor.clear();
        editor.apply();
        return true;
    }
}
