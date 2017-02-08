package com.tn.demineurmadjoh.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tn.demineurmadjoh.BuildConfig;
import com.tn.demineurmadjoh.DmApplication;
import com.tn.demineurmadjoh.model.DmGame;
import com.tn.demineurmadjoh.model.DmTile;

//le helper pour la base de donnée (SQLite)
public class DmDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "demineur_madjoh";
    private static final int DATABASE_VERSION = 3;
    private static DmDatabaseHelper mInstance = null;
    private Context mContext;

    public DmDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setContext(context);
    }

    public static DmDatabaseHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DmDatabaseHelper(context);
        }
        return mInstance;
    }

    //pour créer la base de donnée
    @Override
    public void onCreate(SQLiteDatabase db) {
        DmGame.createTable(db);
        DmTile.createTable(db);
    }

    //Mise à jour la base de donnée
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (BuildConfig.DEBUG)
            android.util.Log.d(DmApplication.LOG_TAG, "Upgrading database, which will destroy all old data");
        DmGame.dropTable(db);
        DmTile.dropTable(db);
        onCreate(db);
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        mContext = context;
    }
}
