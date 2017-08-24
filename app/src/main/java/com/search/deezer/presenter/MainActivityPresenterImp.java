package com.search.deezer.presenter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;


import com.deezer.sdk.model.Permissions;
import com.deezer.sdk.model.Track;
import com.deezer.sdk.network.connect.DeezerConnect;
import com.deezer.sdk.network.connect.event.DialogListener;
import com.google.gson.JsonObject;
import com.search.deezer.models.Constants;
import com.search.deezer.models.data.DeezerApplication;
import com.search.deezer.models.data.RequestFactory;
import com.search.deezer.models.service.MyplayerTask;
import com.search.deezer.models.service.retrofit.RertofitServiceManger;
import com.search.deezer.models.service.retrofit.RetrofitServiceInterface;
import com.search.deezer.models.service.retrofit.ServerConfig;
import com.search.deezer.views.IMainActivityView;
import com.search.deezer.views.activity.MainActivity;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Hager.Magdy on 8/19/2017.
 */

public class MainActivityPresenterImp implements IMainActivityPresenter {
    IMainActivityView MainView;

    // The set of Deezer Permissions needed by the app
    String[] permissions = new String[]{
            com.deezer.sdk.model.Permissions.BASIC_ACCESS,
            com.deezer.sdk.model.Permissions.MANAGE_LIBRARY,
            com.deezer.sdk.model.Permissions.LISTENING_HISTORY};


    public MainActivityPresenterImp(IMainActivityView MainView) {
        this.MainView = MainView;


    }


    ArrayList<Track> mTracks = new ArrayList<>();

    @Override
    public ArrayList<Track> SearchTrack(Context context, String constraint) {
        //implementation for search track list
        //law success update view law fail no tracks found message

        DeezerApplication applicationContext = (DeezerApplication) context.getApplicationContext();

        RequestFactory mApiRequestFactory = applicationContext.getApiRequestFactory();
        mApiRequestFactory.newTrackRequest(constraint, new RequestFactory.RequestCallback() {
            @Override
            public void onRequestSuccess(Object o) {

                Log.e("request ", "Success");
                mTracks = (ArrayList<Track>) o;
                Log.e("@@@", mTracks.size() + "Ff");
                for (int i = 0; i < mTracks.size(); i++) {
                    //    Log.e("DTrack name",mTracks.get(i).getName());
                }
                MainView.notifyDataLoaded();
            }


            @Override
            public void onRequestFailed(Exception e) {
                Log.e("request ", "Fail");
            }
        });
        return mTracks;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
    public void playSong(Track mTrack) {
        // play song
        Log.e("play song ", "is called");

        new MyplayerTask(mTrack, MainView).execute(mTrack.getPreviewUrl());



    }

    @Override
    public void loadMoreTracks(String query, String index) {
        Map<String, String> sOptions = new HashMap<String, String>();
        sOptions.put("q", query);
        sOptions.put("index", index);
        RertofitServiceManger retrofit = new RertofitServiceManger(0, ServerConfig.SEARCH_MORE_TRACK, ServerConfig.METHOD_GET_Q, MainView, sOptions
                , DeezerApplication.getAppContext());
        retrofit.makeRequest();


    }

}
