package com.search.deezer.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.deezer.sdk.model.Track;
import com.search.deezer.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.media.CamcorderProfile.get;

/**
 * Created by Hager.Magdy on 8/19/2017.
 */

public class TrackRecycleAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private static final int LIST_ITEM = 0;
    private static final int GRID_ITEM = 1;
    private static final int LOADER_ITEM = 2;
    boolean isSwitchView = true;
     ArrayList<Track> mTrackList;
    CustomItemClickListener listener;
    protected boolean showLoader;
    public TrackRecycleAdapter(Context context, ArrayList <Track> mTrackList,CustomItemClickListener listener) {
        this.mTrackList = mTrackList;
        this.context = context;
        this.listener = listener;
    }




    @Override
    //i:viewtype
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View itemView;
        if (i == LIST_ITEM){
            itemView = LayoutInflater.from(viewGroup.getContext()).inflate( R.layout.item_layout, null);
        }

        else if (i == LOADER_ITEM) {

            // Your Loader XML view here
            // View view = mInflater.inflate(R.layout.loader_item_layout, viewGroup, false);
            itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.loader_item_layout, null);

            // Your LoaderViewHolder class
            return new LoaderViewHolder(itemView);

        }else {
            itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_view_grid, null);
        }

        final ItemViewHolder item = new ItemViewHolder(itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, item.getLayoutPosition());
            }
        });
        return item;
    }
    @Override
    public void onBindViewHolder( RecyclerView.ViewHolder holder, final int position) {
        // Loader ViewHolder
        if (holder instanceof LoaderViewHolder) {
            LoaderViewHolder loaderViewHolder = (LoaderViewHolder)holder;
            if (showLoader) {
                loaderViewHolder.mProgressBar.setVisibility(View.VISIBLE);
            } else {
                loaderViewHolder.mProgressBar.setVisibility(View.GONE);
            }

            return;
        }else{
        Track model = mTrackList.get(position);
        holder.itemView.setClickable(true);
        holder.itemView.setFocusableInTouchMode(true);

        initializeViews(model, holder, position);
        }
    }
    @Override
    public int getItemViewType (int position) {

        // loader can't be at position 0
        // loader can only be at the last position
        if (position != 0 && position == getItemCount() - 1) {
            return LOADER_ITEM;
        }
        if (isSwitchView){
            return LIST_ITEM;
        }else{
            return GRID_ITEM;
        }

    }

    public boolean toggleItemViewType () {
        isSwitchView = !isSwitchView;
        return isSwitchView;
    }

    @Override
    public int getItemCount() {
        // If no items are present, there's no need for loader
        if (mTrackList == null || mTrackList.size() == 0) {
            return 0;
        }

        // +1 for loader
        return mTrackList.size() + 1;
      //  return mTrackList.size();
    }
//    @Override
//    public long getItemId(int position) {
//
//        // loader can't be at position 0
//        // loader can only be at the last position
//        if (position != 0 && position == getItemCount() - 1) {
//
//            // id of loader is considered as -1 here
//            return -1;
//        }
//        return mTrackList.get(position).ge;
//    }
    private void initializeViews(Track model, final RecyclerView.ViewHolder holder, int position) {

        String imageUrl = model.getArtist().getPictureUrl();;
        if (imageUrl != null && !imageUrl.isEmpty()){
            Glide.with(context)
                    .load(imageUrl)
                    .centerCrop()
                    //for defaault image   .error()
                    .into(((ItemViewHolder)holder).imageView);


        }
        ((ItemViewHolder)holder).name.setText(model.getShortTitle());
       ((ItemViewHolder)holder).artist.setText(model.getArtist().getName());



    }



    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.imageView)
        ImageView imageView;
        @BindView(R.id.artist)
        TextView artist;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static  class LoaderViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.progressbar)
        ProgressBar mProgressBar;

        public LoaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
    public void showLoading(boolean status) {
        showLoader = status;
    }
}
