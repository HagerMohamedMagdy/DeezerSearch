package com.search.deezer.models.data;

import android.util.Log;

import com.deezer.sdk.model.Track;
import com.google.gson.JsonObject;
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

    public static  ArrayList<Track> parseJasonTrack(String mUri ,Response<JsonObject> response){
        ArrayList<Track> mLoadMoreTrack=new ArrayList<Track>();
        switch (mUri){
            case  ServerConfig.SEARCH_MORE_TRACK:
                mLoadMoreTrack=  parseLoadMoreTracks(response);
                break;
        }
return mLoadMoreTrack;
    }
public static ArrayList<Track> parseLoadMoreTracks(Response<JsonObject> response){
    Log.e("parseLoadMoreTracks","called");
     ArrayList<Track> mLoadMoreTrack=new ArrayList<Track>();
    JSONObject resultObject = null;
    try {
        resultObject = new JSONObject(response.body().toString());
        JSONArray mTrackArray = resultObject.getJSONArray("data");
        for (int i = 0; i < mTrackArray.length(); i++) {

            Track mTrack= new Track(mTrackArray.getJSONObject(i));
            mLoadMoreTrack.add(mTrack);


        }

    } catch (JSONException e) {
        e.printStackTrace();
    }


    return mLoadMoreTrack;
}


}
