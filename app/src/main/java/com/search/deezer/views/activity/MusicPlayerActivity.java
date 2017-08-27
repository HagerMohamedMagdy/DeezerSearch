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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.deezer.sdk.model.Track;
import com.deezer.sdk.player.Player;
import com.search.deezer.R;
import com.search.deezer.adapters.TrackRecycleAdapter;
import com.search.deezer.models.Constants;
import com.search.deezer.models.Utilities;
import com.search.deezer.models.data.DeezerApplication;
import com.search.deezer.models.service.ConnectivityReceiver;
import com.search.deezer.presenter.IMusicPlayerPresenter;
import com.search.deezer.presenter.MusicPlayerPresenterImp;
import com.search.deezer.utils.Utility;
import com.search.deezer.views.IMusicPlayerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.os.Looper.prepare;
import static android.util.Log.e;


public class MusicPlayerActivity extends Activity implements IMusicPlayerView, ConnectivityReceiver.ConnectivityReceiverListener, MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {


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
    @BindView(R.id.progressBar)
    ProgressBar Loading;
    private boolean playPause;
    private int seekForwardTime = 5000; // 5000 milliseconds
    private int seekBackwardTime = 5000; // 5000 milliseconds
    private int currentSongIndex = 0;
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    IMusicPlayerPresenter mPlayerPresenter;
    private Utilities utils;
    @BindView(R.id.parent_view)
    RelativeLayout parent_view;
    ArrayList<Track> mTrackList;
    int trackPosition;
    private boolean mCancel = false;
    boolean prepared = false;
    boolean cancel = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        ButterKnife.bind(this);
        mTrack = getIntent().getParcelableExtra("mTrack");
        e("Recived track is", mTrack.getTitle());
        mTrackList = getIntent().getParcelableArrayListExtra("mTrackList");

        trackPosition = getIntent().getIntExtra("mPosition", trackPosition);

