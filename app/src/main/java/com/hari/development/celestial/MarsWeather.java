package com.hari.development.celestial;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;


/**
 * Created by development on 24/09/15.
 */
public class MarsWeather extends Application {

    private RequestQueue requestQueue;
    private static MarsWeather mInstance;

    public static final String TAG = MarsWeather.class.getName();

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        requestQueue = Volley.newRequestQueue(getApplicationContext());
    }

    public static synchronized MarsWeather getInstance(){
        return mInstance;
    }

    public RequestQueue getRequestQueue(){
        return requestQueue;
    }

    public <T> void add(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancel() {
        requestQueue.cancelAll(TAG);
    }

}
