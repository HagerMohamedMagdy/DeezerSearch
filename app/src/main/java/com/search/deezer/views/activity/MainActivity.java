package com.search.deezer.views.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.deezer.sdk.model.Permissions;
import com.deezer.sdk.model.Track;
import com.deezer.sdk.model.User;
import com.deezer.sdk.network.connect.DeezerConnect;
import com.deezer.sdk.network.connect.event.DialogListener;
import com.deezer.sdk.network.request.DeezerRequest;
import com.deezer.sdk.network.request.DeezerRequestFactory;
import com.deezer.sdk.network.request.event.JsonRequestListener;
import com.deezer.sdk.network.request.event.RequestListener;
import com.google.gson.JsonObject;
import com.search.deezer.R;
import com.search.deezer.adapters.CustomItemClickListener;
import com.search.deezer.adapters.RecyclerViewScrollListener;
import com.search.deezer.adapters.TrackRecycleAdapter;
import com.search.deezer.models.Constants;
import com.search.deezer.models.ItemModel;
import com.search.deezer.models.Utilities;
import com.search.deezer.models.data.DeezerApplication;
import com.search.deezer.models.service.ConnectivityReceiver;
import com.search.deezer.models.service.retrofit.RetrofitServiceInterface;
import com.search.deezer.presenter.IMainActivityPresenter;
import com.search.deezer.presenter.MainActivityPresenterImp;
import com.search.deezer.utils.Utility;
import com.search.deezer.views.IMainActivityView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.search.deezer.R.id.albumArt;
import static com.search.deezer.R.id.bPlay;
import static com.search.deezer.R.id.m_artist;
import static com.search.deezer.R.id.m_title;
import static com.search.deezer.R.id.mini_controller;

public class MainActivity extends BaseActivity implements IMainActivityView, SearchView.OnQueryTextListener, ConnectivityReceiver.ConnectivityReceiverListener, MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {
    //String imageUrl[] = Constants.image;
    String names[] = Constants.name;
    boolean isSwitched;

    @BindView(R.id.my_recycler_view)
    RecyclerView mRecyclerView;
    private TrackRecycleAdapter mTrackAdapter;
    private MenuItem mSearchMenuItem;
    @BindView(R.id.empty_state_view)
    RelativeLayout mContainerLayout;
    IMainActivityPresenter mainPresenter;
    int ViewType = -1;
    @BindView(bPlay)
    ImageButton mplayBtn;
    private ArrayList<Track> mTrackList = new ArrayList<>();

    @BindView(R.id.ConstraintLayout)
    ConstraintLayout coordinatorLayout;
    @BindView(R.id.mini_controller)
    RelativeLayout mini_controller;
    private MediaPlayer mp;
    @BindView(R.id.m_artist)
    TextView m_atrist;
    @BindView(R.id.m_title)
    TextView m_title;
    @BindView(R.id.album_art_nowplayingcard)
    ImageView m_album_art;
    @BindView(R.id.m_song_progress_normal)
    SeekBar m_seek_bar;
    String imageUrl;
    private boolean playPause;
    private Utilities utils;
    private Handler mHandler = new Handler();
    @BindView(R.id.progressBar)
    ProgressBar Loading;
    private Handler mLoadHandler;
    String SearchQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //    ButterKnife.bind(this);
        initView();
        mainPresenter = new MainActivityPresenterImp(this);
        Log.e("On create ", "From MAin");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register connection status listener
        DeezerApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected boolean showHomeAsUp() {
        return false;
    }

