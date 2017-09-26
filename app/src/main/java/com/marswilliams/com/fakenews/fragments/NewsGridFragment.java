package com.marswilliams.com.fakenews.fragments;


import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marswilliams.com.fakenews.R;
import com.marswilliams.com.fakenews.activities.NewsDetailActivity;
import com.marswilliams.com.fakenews.adapters.NewsItemRecyclerViewAdapter;
import com.marswilliams.com.fakenews.databinding.FragmentNewsGridBinding;
import com.marswilliams.com.fakenews.models.Doc;
import com.marswilliams.com.fakenews.models.Stories;
import com.marswilliams.com.fakenews.networking.NetworkUtils;
import com.marswilliams.com.fakenews.recievers.InternetCheckReceiver;
import com.marswilliams.com.fakenews.utils.EndlessRecyclerViewScrollListener;
import com.marswilliams.com.fakenews.utils.ItemClickSupport;
import com.marswilliams.com.fakenews.utils.SharedPreferenceUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewsGridFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsGridFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_QUERY = "param1";
    public static final String FRAGMENT_TAG = "NewsGridFragment";
    private FragmentNewsGridBinding mFragmentNewsGridBinding;
    NewsItemRecyclerViewAdapter mNewsItemRecyclerViewAdapter;
    private List<Doc> mDocList;
    private int mTotalPages;
    private InternetCheckReceiver mBroadcastReceiver;
    private EndlessRecyclerViewScrollListener mEndlessRecyclerViewScrollListener;
    private int retryRemaining = 5;
    // TODO: Rename and change types of parameters
    private String mQuery;


    public NewsGridFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param queryString Parameter 1.
     * @return A new instance of fragment NewsGridFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewsGridFragment newInstance(String queryString) {
        NewsGridFragment fragment = new NewsGridFragment();
        Bundle args = new Bundle();
        args.putString(ARG_QUERY, queryString);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mQuery = getArguments().getString(ARG_QUERY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentNewsGridBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_news_grid,container,false);
        mDocList = new ArrayList<>();
        mNewsItemRecyclerViewAdapter= new NewsItemRecyclerViewAdapter(getActivity(),mDocList);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        mFragmentNewsGridBinding.rvNewsGridItems.setLayoutManager(staggeredGridLayoutManager);
        mFragmentNewsGridBinding.rvNewsGridItems.setAdapter(mNewsItemRecyclerViewAdapter);
        mEndlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if(page<= mTotalPages && mBroadcastReceiver.isInternetAvailable()) {
                    mFragmentNewsGridBinding.swipeContainer.setRefreshing(true);
                    fetchDataFromAPI(page);
                }else{
                    Snackbar.make(mFragmentNewsGridBinding.getRoot(),getString(R.string.snackbar_text_internet_lost),
                            Snackbar.LENGTH_LONG).show();
                }
            }
        };
        mFragmentNewsGridBinding.rvNewsGridItems.addOnScrollListener(mEndlessRecyclerViewScrollListener);
        ItemClickSupport.addTo(mFragmentNewsGridBinding.rvNewsGridItems).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        // do it
                        List<Doc> data = mNewsItemRecyclerViewAdapter.getData();
                        final Doc doc = data.get(position);

                        if (SharedPreferenceUtils.openInChrome(getActivity())) {
                            openChromeTab(doc);
                        }else{
                            Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                            intent.putExtra(NewsDetailActivity.DOC_KEY,doc);
                            startActivity(intent);
                        }
                    }
                }
        );
        mFragmentNewsGridBinding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(mBroadcastReceiver.isInternetAvailable()) {
                    mNewsItemRecyclerViewAdapter.clear();
                    mEndlessRecyclerViewScrollListener.resetState();
                    fetchDataFromAPI(0);
                }else{
                    mFragmentNewsGridBinding.swipeContainer.setRefreshing(false);
                    Snackbar.make(mFragmentNewsGridBinding.getRoot(),getString(R.string.snackbar_text_internet_lost),
                            Snackbar.LENGTH_LONG).show();
                }
            }
        });
        mFragmentNewsGridBinding.swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        fetchDataFromAPI(0);
        return mFragmentNewsGridBinding.getRoot();
    }

    public void fetchDataFromAPI(final int page){

        Log.d(FRAGMENT_TAG,"Loading page--> "+page);
        NetworkUtils networkUtils = new NetworkUtils(getActivity());
        networkUtils.getNewsItems(mQuery,page, new NetworkUtils.NetworkUtilResponse() {
            @Override
            public void onSuccess(Stories stories) {
                Log.d(FRAGMENT_TAG,"Got Retrofit Response");
                Log.d(FRAGMENT_TAG,"Number of stories= "+stories.getResponse().getMeta().getHits());
                mTotalPages = stories.getResponse().getMeta().getHits() / 10;
                List<Doc> newData = stories.getResponse().getDocs();
                mNewsItemRecyclerViewAdapter.addData(newData);
                mFragmentNewsGridBinding.swipeContainer.setRefreshing(false);
                retryRemaining = 5;
            }

            @Override
            public void onFailure() {
                Log.d(FRAGMENT_TAG,"Retrying request for page--> "+page);
                Log.d(FRAGMENT_TAG,"Retrying count--> "+retryRemaining);
                mFragmentNewsGridBinding.swipeContainer.setRefreshing(false);
                if(retryRemaining>0){
                    retryRemaining--;
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fetchDataFromAPI(page);

                        }
                    },5000);

                }
            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        mBroadcastReceiver = new InternetCheckReceiver(mFragmentNewsGridBinding.getRoot());
        getActivity().registerReceiver(mBroadcastReceiver,intentFilter);
    }

    @Override
    public void onStop() {
        getActivity().unregisterReceiver(mBroadcastReceiver);
        super.onStop();
    }

    private void openChromeTab(Doc doc){
        String url = doc.getWebUrl();
        // Use a CustomTabsIntent.Builder to configure CustomTabsIntent.
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_share);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, url);

        int requestCode = 100;

        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(),
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setActionButton(bitmap, "Share Link", pendingIntent, true);
        // set toolbar color and/or setting custom actions before invoking build()
        builder.setToolbarColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
        // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
        CustomTabsIntent customTabsIntent = builder.build();

        customTabsIntent.launchUrl(getActivity(), Uri.parse(url));
    }
}
