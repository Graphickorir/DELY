package com.hq.dely;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by korir on 1/19/2018.
 */

public class Singleton {
        private static Singleton mInstance;
        private RequestQueue mReqQueue;
        private static Context mCtx;

        private Singleton(Context context) {
            mCtx = context;
            mReqQueue = getRequestQueue();

        }
        public static synchronized Singleton getmInstance(Context context){
            if(mInstance == null){
                mInstance=new Singleton(context);
            }
            return mInstance;
        }
        public RequestQueue getRequestQueue(){
            if (mReqQueue==null){
                mReqQueue= Volley.newRequestQueue(mCtx.getApplicationContext());
            }
            return mReqQueue;
        }
        public <T> void addToRequestQueue(Request<T> req){
            getRequestQueue().add(req);
        }

}
