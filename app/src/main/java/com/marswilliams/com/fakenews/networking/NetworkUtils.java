package com.marswilliams.com.fakenews.networking;

import android.content.Context;
import android.util.Log;

import com.marswilliams.com.fakenews.R;
import com.marswilliams.com.fakenews.models.Settings;
import com.marswilliams.com.fakenews.models.Stories;
import com.marswilliams.com.fakenews.utils.SharedPreferenceUtils;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by mars_williams on 9/22/17.
 */

public class NetworkUtils {

    private String mBaseURL;
    private Retrofit mRetrofit;
    private final static String TAG = "NetworkUtils";
    private final String API_KEY= "6973729bd76c46819a940bb6b55c6b0d";
    private NYTimesAPI mNYTimesAPI;
    private final String filterLines = "web_url,headline,multimedia,news_desk,snippet";
    private Context mContext;
    public NetworkUtils(Context context){
        mBaseURL = "https://api.nytimes.com/svc/search/v2/";
        mRetrofit = new Retrofit.Builder()
                .baseUrl(mBaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mNYTimesAPI = mRetrofit.create(NYTimesAPI.class);
        mContext = context;
    }

    public interface NetworkUtilResponse{
        void onSuccess(Stories stories);
        void onFailure();
    }


    public void getNewsItems(String queryString,int page, final NetworkUtilResponse networkUtilResponse){

        Settings settings = SharedPreferenceUtils.getAllSettings(mContext);
        Map<String,String> queryParams = new HashMap<>();
        if(queryString!=null) queryParams.put(mContext.getString(R.string.key_query_string),queryString);
        queryParams.put(mContext.getString(R.string.key_sort_order),settings.getSortOrder());
        if(settings.getNewsDesks()!=null) queryParams.put(mContext.getString(R.string.key_filter_query),settings.getNewsDesks());
        queryParams.put(mContext.getString(R.string.key_api_key),API_KEY);
        queryParams.put(mContext.getString(R.string.key_page_number),String.valueOf(page));
        queryParams.put(mContext.getString(R.string.key_begin_date),settings.getBeginDateforAPI());
        queryParams.put(mContext.getString(R.string.key_filter_lines),filterLines);
        Call<Stories> call = mNYTimesAPI.getResults(queryParams);
        call.enqueue(new Callback<Stories>() {
            @Override
            public void onResponse(Call<Stories> call, Response<Stories> response) {
                int statusCode = response.code();
                Log.d(TAG, "Status code== " + statusCode);
                if(statusCode==200) {
                    Stories stories = response.body();
                    Log.d(TAG, "Total Stories== " + stories.getResponse().getMeta().getHits());
                    networkUtilResponse.onSuccess(stories);
                }else{
                    networkUtilResponse.onFailure();
                }
            }

            @Override
            public void onFailure(Call<Stories> call, Throwable t) {
                // Log error here since request failed
                Log.d(TAG,t.getMessage());
                t.printStackTrace();
            }
        });
    }
}
