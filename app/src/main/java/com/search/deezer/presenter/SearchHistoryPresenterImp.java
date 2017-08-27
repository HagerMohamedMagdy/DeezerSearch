package com.search.deezer.presenter;

import android.util.Log;

import com.deezer.sdk.model.Track;
import com.deezer.sdk.player.networkcheck.NetworkStateChecker;
import com.search.deezer.cashing.DataBaseManger;
import com.search.deezer.models.Constants;
import com.search.deezer.models.Utilities;
import com.search.deezer.models.data.DeezerApplication;
import com.search.deezer.models.service.ConnectivityReceiver;
import com.search.deezer.models.service.retrofit.RertofitServiceManger;
import com.search.deezer.models.service.retrofit.ServerConfig;
import com.search.deezer.utils.Utility;
import com.search.deezer.views.ISearchHistoryView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hager.Magdy on 8/24/2017.
 */

public class SearchHistoryPresenterImp implements SearchHistoryPresenter

{

    ISearchHistoryView searchView;

    public SearchHistoryPresenterImp(ISearchHistoryView searchView) {
        this.searchView = searchView;
    }

    @Override
    public void getSearchHistory(String query) {
        Log.i("getSearchHistory func.", "called");
        Log.e("onLine Data", "found");

        Map<String, String> mparams = new HashMap<String, String>();
        mparams.put("q", query);

        //"response_type=token"
        mparams.put("access_token", Utilities.getData(Constants.USER_ACCESS_TOKEN));

        //log  params
        for (String key : mparams.keySet()) {
            Object value = mparams.get(key);
            if (value != null) {
                Log.d("Parameter value= ", key.toString() + ":" + value.toString());

            }
        }
        RertofitServiceManger retrofit = new RertofitServiceManger(0, ServerConfig.SEARCH_HISTORY, ServerConfig.METHOD_GET_Q, searchView, mparams
                , DeezerApplication.getAppContext(), query);
        retrofit.makeRequest();


    }


    @Override
    public void getCashedSearchHistory(String query) {
        Log.i("getCashedSearchHistory","called");
        Log.e("cashed data", "Found");
        //read from db with selected query
        DataBaseManger db = new DataBaseManger(DeezerApplication.getAppContext());
        ArrayList<JSONObject> mJsonTrack = new ArrayList<>();
        mJsonTrack = db.getCashedHistory(query);
        ArrayList<Track> mCashedTrack = new ArrayList<Track>();
        for (int i = 0; i < mJsonTrack.size(); i++) {

            Track mTrack = null;
            try {
                mTrack = new Track(mJsonTrack.get(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mCashedTrack.add(mTrack);

        }
        searchView.UpdateSearchList(mCashedTrack);
    }
}
