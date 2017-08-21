package com.search.deezer.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
    boolean isSwitchView = true;
  ArrayList<Track> mTrackList;
    CustomItemClickListener listener;

    public TrackRecycleAdapter(Context context, ArrayList <Track> mTrackList,CustomItemClickListener listener) {
        this.mTrackList = mTrackList;
        this.context = context;
        this.listener = listener;
    }




    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View itemView;
        if (i == LIST_ITEM){
            itemView = LayoutInflater.from(viewGroup.getContext()).inflate( R.layout.item_layout, null);
        }else{
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

        Track model = mTrackList.get(position);
        holder.itemView.setClickable(true);
        holder.itemView.setFocusableInTouchMode(true);
/*
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Clicked",mTrackList.get(position).getTitle() +"Found");

              c

            }
        });*/

//        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
//          @Override
//           public boolean onTouch(View v, MotionEvent event) {
//              Log.e("Touch","Found");
//              Intent playSong= new Intent(v.getContext(), MusicPlayerActivity.class);
//              playSong.putExtra("mTrack",mTrackList.get(position));
//              v.getContext().startActivity(playSong);
//                return false;
//            }
//       });

        initializeViews(model, holder, position);
    }
    @Override
    public int getItemViewType (int position) {
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
        Log.e("Mtracklisk.size",mTrackList.size()+"");
        return mTrackList.size();
    }

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
}