        mPlayerPresenter = new MusicPlayerPresenterImp(this);
        initView();
    }

    public void initView() {
        e("Track in Initview", mTrack.getTitle());
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

                    .into(albumArt);


        } else {
            e("Empty image", "Found");
        }


        bPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {


                if (!playPause) {

                    bPlay.setImageResource(R.drawable.img_bpause);
                    if (Constants.INITIAL_STAGE) {

                        if (ConnectivityReceiver.isConnected())
                            mPlayerPresenter.playSong(mTrack);
                        else
                            Utility.showSnack(false, parent_view);
                    } else {

                        if (!mp.isPlaying())
                            mp.start();
                    }
                    playPause = true;
                } else {
                    bPlay.setImageResource(R.drawable.img_bplay);
                    if (mp.isPlaying())
                        mp.pause();
                    playPause = false;
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
                    Toast.makeText(DeezerApplication.getAppContext(), getResources().getString(R.string.repeat_of), Toast.LENGTH_SHORT).show();
                    bRepeat.setImageResource(R.drawable.img_btn_repeat);
                } else {
                    //make repeat to true
                    isRepeat = true;
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.repeat_on), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.shuffle_of), Toast.LENGTH_SHORT).show();
                    bShuffle.setImageResource(R.drawable.img_btn_shuffle);
                } else {
                    isShuffle = true;
                    isRepeat = false;
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.shuffle_on), Toast.LENGTH_SHORT).show();
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
                Log.e("Forword", "presssed");
                if (Loading.getVisibility() == View.VISIBLE) {
                    Toast.makeText(DeezerApplication.getAppContext(), R.string.action_search, Toast.LENGTH_SHORT);
                } else {
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
                Log.e("next ", "iscalled");
                showLoading(false);
                playPause = false;

                // TODO Auto-generated method stub
                //get current song position


                if (mp != null) {
                    if (trackPosition < (mTrackList.size()) - 1) {
                        Constants.INITIAL_STAGE = true;
                        mHandler.removeCallbacks(mUpdateTimeTask);
                        mp.reset();
                        mp.release();
                        mp = null;
                        songProgressBar.setProgress(0);
                        bPlay.setImageResource(R.drawable.img_bplay);
                        mTrack = mTrackList.get(trackPosition + 1);
                        trackPosition++;
                        initView();
                    } else {
                        Toast.makeText(DeezerApplication.getAppContext(), R.string.last_track, Toast.LENGTH_SHORT).show();
                    }
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
                Log.e("previous ", "iscalled");
                showLoading(false);
                playPause = false;


                if (mp != null) {
                    if (trackPosition > 0) {
                        Constants.INITIAL_STAGE = true;
                        mHandler.removeCallbacks(mUpdateTimeTask);
                        mp.reset();
                        mp.release();
                        mp = null;
                        songProgressBar.setProgress(0);
                        bPlay.setImageResource(R.drawable.img_bplay);
                        mTrack = mTrackList.get(trackPosition - 1);
                        trackPosition--;
                        initView();
                    } else {
                        Toast.makeText(DeezerApplication.getAppContext(), R.string.first_track, Toast.LENGTH_SHORT).show();

                    }
                }

            }
        });

    }


    @Override
    public void onCompletion(MediaPlayer mp) {
        e("onCompletion", "onCompletion");
        // check for repeat on or off
        if (isRepeat) {
            if (mp != null) {
                Log.e("Repeat same song", "found");
                Constants.INITIAL_STAGE = true;
                mHandler.removeCallbacks(mUpdateTimeTask);
                mp.reset();
                mp.release();
                mp = null;
                songProgressBar.setProgress(0);
                bPlay.setImageResource(R.drawable.img_bplay);

                initView();
            }

        } else {
            if (mp != null) {
                Constants.INITIAL_STAGE = true;
                playPause = false;
                bPlay.setImageResource(R.drawable.img_bplay);
                mHandler.removeCallbacks(mUpdateTimeTask);
                mp.stop();

                //  mp.release();
                //  mp = null;
            }
        }

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
                        .error(R.drawable.ic_empty_music2)
                        //for defaault image   .error()
                        .into(albumArt);


            } else {
                e("Empty image", "Found");
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

            //     Log.e("Update TimeTask", "called");
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
        Log.e("reset player", "called");
        mp.reset();
    }

    @Override
    public void preparePlayer(Track mTrack) {

        try {
            e("PREPARE", mTrack.getPreviewUrl().toString() + "Found");
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
            Log.e("mp.prepare ", "found");
            mp.prepare();
            mp.start();
        } catch (IOException e) {
            Log.e("mp.prepare EXp", "found");
            e.printStackTrace();
        }
//mp3 will be started after completion of preparing...
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer player) {
                Log.e("onprepare", "finish");
                prepared = true;
                /*

                if(mCancel){
                    player.release();
                    prepared = false;
                    mCancel = false;
                    //nullify your MediaPlayer reference
                    mp = null;
                }*/
                Log.e("media player ", "start");

                //  mp.start();


            }

        });
        //  mp.start();
    }

    @Override
    public void showLoading(boolean show) {


        Loading.setVisibility(show ? View.VISIBLE : View.GONE);
    }


    @Override
    protected void onDestroy() {
        Log.e("ondestroy ", "called");
        super.onDestroy();
        Constants.INITIAL_STAGE = true;
        //
        if (mp != null) {
            mHandler.removeCallbacks(mUpdateTimeTask);
            //  mp.stop(); crash while preparing
            mp.reset();
            mp.release();
            mp = null;
        }

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        Utility.showSnack(false, parent_view);
    }

    //    public void showLoading(boolean show) {
//        Loading.setVisibility(show ? View.VISIBLE : View.GONE);
//
//    }
    public void playNexPrev(Track mTrack) {
        e("playPause", playPause + " dd");
        if (!playPause) {

            bPlay.setImageResource(R.drawable.img_bpause);
            if (Constants.INITIAL_STAGE) {
                e("intial", "called");
                if (ConnectivityReceiver.isConnected())
                    mPlayerPresenter.playSong(mTrack);
                else
                    Utility.showSnack(false, parent_view);
            } else {
                e("not intial", "called");
                if (!mp.isPlaying())
                    mp.start();
            }
            playPause = true;
        } else {
            bPlay.setImageResource(R.drawable.img_bplay);
            if (mp.isPlaying())
                mp.pause();
            playPause = false;
        }

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
            e("Empty image", "Found");
        }
    }
}
