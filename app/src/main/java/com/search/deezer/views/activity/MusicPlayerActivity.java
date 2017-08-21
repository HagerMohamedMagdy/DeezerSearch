package com.search.deezer.views.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.deezer.sdk.model.Track;
import com.search.deezer.R;
import com.search.deezer.adapters.TrackRecycleAdapter;
import com.search.deezer.models.Utilities;
import com.search.deezer.models.data.DeezerApplication;
import com.search.deezer.presenter.IMusicPlayerPresenter;
import com.search.deezer.presenter.MusicPlayerPresenterImp;
import com.search.deezer.views.IMusicPlayerView;

import java.io.IOException;
import java.util.Random;

import butterknife.BindView;



public class MusicPlayerActivity extends Activity implements IMusicPlayerView, MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {


    @BindView(R.id.albumArt)
    ImageView albumArt;
    @BindView(R.id.bPlay)
    ImageView bPlay;
    @BindView(R.id.bForward)
    ImageView bForward;
    @BindView(R.id.bBackward)
    ImageView bBackward;
    @BindView(R.id.bNext)
    ImageView bNext;
    @BindView(R.id.bPrevious)
    ImageView bPrevious;
    @BindView(R.id.bPlaylist)
    ImageView bPlaylist;
    @BindView(R.id.bRepeat)
    ImageView bRepeat;
    @BindView(R.id.bShuffle)
    ImageView bShuffle;
    @BindView(R.id.SongProgress)
    SeekBar songProgressBar;
    @BindView(R.id.totalDuration)
    TextView songTotalDuration;
    @BindView(R.id.SongTitle)
    TextView songTitleLabel;
    @BindView(R.id.currentDuration)
    TextView songCurrentDuration;
    Animation FadeIn;
    Animation FadeOut;
    // Media Player
    private MediaPlayer mp;
    Track mTrack;
    String imageUrl;
    // Handler to update UI timer, progress bar etc,.
    private Handler mHandler = new Handler();
    ;

    private int seekForwardTime = 5000; // 5000 milliseconds
    private int seekBackwardTime = 5000; // 5000 milliseconds
    private int currentSongIndex = 0;
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    IMusicPlayerPresenter mPlayerPresenter;
    private Utilities utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        mTrack = getIntent().getParcelableExtra("mTrack");
        Log.e("Recived track is", mTrack.getTitle());
        mPlayerPresenter = new MusicPlayerPresenterImp(this);
        initView();
    }

    public void initView() {
        bPlay = (ImageButton) findViewById(R.id.bPlay);
        bForward = (ImageButton) findViewById(R.id.bForward);
        bBackward = (ImageButton) findViewById(R.id.bBackward);
        bNext = (ImageButton) findViewById(R.id.bNext);
        bPrevious = (ImageButton) findViewById(R.id.bPrevious);
        bPlaylist = (ImageButton) findViewById(R.id.bPlaylist);
        bRepeat = (ImageButton) findViewById(R.id.bRepeat);
        bShuffle = (ImageButton) findViewById(R.id.bShuffle);
        songProgressBar = (SeekBar) findViewById(R.id.SongProgress);
        songTitleLabel = (TextView) findViewById(R.id.SongTitle);
        songCurrentDuration = (TextView) findViewById(R.id.currentDuration);
        songTotalDuration = (TextView) findViewById(R.id.totalDuration);
        albumArt = (ImageView) findViewById(R.id.albumArt);
        FadeIn = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fadein);
        utils = new Utilities();
        //media player
        mp = new MediaPlayer();
        //Listeners
        songProgressBar.setOnSeekBarChangeListener(this);//Important
        mp.setOnCompletionListener(this);
        imageUrl = mTrack.getArtist().getPictureUrl();
        songTitleLabel.setText(mTrack.getTitle());
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(DeezerApplication.getAppContext())
                    .load(imageUrl)
                    // .centerCrop()
//                    //for defaault image   .error()
                    .into(albumArt);
//
//
        } else {
            Log.e("Empty image", "Found");
        }
