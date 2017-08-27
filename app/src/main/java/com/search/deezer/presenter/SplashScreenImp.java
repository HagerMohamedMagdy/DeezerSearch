package com.search.deezer.presenter;

import android.os.Bundle;
import android.util.Log;

import com.deezer.sdk.model.Permissions;
import com.deezer.sdk.network.connect.DeezerConnect;
import com.deezer.sdk.network.connect.event.DialogListener;
import com.search.deezer.models.Constants;
import com.search.deezer.models.Utilities;
import com.search.deezer.models.data.DeezerApplication;
import com.search.deezer.views.ISplashScreenView;

import static com.search.deezer.models.Constants.USER_ACCESS_TOKEN;

/**
 * Created by Hager.Magdy on 8/24/2017.
 */

public class SplashScreenImp implements ISplashScreenPresenter {
    ISplashScreenView mSplashView;

    public SplashScreenImp(ISplashScreenView mSplashView) {
        this.mSplashView = mSplashView;
    }

    @Override
    public void getAuthorization() {
        //make authorizatio
        Log.i("Authorization function","called");

        DeezerConnect deezerConnect = new DeezerConnect(DeezerApplication.getAppContext(), Constants.APP_ID);

        // The set of Deezer Permissions needed by the app
       String[] permissions = new String[] {
               Permissions.BASIC_ACCESS,
                Permissions.MANAGE_LIBRARY,
                Permissions.OFFLINE_ACCESS,
                Permissions.LISTENING_HISTORY };
        Log.d("params =","Set of Permissions");
//// The listener for authentication events
        DialogListener listener = new DialogListener() {

           public void onComplete(Bundle values) {

               if (values.getString("access_token") != null && !values.getString("access_token").isEmpty()) {
                   // succes
                   Log.d("Access_token",values.getString("access_token"));
                   Utilities.saveData(USER_ACCESS_TOKEN,values.getString("access_token"));
                   mSplashView.AuthSuccesScinario();
               }else{
                   //Outh failed

               }



                Log.d("Bundle", values.toString());
           }

           public void onCancel() {}

           public void onException(Exception e) {}
       };
      //  Launches the authentication process
       deezerConnect.authorize(DeezerApplication.getCurrentActivity(), permissions, listener);

    }
}
