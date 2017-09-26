package com.marswilliams.com.fakenews.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.marswilliams.com.fakenews.databinding.NoThumbnailNewsItemBinding;
import com.marswilliams.com.fakenews.models.Doc;

import static com.marswilliams.com.fakenews.fragments.DatePickerFragment.TAG;

/**
 * Created by mars_williams on 9/22/17.
 */

public class NoThumbnailViewHolder extends RecyclerView.ViewHolder{
    final NoThumbnailNewsItemBinding mNoThumbnailNewsItemBinding;
    public NoThumbnailViewHolder(View view){
        super(view);
        mNoThumbnailNewsItemBinding = NoThumbnailNewsItemBinding.bind(view);
    }
    public void bind(final Doc doc){
        mNoThumbnailNewsItemBinding.tvHeadline.setText(doc.getHeadline().getMain());
        mNoThumbnailNewsItemBinding.tvNewsSynopsis.setText(doc.getSnippet());
        String newsDesk = doc.getNewsDesk();
        Log.d(TAG,"news desk-->" +newsDesk);
        if(newsDesk==null || newsDesk.equalsIgnoreCase("none")){
            mNoThumbnailNewsItemBinding.tvNewsDesk.setVisibility(View.GONE);
        }else{
            mNoThumbnailNewsItemBinding.tvNewsDesk.setText(doc.getNewsDesk());
        }
    }
}
