package com.search.deezer.models.service.retrofit;

/**
 * Created by Hager.Magdy on 8/23/2017.
 */

public interface RetrofitCallBack {


    public void onSuccess(int reqId, Object data);
    public void onFail(int reqId, Object data, Object object);
}
