package com.search.deezer.presenter;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Build;
import android.os.DeadObjectException;
import android.util.Log;

import com.deezer.sdk.model.Track;
import com.search.deezer.R;
import com.search.deezer.models.data.DeezerApplication;
import com.search.deezer.models.service.MyplayerTask;
import com.search.deezer.views.IMusicPlayerView;
import com.search.deezer.views.activity.MusicPlayerActivity;

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
      Log.e("play song ","is called");
      if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB)
        new MyplayerTask(mTrack,mMusicPlayerView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mTrack.getPreviewUrl());
      else
        new MyplayerTask(mTrack,mMusicPlayerView).execute( mTrack.getPreviewUrl());



    }
}
