package com.search.deezer.models.service.retrofit;

import com.google.gson.JsonObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

import static com.deezer.sdk.network.request.DeezerRequest.GET;

/**
 * Created by Hager.Magdy on 8/23/2017.
 */

public interface RetrofitServiceInterface {

    @GET
    Call<JsonObject> getTracks(@Url String url, @QueryMap Map<String, String> options
    );

}