    public void initView() {

        coordinatorLayout = (ConstraintLayout) findViewById(R.id
                .ConstraintLayout);
        mLoadHandler = new Handler();
        mRecyclerView.addOnScrollListener(new RecyclerViewScrollListener() {
            @Override
            public void onScrollUp() {

            }

            @Override
            public void onScrollDown() {

            }

            @Override
            public void onLoadMore() {
                loadMoreData();
                //   Toast.makeText(DeezerApplication.getAppContext(), "loadMoreData", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        mSearchMenuItem = menu.findItem(R.id.action_search);

        mSearchMenuItem.setEnabled(true);

        SearchView searchView = (SearchView) mSearchMenuItem.getActionView();
        searchView.setIconified(true);
        searchView.setOnQueryTextListener(this);
        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchView.setFocusable(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_settings:

                supportInvalidateOptionsMenu();

                if (ViewType == 0) {

                    mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
                    ViewType = 1;
                } else if (ViewType == 1) {

                    Constants.INITIAL_STAGE = true;//to start media player as intial condtiton
                    if (mp != null) {
                        mHandler.removeCallbacks(mUpdateTimeTask);
                        mp.stop();

                        mp.release();
                        mp = null;
                        m_seek_bar.setProgress(0);
                        mplayBtn.setImageResource(R.drawable.img_bplay);


                    }
                    showMiniController(false);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                    ViewType = 0;

                }

                mTrackAdapter.notifyDataSetChanged();

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    ///For test
    private List<ItemModel> getList() {
        List<ItemModel> list = new ArrayList<>();
//        for (int i = 0; i < imageUrl.length; i++) {
//            ItemModel model = new ItemModel();
//            model.setName(names[i]);
//            model.setImagePath(imageUrl[i]);
//            list.add(model);
//        }
        return list;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText == null || newText.equals("")) {
            //    showEmptyView();
            return false;
        } else {
            hideEmptyView();

            boolean isConnected = ConnectivityReceiver.isConnected();

            if (isConnected) {
                mTrackList = mainPresenter.SearchTrack(DeezerApplication.getAppContext(), newText);
                SearchQuery = newText;
                Log.e("SearchQuery instate", SearchQuery);
            }

            else
                showSnack(isConnected);


            return true;
        }
    }

    @Override
    public void showEmptyView() {
        mRecyclerView.setVisibility(View.GONE);
        mContainerLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideEmptyView() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mContainerLayout.setVisibility(View.GONE);
    }

    @Override
    public void notifyDataLoaded() {
        mTrackAdapter = new TrackRecycleAdapter(DeezerApplication.getAppContext(), mTrackList, new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                if (ViewType == 0) {//list
                    Intent playSong = new Intent(v.getContext(), MusicPlayerActivity.class);
                    playSong.putExtra("mTrack", mTrackList.get(position));
                    v.getContext().startActivity(playSong);
                } else {//grid

                    Toast.makeText(DeezerApplication.getAppContext(), "Gride is clicked", Toast.LENGTH_SHORT).show();

                    if (mini_controller.getVisibility() == View.VISIBLE) {
                        Constants.INITIAL_STAGE = true;
                        Log.e("mini_controller", "Visible");


                        if (mp != null) {
                            mHandler.removeCallbacks(mUpdateTimeTask);
                            mp.stop();

                            mp.release();
                            mp = null;
                            m_seek_bar.setProgress(0);
                            mplayBtn.setImageResource(R.drawable.img_bplay);
                            intMiniController(mTrackList.get(position));


                        }

                    } else {
                        Log.e("mini_controller", "Not Visisble");
                        showMiniController(true);
                        //call task to play track
                        //update view
                        intMiniController(mTrackList.get(position));
                    }
                }
            }
        });

        if (ViewType == 0 || ViewType == -1) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            ViewType = 0;
        }
        if (ViewType == 1) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        }


        mRecyclerView.setAdapter(mTrackAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mTrackAdapter.notifyDataSetChanged();


    }

    @Override
    public void notifyError() {
        Log.e("Error", "No DTrack Found");
        Toast.makeText(DeezerApplication.getAppContext(), "OOPs! Couldn't Load Data ", Toast.LENGTH_SHORT).show();

    }


    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
            message = "Good! Connected to Internet";
            color = Color.WHITE;
        } else {
            message = "Sorry! Not connected to internet";
            color = Color.RED;
        }

        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    @Override
    public void showMiniController(boolean show) {
        mini_controller.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void UpdateMiniController(Track mTrack) {

        imageUrl = mTrack.getArtist().getPictureUrl();
        m_title.setText(mTrack.getTitle());
        if (m_album_art.getDrawable() == null) {
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(DeezerApplication.getAppContext())
                        .load(imageUrl)
                        .centerCrop()
                        //for defaault image   .error()
                        .into(m_album_art);


            } else {
                Log.e("Empty image", "Found");
            }
        }
        //  albumArt.startAnimation(FadeIn);
        mplayBtn.setImageResource(R.drawable.img_bpause);

        //setProgress Bar Values
        m_seek_bar.setProgress(0);
        m_seek_bar.setMax(100);
        // Updating progress bar
        updateProgressBar();
    }

