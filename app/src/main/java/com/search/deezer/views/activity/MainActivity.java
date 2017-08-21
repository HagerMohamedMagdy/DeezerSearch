package com.search.deezer.views.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.deezer.sdk.model.Track;
import com.search.deezer.R;
import com.search.deezer.adapters.CustomItemClickListener;
import com.search.deezer.adapters.TrackRecycleAdapter;
import com.search.deezer.models.Constants;
import com.search.deezer.models.ItemModel;
import com.search.deezer.models.data.DeezerApplication;
import com.search.deezer.models.service.ConnectivityReceiver;
import com.search.deezer.presenter.IMainActivityPresenter;
import com.search.deezer.presenter.MainActivityPresenterImp;
import com.search.deezer.views.IMainActivityView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements IMainActivityView, SearchView.OnQueryTextListener, ConnectivityReceiver.ConnectivityReceiverListener {
    String imageUrl[] = Constants.image;
    String names[] = Constants.name;


    @BindView(R.id.my_recycler_view)
    RecyclerView mRecyclerView;
    private TrackRecycleAdapter mTrackAdapter;
    private MenuItem mSearchMenuItem;
    @BindView(R.id.empty_state_view)
    RelativeLayout mContainerLayout;
    IMainActivityPresenter mainPresenter;

    private ArrayList<Track> mTrackList;
    @BindView(R.id.ConstraintLayout)
    ConstraintLayout coordinatorLayout;

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

        mTrackList = new ArrayList<>();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //  mFilteredTrack=new FilteredTrack(DeezerApplication.getAppContext(),this);

//    List<ItemModel> list = getList();
//    mAdapter = new RecyclerAdapter(this, );
//    mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//    mRecyclerView.setAdapter(mAdapter);
//    mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        //  mAdapter = new RecyclerAdapter(this, mFilteredTrack);


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
                //TODO grideView
                //     boolean isSwitched = mTrackAdapter.toggleItemViewType();

                //supportInvalidateOptionsMenu();


                //   mRecyclerView.setLayoutManager(isSwitched ? new LinearLayoutManager(this) : new GridLayoutManager(this, 2));

                //   mTrackAdapter.notifyDataSetChanged();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    ///For test
    private List<ItemModel> getList() {
        List<ItemModel> list = new ArrayList<>();
        for (int i = 0; i < imageUrl.length; i++) {
            ItemModel model = new ItemModel();
            model.setName(names[i]);
            model.setImagePath(imageUrl[i]);
            list.add(model);
        }
        return list;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText == null || newText.equals("")) {
            showEmptyView();
            return false;
        } else {
            hideEmptyView();

            boolean isConnected = ConnectivityReceiver.isConnected();

            if (isConnected)
                mTrackList = mainPresenter.SearchTrack(DeezerApplication.getAppContext(), newText);
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

                Log.e("MTrack is", mTrackList.get(position).getTitle());
                // Log.e("MTrack is",mTrackList.get(position+1).getTitle());
                Intent playSong = new Intent(v.getContext(), MusicPlayerActivity.class);
                playSong.putExtra("mTrack", mTrackList.get(position));
                v.getContext().startActivity(playSong);
            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mTrackAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        //   mTrackAdapter.notifyDataSetChanged();
        //     mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void notifyError() {
        Log.e("Error", "No DTrack Found");
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
}
