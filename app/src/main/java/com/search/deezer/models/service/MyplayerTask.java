package com.search.deezer.models.service;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.deezer.sdk.model.Track;
import com.search.deezer.models.Constants;
import com.search.deezer.models.data.DeezerApplication;
import com.search.deezer.views.IMainActivityView;
import com.search.deezer.views.IMusicPlayerView;
import com.search.deezer.views.activity.MusicPlayerActivity;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import static android.os.Build.VERSION_CODES.M;

/**
 * Created by Hager.Magdy on 8/22/2017.
 */

public class MyplayerTask extends AsyncTask<String, String, String> {


    //  final String file="song.mp3";
    Track mtrack;
    IMusicPlayerView mMusicPlayerView;
    IMainActivityView mainActivityView;

    public MyplayerTask(Track mtrack, IMusicPlayerView mMusicPlayerView) {

        this.mtrack = mtrack;
        this.mMusicPlayerView = mMusicPlayerView;
    }

    public MyplayerTask(Track mtrack, IMainActivityView mainActivityView) {

        this.mtrack = mtrack;
        this.mainActivityView = mainActivityView;
    }

    public void onPreExecute() {

        if (mainActivityView == null) {

            mMusicPlayerView.showLoading(true);
        } else {
            mainActivityView.showLoading(true);
        }


    }

    @Override
    protected String doInBackground(String... f_url) {
        // TODO Auto-generated method stub
        int count;

        //  try {
/*
                    URL url = new URL(f_url[0]);

                    URLConnection conection = url.openConnection();

                    conection.connect();

                    // Get Music file length

                    int lenghtOfFile = conection.getContentLength();

                    // input stream to read file - with 8k buffer

                    InputStream input = new BufferedInputStream(url.openStream(),10*1024);

                    // Output stream to write file in SD card

                    OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().getPath()+"/jai_ho.mp3");

                    byte data[] = new byte[1024];

                    long total = 0;

                    while ((count = input.read(data)) != -1) {

                        total += count;

                        // Publish the progress which triggers onProgressUpdate method

                        publishProgress("" + (int) ((total * 100) / lenghtOfFile));



                        // Write data to file

                        output.write(data, 0, count);

                    }

                    // Flush output

                    output.flush();

                    // Close streams

                    output.close();

                    input.close();

                } catch (Exception e) {

                    Log.e("Error: ", e.getMessage());

                }
                */
        if (mainActivityView == null) {
            mMusicPlayerView.resetPlayer();
            mMusicPlayerView.preparePlayer(mtrack);
        } else {
            Log.e("Mini Controller", "background");
            mainActivityView.resetMiniPlayer();
            mainActivityView.prepareMiniPlayer(mtrack);
        }
        return null;
//////////////
    }
    // While Downloading Music File

    protected void onProgressUpdate(String... progress) {

        // Set progress percentage

        //   progress.ssetProgress(Integer.parseInt(progress[0]));

    }


    // Once Music File is downloaded
    @Override

    protected void onPostExecute(String file_url) {
        Constants.INITIAL_STAGE = false;
        // Dismiss the dialog after the Music file was downloaded
        if (mainActivityView == null) {

            mMusicPlayerView.showLoading(false);
            mMusicPlayerView.UpdatePlayer(mtrack);
        } else {
            mainActivityView.showLoading(false);
            mainActivityView.UpdateMiniController(mtrack);
        }

    }


}
