package com.search.deezer.views;

import com.deezer.sdk.model.Track;

/**
 * Created by Hager.Magdy on 8/21/2017.
 */

public interface IMusicPlayerView {
    void UpdatePlayer(Track mTrack);
    void resetPlayer();
    void preparePlayer(Track mTrack);
    void showLoading(boolean show);

}