//
//        albumArt.startAnimation(FadeIn);
        //playactionBtn
        bPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //put in
                // check for already playing
                if (mp.isPlaying()) {
                    if (mp != null) {
                        mp.pause();
                        // Changing button image to play button
                        bPlay.setImageResource(R.drawable.img_bplay);
                    }
                } else {
                    // Resume song
                    if (mp != null) {

                        mp.start();
                        Log.e("Start called", "herer");
                        // mPlayerPresenter.playSong(mTrack);
                        // Changing button image to pause button
                        bPlay.setImageResource(R.drawable.img_bpause);
                    }
                }

            }
        });
        /**
         * Button Click event for Repeat button
         * Enables repeat flag to true
         * */
        bRepeat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (isRepeat) {
                    isRepeat = false;
                    Toast.makeText(getApplicationContext(), "Repeat is OFF", Toast.LENGTH_SHORT).show();
                    bRepeat.setImageResource(R.drawable.img_btn_repeat);
                } else {
                    //make repeat to true
                    isRepeat = true;
                    Toast.makeText(getApplicationContext(), "Repeat is ON", Toast.LENGTH_SHORT).show();
                    //make shuffle to false
                    isShuffle = false;
                    bRepeat.setImageResource(R.drawable.img_btn_repeat_pressed);
                    bShuffle.setImageResource(R.drawable.img_btn_shuffle);
                }
            }

        });
        bShuffle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (isShuffle) {
                    isShuffle = false;
                    Toast.makeText(getApplicationContext(), "Shuffle is OFF", Toast.LENGTH_SHORT).show();
                    bShuffle.setImageResource(R.drawable.img_btn_shuffle);
                } else {
                    isShuffle = true;
                    isRepeat = false;
                    Toast.makeText(getApplicationContext(), "Shuffle is ON", Toast.LENGTH_SHORT).show();
                    bShuffle.setImageResource(R.drawable.img_btn_shuffle_pressed);
                    bRepeat.setImageResource(R.drawable.img_btn_repeat);
                }
            }

        });
        /**
                  * Forward button click event
                  * Forwards song specified seconds
                  * */
        bForward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //get current song position
                int currentPosition = mp.getCurrentPosition();
                //check if seekForward time is less than song duration
                if (currentPosition + seekForwardTime <= mp.getDuration()) {
                    //forward Sing
                    mp.seekTo(currentPosition + seekForwardTime);
                } else {
                    // forward to end
                    mp.seekTo(mp.getDuration());
                }
            }
        });
        /**
         * Backward button click event
         * Backward song to specified seconds
         * */
        bBackward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // get current song position
                int currentPosition = mp.getCurrentPosition();
                // check if seekBackward time is greater than 0 sec
                if (currentPosition - seekBackwardTime >= 0) {
                    // forward song
                    mp.seekTo(currentPosition - seekBackwardTime);
                } else {
                    // backward to starting position
                    mp.seekTo(0);
                }

            }
        });
        /**
         * Next button click event
         * Plays next song by taking currentSongIndex + 1
         * */
        bNext.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //get current song position

                //check if next song is present
                if (isRepeat) {
                    //repeat is on play same song again
                    mPlayerPresenter.playSong(mTrack);
                } else if (isShuffle) {
                    //shuffle is on--play random song
                    Random rand = new Random();

                    mPlayerPresenter.playSong(mTrack);
                } else {

                }
            }
        });
        /**
         * Back button click event
         * Plays previous song by currentSongIndex - 1
         * */
        bPrevious.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (isRepeat) {
                    //repeat is on play same song again
                    mPlayerPresenter.playSong(mTrack);
                } else if (isShuffle) {
                    //shuffle is on--play random song

                    mPlayerPresenter.playSong(mTrack);
                } else {
                    if (currentSongIndex > 0) {
                        //   mPlayerPresenter.playSong(mTrack - 1);
                        currentSongIndex = currentSongIndex - 1;
                    } else {

                    }
                }

            }
        });

    }


    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.e("onCompletion", "onCompletion");
        // check for repeat on or off

        mPlayerPresenter.playSong(mTrack);


    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    /**
     * When user starts moving the progress handler
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
// remove message Handler from updating Progress Handler
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    /**
     * When user stops moving the progress hanlder
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mp.getDuration();
        int currentPosition = (seekBar.getProgress()) * totalDuration / 100;
        //forward or backward to certain seconds
        System.out.println(seekBar.getProgress());
        System.out.println(currentPosition);
        mp.seekTo(currentPosition);
        //update timer
        updateProgressBar();
    }

    @Override
    public void UpdatePlayer(Track mTrack) {
//setimage and title
        imageUrl = mTrack.getArtist().getPictureUrl();
        songTitleLabel.setText(mTrack.getTitle());
        if (albumArt.getDrawable() == null) {
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(DeezerApplication.getAppContext())
                        .load(imageUrl)
                        .centerCrop()
                        //for defaault image   .error()
                        .into(albumArt);


            } else {
                Log.e("Empty image", "Found");
            }
        }
        //  albumArt.startAnimation(FadeIn);
        bPlay.setImageResource(R.drawable.img_bpause);

        //setProgress Bar Values
        songProgressBar.setProgress(0);
        songProgressBar.setMax(100);
        // Updating progress bar
        updateProgressBar();
    }

    /**
     * Update timer on seekbar
     */
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    /**
     * Background Runnable thread
     */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {

            Log.e("Update TimeTask", "called");
            long totalDuration = mp.getDuration();
            long currentDuration = mp.getCurrentPosition();
            // Displaying total time
            songTotalDuration.setText("" + utils.millisecondsToTimer(totalDuration));
            //displaying cuurent time
            songCurrentDuration.setText("" + utils.millisecondsToTimer(currentDuration));

            //Updating Progress Bar
            int progress = (int) utils.getProgressPercentage(currentDuration, totalDuration);
            Log.d("Progress", "" + progress);
            songProgressBar.setProgress(progress);
            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }

    };

    @Override
    public void resetPlayer() {
        mp.reset();
    }

    @Override
    public void preparePlayer(Track mTrack) {

        try {
            Log.e("PREPARE", mTrack.getPreviewUrl().toString() + "Found");
            mp.setDataSource(mTrack.getPreviewUrl());
            //TODO
//            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mp) {
//                    progressDialog=ProgressDialog.show(DeezerApplication.getAppContext(), "Loading", "Playing...");
//                }
//            });

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mp.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mp != null) {
            mHandler.removeCallbacks(mUpdateTimeTask);
            mp.stop();
            ;
            mp.release();
            mp = null;
        }
    }
}
