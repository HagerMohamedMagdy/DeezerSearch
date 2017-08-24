package com.search.deezer.utils;

import android.app.ProgressDialog;
import android.content.Context;

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

}
