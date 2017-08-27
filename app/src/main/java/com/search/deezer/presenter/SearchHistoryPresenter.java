package com.search.deezer.presenter;

import com.deezer.sdk.model.Track;

import java.util.ArrayList;

/**
 * Created by Hager.Magdy on 8/24/2017.
 */

public interface SearchHistoryPresenter {
   void getSearchHistory(String query);
   void getCashedSearchHistory(String query);
}
