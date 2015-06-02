package com.enroute.enroute.utility;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by charlesx on 11/5/14.
 */
public class VolleyInstance {
    private static VolleyInstance mInstance;
    private RequestQueue mRequestQueue;
    private static Context mContext;

    private VolleyInstance(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized VolleyInstance getInstance(Context context) {
        if (mInstance == null) mInstance = new VolleyInstance(context);
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
