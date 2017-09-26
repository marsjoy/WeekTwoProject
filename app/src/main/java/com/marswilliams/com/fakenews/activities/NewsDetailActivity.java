package com.marswilliams.com.fakenews.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.marswilliams.com.fakenews.R;
import com.marswilliams.com.fakenews.fragments.NewsDetailFragment;
import com.marswilliams.com.fakenews.models.Doc;

/*
 * Activity to host the Detailed News web view fragment.
 */
public class NewsDetailActivity extends AppCompatActivity {

    public final String TAG = "NewsDetailActivity";
    public static final String DOC_KEY = "news_doc";
    Fragment newsDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_article);

        Doc doc = getIntent().getParcelableExtra(DOC_KEY);
        newsDetailFragment = NewsDetailFragment
                .newInstance(doc.getWebUrl(),doc.getHeadline().getMain());
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().add(R.id.news_detail_container,
                newsDetailFragment,NewsDetailFragment.TAG).commit();
    }

    // Call fragment method to handle back button press.
    @Override
    public void onBackPressed() {
        ((NewsDetailFragment)newsDetailFragment).backButtonPressed();
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
