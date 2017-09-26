package com.marswilliams.com.fakenews.networking;

import com.marswilliams.com.fakenews.models.Stories;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by mars_williams on 9/22/17.
 */

public interface NYTimesAPI {

    @GET("articlesearch.json")
    Call<Stories> getResults(@QueryMap Map<String, String> queryParams);

    @GET("articlesearch.json")
    Call<Stories> getResultsFromPage(@Query("q") String query,@Query("api-key") String apiKey,
                             @Query("fq") String filterQuery,@Query("fl") String filterLines,
                                     @Query("page") int page,@Query("sort") String sortOrder,
                                     @Query("begin_date") String beginDate);

}
