package com.search.deezer.presenter;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.util.Log;

import com.deezer.sdk.model.Track;
import com.search.deezer.R;
import com.search.deezer.views.IMusicPlayerView;

import java.io.IOException;

import static com.search.deezer.R.id.albumArt;
import static com.search.deezer.R.id.bPlay;

/**
 * Created by Hager.Magdy on 8/21/2017.
 */

public class MusicPlayerPresenterImp implements IMusicPlayerPresenter {
  IMusicPlayerView mMusicPlayerView;

    public MusicPlayerPresenterImp(IMusicPlayerView mMusicPlayerView) {
        this.mMusicPlayerView = mMusicPlayerView;
    }
    /**
     * Function to play a song
     * @param mTrack -
     * */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
    public void playSong(Track mTrack){
        // play song



            mMusicPlayerView.resetPlayer();
            //mp.setDataSource(db.getAllSongs().get(songIndex).getPath());
//"http://e-cdn-preview-9.deezer.com/stream/995cb325bb07665855502248eb4ee104-3.mp3"
            mMusicPlayerView.preparePlayer(mTrack);
            mMusicPlayerView.UpdatePlayer(mTrack);


    }
}
