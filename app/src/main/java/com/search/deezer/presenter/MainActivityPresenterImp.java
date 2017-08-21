package com.search.deezer.presenter;

import android.content.Context;
import android.util.Log;


import com.deezer.sdk.model.Track;
import com.search.deezer.models.data.DeezerApplication;
import com.search.deezer.models.data.RequestFactory;
import com.search.deezer.views.IMainActivityView;



import java.util.ArrayList;

/**
 * Created by Hager.Magdy on 8/19/2017.
 */

public class MainActivityPresenterImp implements IMainActivityPresenter {
    IMainActivityView MainView;

    // The set of Deezer Permissions needed by the app
    String[] permissions = new String[] {
            com.deezer.sdk.model.Permissions.BASIC_ACCESS,
            com.deezer.sdk.model.Permissions.MANAGE_LIBRARY,
            com.deezer.sdk.model.Permissions.LISTENING_HISTORY };


    public MainActivityPresenterImp(IMainActivityView MainView) {
        this.MainView = MainView;


    }

    ArrayList<Track> mTracks=new ArrayList<>();
    @Override
    public ArrayList<Track> SearchTrack(Context context, String constraint) {
        //implementation for search track list
        //law success update view law fail no tracks found message

        DeezerApplication applicationContext = (DeezerApplication) context.getApplicationContext();

      RequestFactory mApiRequestFactory = applicationContext.getApiRequestFactory();
        mApiRequestFactory.newTrackRequest( constraint, new RequestFactory.RequestCallback() {
            @Override
            public void onRequestSuccess(Object o) {

              Log.e("request ","Success");
                mTracks = (ArrayList<Track>) o;
                Log.e("@@@",mTracks.size() +"Ff");
                for(int i=0;i<mTracks.size();i++){
                //    Log.e("DTrack name",mTracks.get(i).getName());
                }
                MainView.notifyDataLoaded();
            }


            @Override
            public void onRequestFailed(Exception e) {
                Log.e("request ","Fail");
            }
        });
        return mTracks;
    }
}
