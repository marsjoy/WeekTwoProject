package com.marswilliams.com.fakenews.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.marswilliams.com.fakenews.R;
import com.marswilliams.com.fakenews.databinding.ActivityNewsGridBinding;
import com.marswilliams.com.fakenews.fragments.NewsGridFragment;
import com.marswilliams.com.fakenews.fragments.SettingsDialogFragment;

public class NewsGridActivity extends AppCompatActivity {

    private ActivityNewsGridBinding binding;
    private final String TAG = "NewsGridActivity";
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_news_grid);
        setSupportActionBar(binding.myToolbar);
        binding.myToolbar.setTitleTextColor(getResources().getColor(R.color.white));

        mFragmentManager = getSupportFragmentManager();
        Fragment fragment = mFragmentManager.findFragmentById(R.id.fl_fragment_container);
        if(fragment==null){
            fragment = NewsGridFragment.newInstance(null);
            mFragmentManager.beginTransaction()
                    .add(R.id.fl_fragment_container,fragment,NewsGridFragment.FRAGMENT_TAG)
                    .commit();

        }else{
            mFragmentManager.beginTransaction()
                    .replace(R.id.fl_fragment_container,fragment,NewsGridFragment.FRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);

        //Set up search view.
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        MenuItem searchItem = menu.findItem(R.id.search);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id){
            case R.id.search: return true;
            case R.id.settings:
                SettingsDialogFragment settingsDialogFragment = new SettingsDialogFragment();
                settingsDialogFragment.show(mFragmentManager,SettingsDialogFragment.TAG);
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        //Check if the received intent is for Search.
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.d(TAG, query);

            mFragmentManager.beginTransaction().replace(R.id.fl_fragment_container,
                    NewsGridFragment.newInstance(query),NewsGridFragment.FRAGMENT_TAG).commit();

        }
    }
}
