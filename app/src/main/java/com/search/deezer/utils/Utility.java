package com.search.deezer.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import com.search.deezer.R;
import com.search.deezer.models.data.DeezerApplication;

/**
 * Created by Hager.Magdy on 8/16/2017.
 */

public class Utility {

    private static ProgressDialog mProgressDialog;


    public static boolean isProgressDialogShowing() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            return true;
        else
            return false;
    }

    public static void showProgressDialog(Context context,
                                          String msg) {
        if (mProgressDialog == null || !mProgressDialog.isShowing()) {
            mProgressDialog = ProgressDialog.show(context, "", msg);
            mProgressDialog.setCancelable(false);

        }
    }

    public static void removeProgressDialog() {


        try {
            if (mProgressDialog != null) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;

                }

            }
        } catch (IllegalArgumentException ie) {
            ie.printStackTrace();

        } catch (RuntimeException re) {
            re.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
public static boolean isEmptyString(String text){
    if (text != null && !text.isEmpty() && !text.equals("null"))
    return  false;
    else
        return true;

}
    // Showing the status in Snackbar
    public static void showSnack(boolean isConnected, View view) {
        String message;
        int color;
        if (isConnected) {
            message = DeezerApplication.getAppContext().getResources().getString(R.string.internet_connect);
            color = Color.WHITE;
        } else {
            message = DeezerApplication.getAppContext().getResources().getString(R.string.internet_error) ;
            color = Color.RED;
        }

        Snackbar snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }
}
