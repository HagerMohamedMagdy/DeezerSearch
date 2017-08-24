package com.search.deezer.views;

import com.deezer.sdk.model.Track;

import java.util.ArrayList;

/**
 * Created by Hager.Magdy on 8/19/2017.
 */

public interface IMainActivityView {
    void showEmptyView();
    void hideEmptyView();
    void notifyDataLoaded();
    void notifyError();
    void showMiniController(boolean show);
    void UpdateMiniController(Track mTrack);

    void resetMiniPlayer();
    void prepareMiniPlayer(Track mTrack);
    void showLoading(boolean show);
    void UpdateScrollData( ArrayList<Track> newTrackList);

}
