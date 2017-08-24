package com.search.deezer.models.service.retrofit;

import android.content.Context;
import android.util.Log;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.search.deezer.models.data.ModelParser;
import com.search.deezer.utils.Utility;
import com.search.deezer.views.IMainActivityView;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Hager.Magdy on 8/17/2017.
 */

public class RertofitServiceManger {
    //in this class we create retrofit request without asynctask as retrofit support both syncronus(need asynctask) and
    // asyncronus call(don't need asyntask), and we will call asynchrouns request so we don't need aynstasck
    private String uri;
    private String methodType;
    private int requestId;
    private Map<String, String> parameters;
    private Context context;
    private IMainActivityView MainView;
    private Retrofit retrofit;
    private Call<JsonObject> call;
    //For Normal Post Requests /OR GET Request
    public RertofitServiceManger(int requestId, String uri,
                                 String methodType, IMainActivityView MainView,
                                 Map<String, String> parameters,
                                 Context context) {
        this.MainView = MainView;
        this.uri = uri;
        this.methodType = methodType;
        this.requestId = requestId;
        this.parameters = parameters;

        this.context = context;
    }
    public void makeRequest() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
      //  httpClient.addInterceptor(new RetrofitInterceptor(mHeader));//if we need to add header
        OkHttpClient client = httpClient.readTimeout(90000, TimeUnit.MILLISECONDS).connectTimeout(90000, TimeUnit.MILLISECONDS).build();


        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(ServerConfig.SERVER_URl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        RetrofitServiceInterface mRetrofitService = retrofit.create(RetrofitServiceInterface.class);
        if (methodType.equalsIgnoreCase(ServerConfig.METHOD_POST)) {
            //implement post behavior

        }else if(methodType.equalsIgnoreCase(ServerConfig.METHOD_GET)){
            //this methode send header parmeter only


        }else if(methodType.equalsIgnoreCase(ServerConfig.METHOD_GET_Q)){
            //this methode takes query params only
            call = mRetrofitService.getMoreTracks(uri,parameters);
        }
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {


                if(response.isSuccessful()){

                    if (Utility.isProgressDialogShowing()) {
                        Utility.removeProgressDialog();


                    }
                    MainView.UpdateScrollData( ModelParser.parseJasonTrack(uri,response));
                    Log.e("MyResponse Body", response.body().toString() + "," + response.code() + "");
                }else {
                    if (Utility.isProgressDialogShowing()) {
                        Utility.removeProgressDialog();}
                    MainView.notifyError();


                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                MainView.notifyError();
            }
        });
    }

}
