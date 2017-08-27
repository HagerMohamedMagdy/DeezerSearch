package com.search.deezer.views.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.deezer.sdk.model.Track;
import com.search.deezer.R;
import com.search.deezer.adapters.CustomItemClickListener;
import com.search.deezer.adapters.TrackRecycleAdapter;
import com.search.deezer.models.Constants;
import com.search.deezer.models.Utilities;
import com.search.deezer.models.data.DeezerApplication;
import com.search.deezer.models.service.ConnectivityReceiver;
import com.search.deezer.presenter.SearchHistoryPresenter;
import com.search.deezer.presenter.SearchHistoryPresenterImp;
import com.search.deezer.utils.Utility;
import com.search.deezer.views.ISearchHistoryView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.search.deezer.R.id.mini_controller;
import static com.search.deezer.R.id.my_recycler_view;

public class SearchHistoryActivity extends AppCompatActivity implements ISearchHistoryView, ConnectivityReceiver.ConnectivityReceiverListener {
    SearchHistoryPresenter mSearchPresenter;
    @BindView(R.id.parent_view)
    RelativeLayout parent_view;
    @BindView(R.id.toolbar_title)
    TextView toolbar_title;
    @BindView(R.id.history_btn)
    Button history_btn;
    @BindView(R.id.track_ed)
    EditText track_ed;
    @BindView(R.id.history_recycler_view)
    RecyclerView mRecyclerView;
    TrackRecycleAdapter mTrackAdapter;
    @BindView(R.id.no_data_tv)
    TextView no_data_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_history);

        initView();
    }

    public void initView() {
        ButterKnife.bind(this);
        mSearchPresenter = new SearchHistoryPresenterImp(this);
        toolbar_title.setVisibility(View.VISIBLE);
        toolbar_title.setText(R.string.search_history);
        //  mSearchPresenter.getSearchHistory();
        history_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utility.isEmptyString(track_ed.getText().toString())) {
                    onEmptyResult();
                } else {

                    if (ConnectivityReceiver.isConnected()) {
                        mSearchPresenter.getSearchHistory(track_ed.getText().toString());
                    } else {
                        //if offline read from  the db
                        Utility.showSnack(false, parent_view);
                        mSearchPresenter.getCashedSearchHistory(track_ed.getText().toString());
                    }
                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        // register connection status listener
        DeezerApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    public void UpdateSearchList(final ArrayList<Track> mTrackList) {


        mTrackAdapter = new TrackRecycleAdapter(DeezerApplication.getAppContext(), mTrackList, new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent playSong = new Intent(v.getContext(), MusicPlayerActivity.class);
                playSong.putExtra("mTrack", mTrackList.get(position));
                playSong.putExtra("mPosition", position);
                playSong.putExtra("mTrackList", mTrackList);
                v.getContext().startActivity(playSong);
            }
        });

        if (mTrackList.size() == 0) {
            mRecyclerView.setVisibility(View.GONE);
            no_data_view.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mRecyclerView.setAdapter(mTrackAdapter);
            mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
            mTrackAdapter.notifyDataSetChanged();
        }


    }

    @Override
    public void notifyError() {
//{"error":{"type":"ParameterException","message":"Wrong parameter: q (empty parameter)","code":500}}
    }

    @Override
    public void onEmptyResult() {
        Toast.makeText(DeezerApplication.getAppContext(), "please enter a track name ", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        Utility.showSnack(isConnected, parent_view);

    }

}
