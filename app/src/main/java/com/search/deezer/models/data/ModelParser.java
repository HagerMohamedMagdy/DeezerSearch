package com.search.deezer.models.data;

import android.util.Log;

import com.deezer.sdk.model.Track;
import com.google.gson.JsonObject;
import com.search.deezer.cashing.DataBaseManger;
import com.search.deezer.models.service.retrofit.ServerConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Response;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

/**
 * Created by Hager.Magdy on 8/23/2017.
 */

public class ModelParser {
    private String mUri;

    public ModelParser(String mUri) {
        this.mUri = mUri;
    }

    public static ArrayList<Track> parseJasonTrack(String mUri, Response<JsonObject> response,String Query) {
        ArrayList<Track> TrackList = new ArrayList<Track>();
        switch (mUri) {
            case ServerConfig.SEARCH_MORE_TRACK:
                TrackList = parseLoadMoreTracks(response);
                break;
            case ServerConfig.SEARCH_HISTORY:
                TrackList = parseandCashTrackHistory(response,Query);
                break;
        }
        return TrackList;
    }

    public static ArrayList<Track> parseLoadMoreTracks(Response<JsonObject> response) {
        Log.e("parseLoadMoreTracks", "called");
        ArrayList<Track> mLoadMoreTrack = new ArrayList<Track>();
        JSONObject resultObject = null;
        try {
            resultObject = new JSONObject(response.body().toString());
            JSONArray mTrackArray = resultObject.getJSONArray("data");

            for (int i = 0; i < mTrackArray.length(); i++) {

                Track mTrack = new Track(mTrackArray.getJSONObject(i));
                mLoadMoreTrack.add(mTrack);

            }
            Log.e("parced list size", mLoadMoreTrack.size() + "y");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return mLoadMoreTrack;
    }

    public static ArrayList<Track> parseandCashTrackHistory(Response<JsonObject> response,String Query) {
        Log.e("CashTrackHistory", "called");
        ArrayList<Track> mSearchHistory = new ArrayList<Track>();
        JSONObject resultObject = null;
        try {
            resultObject = new JSONObject(response.body().toString());
            JSONArray mTrackArray = resultObject.getJSONArray("data");
            DataBaseManger db = new DataBaseManger(DeezerApplication.getAppContext());
            for (int i = 0; i < mTrackArray.length(); i++) {

                Track mTrack = new Track(mTrackArray.getJSONObject(i));
                mSearchHistory.add(mTrack);
                //   save in db as json object
                String stringToBeInserted = mTrackArray.getJSONObject(i).toString();

                db.insertTrack(Query,stringToBeInserted);

            }
            Log.e("cashed list size", mSearchHistory.size() + "y");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return mSearchHistory;
    }
}
