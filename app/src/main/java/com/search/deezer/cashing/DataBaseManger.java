package com.search.deezer.cashing;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.deezer.sdk.model.Track;
import com.search.deezer.models.data.DeezerApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.data;
import static android.R.attr.type;

/**
 * Created by Hager.Magdy on 8/25/2017.
 */

public class DataBaseManger {
    // private static final String DB_PATH =  "data/data/com.search.deezer/databases/deezer_db_cash/";
    private static final String DB_PATH = "/data/data/com.search.deezer/databases/";
    private static final String DB_NAME = "Deezer_db_cash";
    private static final int DATABASE_VERSION = 1;
    private Context context;
    private static SQLiteDatabase db;

    public DataBaseManger(Context context) {
        this.context = context;
        DeezerSQLiteOpenHelper openHelper = new DeezerSQLiteOpenHelper(this.context);
        try {
            openHelper.createDataBase();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.db = openHelper.openDataBase();
    }


    public void insertTrack(String Query, String mTrackObJect) {
        ContentValues cv = new ContentValues();
        cv.put("Query", Query);
        cv.put("JsonObject", mTrackObJect);
        this.db.insert("Track", null, cv);
        Log.e("success insert db", "q=" + Query + "t=" + mTrackObJect);

    }

    public ArrayList<JSONObject> getCashedHistory(String mquery) {
        Cursor cursor = null;
        String query = "SELECT " + "JsonObject" + " FROM " + "Track" + " WHERE "
                + "Query" + "='" + mquery + "'";


        cursor = db.rawQuery(query, null);
        ArrayList<JSONObject> mJsonTrack = new ArrayList<JSONObject>();
        JSONObject mJasonObject = new JSONObject();
        if (cursor.moveToFirst()) {
            do {
                //save Jason Object

                String json = cursor.getString(0);
                try {
                    mJasonObject = new JSONObject(json);
                    mJsonTrack.add(mJasonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } while (cursor.moveToNext());
        }
        Log.e("cashed Track size", mJsonTrack.size() + "dd");
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return mJsonTrack;
    }


    private static class DeezerSQLiteOpenHelper extends SQLiteOpenHelper {
        Context myContext;
        SQLiteDatabase myDataBase;

        DeezerSQLiteOpenHelper(Context context) {

            super(context, DB_NAME, null, DATABASE_VERSION);
            myContext = context;

        }

        @Override
        public synchronized void close() {

            if (myDataBase != null)
                myDataBase.close();

            super.close();

        }

        @Override
        public void onCreate(SQLiteDatabase db) {

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (newVersion > oldVersion)
                try {
                    copyDataBase();
                } catch (IOException e) {
                    Log.e("UPgradeEXp", e.getMessage() + "c");
                    e.printStackTrace();
                }
        }

        public void createDataBase() throws IOException {

            boolean dbExist = checkDataBase();

            if (dbExist) {
                // do nothing - database already exist
                Log.e("DATABASE,Exit", DeezerApplication.getAppContext().getDatabasePath(DB_NAME).getPath() + "Found");
            } else {
                Log.e("Db", "Not EXIT");
                // By calling this method and empty database will be created
                // into
                // the default system path
                // of your application so we are gonna be able to overwrite that
                // database with our database.
                this.getReadableDatabase();

                try {

                    copyDataBase();

                } catch (IOException e) {

                    throw new RuntimeException(e);

                }
            }

        }


        public SQLiteDatabase openDataBase() throws SQLException {

            // Open the database
            String myPath = DB_PATH + DB_NAME;
            myDataBase = SQLiteDatabase.openDatabase(myPath, null,
                    SQLiteDatabase.OPEN_READWRITE);
            return myDataBase;
        }

        /**
         * Check if the database already exist to avoid re-copying the file each
         * time you open the application.
         *
         * @return true if it exists, false if it doesn't
         */

        private boolean checkDataBase() {
            Log.e("checkDB", "called");

            File databasePath = DeezerApplication.getAppContext().getDatabasePath(DB_NAME);
            return databasePath.exists();
/*
           SQLiteDatabase checkDB = null;

            try {
                String myPath = DB_PATH + DB_NAME;
                checkDB = SQLiteDatabase.openDatabase(myPath, null,
                        SQLiteDatabase.OPEN_READWRITE);

            } catch (SQLiteException e) {
Log.e("EXP","Found"+e.getMessage() +"Found");
                e.printStackTrace();
            }

            if (checkDB != null) {

                checkDB.close();

            }

            return checkDB != null ? true : false;
*/
        }

        /**
         * Copies your database from your local assets-folder to the just
         * created empty database in the system folder, from where it can be
         * accessed and handled. This is done by transfering bytestream.
         */
        private void copyDataBase() throws IOException {
            Log.e("DATABASE,copy", DeezerApplication.getAppContext().getDatabasePath(DB_NAME).getAbsolutePath() + "Found");


            // Open your local db as the input stream
            InputStream myInput = myContext.getAssets().open(DB_NAME);

            // Path to the just created empty db
            String outFileName = DB_PATH + DB_NAME;

            Context context = DeezerApplication.getAppContext();//
            context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);//
            // Open the empty db as the output stream
            OutputStream myOutput = new FileOutputStream(outFileName);

            // transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }

            // Close the streams
            myOutput.flush();
            myOutput.close();
            myInput.close();

        }
    }
}
