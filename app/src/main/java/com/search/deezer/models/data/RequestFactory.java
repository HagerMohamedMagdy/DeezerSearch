package com.search.deezer.models.data;

import android.util.Log;

import com.deezer.sdk.network.connect.DeezerConnect;
import com.deezer.sdk.network.request.DeezerRequest;
import com.deezer.sdk.network.request.DeezerRequestFactory;
import com.deezer.sdk.network.request.event.JsonRequestListener;

/**
 * Created by Hager.Magdy on 8/19/2017.
 */

public class RequestFactory {
    protected DeezerConnect mDeezerConnect;

    public RequestFactory(DeezerConnect deezerConnect) {
        mDeezerConnect = deezerConnect;
    }
    /**
     * Search the Deezer Api for tracks matches a given queryString
     * @param queryString Query string of the tracks to search for
     * @param callback RequestCallback to forwarding the request
     */
    public void newTrackRequest(String queryString, final RequestCallback callback){
        DeezerRequest request = DeezerRequestFactory.requestSearchTracks(queryString);
      Log.e ("Service Path ",request.getDeezerServicePath()+"para"+" "+request.getParams());
        makeRequest(request, callback);
    }

    private void makeRequest(DeezerRequest request, final RequestCallback callback) {
        mDeezerConnect.requestAsync(request, new JsonRequestListener() {
            @Override
            public void onResult(Object result, Object o1) {
                callback.onRequestSuccess(result);
            }

            @Override
            public void onUnparsedResult(String s, Object o) {

            }

            @Override
            public void onException(Exception e, Object o) {
                callback.onRequestFailed(e);
            }
        });
    }
    /**
     * Interface for forwarding Deezer API request result
     */
    public interface RequestCallback{
        /**
         * Forwards Deezer api request result if complete with success
         * @param result The request result
         */
        void onRequestSuccess(Object result);

        /**
         * Forwards Deezer api request exception if complete with failure
         * @param exception
         */
        void onRequestFailed(Exception exception);
    }

}
