package com.search.deezer.models.data;

import android.app.Activity;
import android.app.Application;
import android.content.Context;


import com.deezer.sdk.network.connect.DeezerConnect;
import com.search.deezer.R;
import com.search.deezer.models.service.ConnectivityReceiver;

/**
 * Created by Hager.Magdy on 8/19/2017.
 */

public class DeezerApplication extends Application {
    RequestFactory mApiRequestFactory;
    private static Application instance;
    private static DeezerApplication mIinstance;
    private static Context context;
    private static Activity activity;
    @Override
    public void onCreate() {
        super.onCreate();
        DeezerConnect deezerConnect = DeezerConnect.forApp(getString(R.string.deezer_app_id))
                .withContext(this)
                .build();
        mApiRequestFactory = new RequestFactory(deezerConnect);
        DeezerApplication.context = getApplicationContext();
        DeezerApplication.activity = getActivity();
        instance=this;
        mIinstance=this;
    }
    public static Activity getActivity() {
        return DeezerApplication.activity;
    }
    public RequestFactory getApiRequestFactory(){
        return mApiRequestFactory;
    }
    public static Context getAppContext() {

        return instance.getApplicationContext();
        // return MyApplication.context;
    }
    public static synchronized DeezerApplication getInstance() {
        return mIinstance;
    }


    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
    public static void setCurrentActivity(Activity currentActivity) {
        activity = currentActivity;
    }

    public static Activity getCurrentActivity() {
        return activity;
    }
}
