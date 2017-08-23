package com.search.deezer.presenter;

import android.content.Context;

import com.deezer.sdk.model.Track;

import java.util.ArrayList;

/**
 * Created by Hager.Magdy on 8/19/2017.
 */

public interface IMainActivityPresenter {
ArrayList<Track> SearchTrack(Context context, String  constraint);
    void playSong(Track mTrack);
}
