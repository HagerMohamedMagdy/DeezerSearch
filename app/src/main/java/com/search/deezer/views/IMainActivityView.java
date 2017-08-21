package com.search.deezer.views;

/**
 * Created by Hager.Magdy on 8/19/2017.
 */

public interface IMainActivityView {
    void showEmptyView();
    void hideEmptyView();
    void notifyDataLoaded();
    void notifyError();
}