    /**
     * Update timer on seekbar
     */
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    @Override
    public void resetMiniPlayer() {

        mp.reset();
    }

    @Override
    public void prepareMiniPlayer(Track mTrack) {
        try {
            Log.e("PREPARE Mini", mTrack.getPreviewUrl().toString() + "Found");
            mp.setDataSource(mTrack.getPreviewUrl());


        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
//mp3 will be started after completion of preparing...
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer player) {
                mp.start();
            }

        });
    }

    @Override
    public void showLoading(boolean show) {
        Loading.setVisibility(show ? View.VISIBLE : View.GONE);
    }


    public void intMiniController(final Track mTrack) {
        utils = new Utilities();
        if (mp != null) {

        } else {
            mp = new MediaPlayer();
        }

        imageUrl = mTrack.getArtist().getPictureUrl();
        m_title.setText(mTrack.getTitle());
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(DeezerApplication.getAppContext())
                    .load(imageUrl)
                    // .centerCrop()
//                    //for defaault image   .error()
                    .into(m_album_art);
//
//
        } else {
            Log.e("Empty image", "Found");
        }
        mplayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!playPause) {

                    mplayBtn.setImageResource(R.drawable.img_bpause);
                    if (Constants.INITIAL_STAGE) {
                        Log.e("intial", "called");

                        mainPresenter.playSong(mTrack);

                    } else {
                        Log.e("not intial", "called");
                        if (!mp.isPlaying())
                            mp.start();
                    }
                    playPause = true;
                } else {
                    mplayBtn.setImageResource(R.drawable.img_bplay);
                    if (mp.isPlaying())
                        mp.pause();
                    playPause = false;
                }
            }

        });
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {

            Log.e("Update TimeTask", "called");
            long totalDuration = mp.getDuration();
            long currentDuration = mp.getCurrentPosition();
            //Updating Progress Bar
            int progress = (int) utils.getProgressPercentage(currentDuration, totalDuration);
            Log.d("Progress", "" + progress);
            m_seek_bar.setProgress(progress);
            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }

    };

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mp != null) {
            Constants.INITIAL_STAGE = true;
            playPause = false;
            mplayBtn.setImageResource(R.drawable.img_bplay);
            mHandler.removeCallbacks(mUpdateTimeTask);
            mp.stop();
            // showMiniController(true);
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
    protected void onDestroy() {
        super.onDestroy();
        if (mp != null) {
            mHandler.removeCallbacks(mUpdateTimeTask);
            mp.stop();

            mp.release();
            mp = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    ////Load more data
    private void loadMoreData() {

        mTrackAdapter.showLoading(true);
        mTrackAdapter.notifyDataSetChanged();
        Log.e("SearchQuery in more", SearchQuery);
        mainPresenter.loadMoreTracks(SearchQuery, "25");


    }

    @Override
    public void UpdateScrollData(final ArrayList<Track> newTrackList) {
        mLoadHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mTrackList.size() == 0)
                    Toast.makeText(DeezerApplication.getAppContext(), "No More Data Found !", Toast.LENGTH_SHORT).show();
                else {
                    mTrackList.addAll(0, newTrackList);
                    Log.e("new Track Size", mTrackList.size() + "Found");
                    mTrackAdapter.showLoading(false);
                    mTrackAdapter.notifyDataSetChanged();
                }
            }
        }, 1500);


    }

///////////////


}
