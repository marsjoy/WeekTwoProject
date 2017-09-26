package com.marswilliams.com.fakenews.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marswilliams.com.fakenews.R;
import com.marswilliams.com.fakenews.ViewHolders.NoThumbnailViewHolder;
import com.marswilliams.com.fakenews.ViewHolders.ThumbnailViewHolder;
import com.marswilliams.com.fakenews.models.Doc;

import java.util.List;

/**
 * Created by mars_williams on 9/22/17.
 */

public class NewsItemRecyclerViewAdapter extends RecyclerView.Adapter {


    private Context mContext;
    private List<Doc> mData;
    public final static String TAG = "NewsItemAdapter";
    private final static int THUMBNAIL_VIEW = 0;
    private final static int NO_THUMBNAIL_VIEW = 1;

    public NewsItemRecyclerViewAdapter(Context context, List<Doc> data){
        mContext = context;
        mData = data;
    }

    public void addData(List<Doc> data){
        int initalSize = mData.size();
        mData.addAll(data);
        int finalSize = mData.size();
        notifyItemRangeChanged(initalSize,finalSize-initalSize);
    }
    public List<Doc> getData(){
        return mData;
    }

    public void clear(){
        mData.clear();
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);

        switch (viewType){
            case THUMBNAIL_VIEW:
                View thumbnailView  = inflater.inflate(R.layout.news_item_layout,parent,false);
                return new ThumbnailViewHolder(thumbnailView, mContext);
            case NO_THUMBNAIL_VIEW:
                View noThumbnailView  = inflater.inflate(R.layout.no_thumbnail_news_item,parent,false);
                return new NoThumbnailViewHolder(noThumbnailView);
            default:
                View defaultView  = inflater.inflate(R.layout.no_thumbnail_news_item,parent,false);
                return new NoThumbnailViewHolder(defaultView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Doc doc = mData.get(position);
        switch (holder.getItemViewType()){
            case THUMBNAIL_VIEW:
                ((ThumbnailViewHolder) holder).bind(doc);
                break;
            case NO_THUMBNAIL_VIEW:
                ((NoThumbnailViewHolder) holder).bind(doc);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        Doc doc = mData.get(position);

        if(doc.getThumbnailURL()==null){
            return NO_THUMBNAIL_VIEW;
        }
        return THUMBNAIL_VIEW;

    }
}
