package com.example.medicalalert;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper{

    //Constants for db name and version
    private static final String DATABASE_NAME = "Medical.db";
    private static final int DATABASE_VERSION = 1;

    //Constants for identifying table and columns
    public static final String TABLE_MEDICS = "medics";
    public static final String MEDICINE_ID = "_id";
    public static final String MEDICINE_TIME = "medtime";
    public static final String MEDICINE_DESC = "meddesc";
    public static final String MEDICINE_START = "medsrt";
    public static final String MEDICINE_END = "medend";
    public static final String MEDICINE_ADDED = "medtstp";
    public static final String MEDICINE_IMG = "medimg";
    public static final String[] ALL_COLUMNS =
            {MEDICINE_ID, MEDICINE_DESC, MEDICINE_START, MEDICINE_END, MEDICINE_TIME, MEDICINE_ADDED,  MEDICINE_IMG};

    //SQL to create table
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_MEDICS + " (" +
                    MEDICINE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    MEDICINE_DESC + " TEXT default NULL, " +
                    MEDICINE_START + " TEXT default NULL, " +
                    MEDICINE_END + " TEXT default NULL, " +
                    MEDICINE_TIME + " TEXT default NULL, " +
                    MEDICINE_ADDED + " TEXT default CURRENT_TIMESTAMP, "+
                    MEDICINE_IMG + " TEXT default NULL" +
                    ")";

    public DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDICS);
        onCreate(db);
    }
}
