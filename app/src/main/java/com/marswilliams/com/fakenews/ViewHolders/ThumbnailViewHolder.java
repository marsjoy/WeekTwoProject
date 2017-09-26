package com.marswilliams.com.fakenews.ViewHolders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.marswilliams.com.fakenews.R;
import com.marswilliams.com.fakenews.databinding.NewsItemLayoutBinding;
import com.marswilliams.com.fakenews.models.Doc;

import static android.content.ContentValues.TAG;

/**
 * Created by mars_williams on 9/22/17.
 */

public class ThumbnailViewHolder extends RecyclerView.ViewHolder{
    final NewsItemLayoutBinding mNewsItemLayoutBinding;
    private Context mContext;

    public ThumbnailViewHolder(View view, Context context){
        super(view);
        mNewsItemLayoutBinding = NewsItemLayoutBinding.bind(view);
        mContext = context;
    }

    public void bind(final Doc doc){
        mNewsItemLayoutBinding.tvHeadline.setText(doc.getHeadline().getMain());
        mNewsItemLayoutBinding.tvNewsSynopsis.setText(doc.getSnippet());
        String newsDesk = doc.getNewsDesk();
        Log.d(TAG,"news desk-->" +newsDesk);
        if(newsDesk==null || newsDesk.equalsIgnoreCase("none")){
            mNewsItemLayoutBinding.tvNewsDesk.setVisibility(View.GONE);
        }else{
            mNewsItemLayoutBinding.tvNewsDesk.setText(doc.getNewsDesk());
        }


        String baseURL = mContext.getString(R.string.image_base_url);
        String imageURL = baseURL + doc.getThumbnailURL();

        Log.d(TAG,"image url-->" +imageURL);

        Glide.with(mContext).load(imageURL).into(mNewsItemLayoutBinding.ivNewsThumbnail);

    }
}
