package com.search.deezer.views;

import android.view.View;

import com.deezer.sdk.model.Track;

import java.util.ArrayList;

/**
 * Created by Hager.Magdy on 8/24/2017.
 */

public interface ISearchHistoryView {
    void UpdateSearchList(ArrayList<Track> mTrackList);
    void notifyError();
    void onEmptyResult();

}
