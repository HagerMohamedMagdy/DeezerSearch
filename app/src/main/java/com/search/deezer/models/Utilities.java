package com.search.deezer.models;

import android.content.SharedPreferences;

import com.search.deezer.models.data.DeezerApplication;

import static android.content.Context.MODE_PRIVATE;
import static com.search.deezer.models.Constants.MY_PREFS_NAME;

/**
 * Created by Hager.Magdy on 8/21/2017.
 */

public class Utilities {
    /**
     * Function to convert milliseconds time to
     * Timer Format
     * Hours:Minutes:Seconds
     * */
    public String millisecondsToTimer(long milliseconds){
        String finalTimerString = "";
        String secondsString = "";
        //Convert total duration into time
        int hours = (int)(milliseconds/(1000*60*60));
        int minutes = (int)(milliseconds % (1000*60*60))/(1000*60);
        int seconds = (int) (milliseconds % (1000*60*60) % (1000*60)/1000);
        // Add hours if there
        if (hours > 0){
            finalTimerString = hours + ":";
        }
        //Prepending 0 toseconds if its one digit
        if(seconds<10){
            secondsString = "0"+seconds;
        }
        else{
            secondsString = ""+seconds;
        }
        finalTimerString = finalTimerString + minutes + ":" + secondsString;
        return finalTimerString;
    }

    /**
     * Function to get Progress percentage
     * @param currentDuration
     * @param totalDuration
     * */
    public int getProgressPercentage(long currentDuration,long totalDuration){
        Double percentage = (double)0;
        long currentSeconds = (int) (currentDuration/1000);
        long totalSeconds = (int) (totalDuration/1000);
        //calculating percentage
        percentage = ((((double)currentSeconds)/totalSeconds)*100);
        return percentage.intValue();
    }
    /**
     * Function to change progress to timer
     * @param progress -
     * @param totalDuration
     * returns current duration in milliseconds
     * */
    public int progressToTimer(int progress, int totalDuration){
        int currentDuration = 0;
        totalDuration = (int)(totalDuration/1000);
        currentDuration = (int)((((double)progress)/100)*totalDuration);
        return currentDuration*1000;

    }
    /**
     * Function to save data in shared prefrence
     * @param key -
     * @param value
     * returns current duration in milliseconds
     * */
    public static  void saveData(String key,String value) {
        SharedPreferences.Editor editor = DeezerApplication.getAppContext().getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }

public static String getData(String key){
    SharedPreferences prefs = DeezerApplication.getAppContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        String value = prefs.getString(key, null);
return  value;


}
}
