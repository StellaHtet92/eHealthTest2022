package com.stella.ehealthtest.ecg;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Belal on 1/27/2017.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    //Constants for Database name, table name, and column names
    public static final String DB_NAME = "his";
    public static final String TABLE_NAME = "ecgdata";
    public static final String COLUMN_ECG_ID = "ecg_id";
    public static final String COLUMN_User_ID = "user_id";
    public static final String COLUMN_ECG = "ecg";
    public static final String COLUMN_Created_At = "createdAt";
    public static final String COLUMN_STATUS = "status";
    //for vital signs
    public static final String Vital_TABLE_NAME = "vital_records";
    public static final String COLUMN_Vital_ID = "vital_id";
    public static final String COLUMN_Vital_User_ID = "user_id";
    public static final String COLUMN_Vital_EWS = "EWS";
    public static final String COLUMN_Vital_UpperBP = "BP_Sys";
    public static final String COLUMN_Vital_LowerBP = "BP_Dia";
    public static final String COLUMN_Vital_Temp = "temperature";
    public static final String COLUMN_Vital_SPO2 = "spO2";
    public static final String COLUMN_Vital_pulse = "pulse";
    public static final String COLUMN_Vital_Level_Consciousness = "level_of_consciousness";
    public static final String COLUMN_Vital_Blood_Sugar = "blood_sugar_level";
    public static final String COLUMN_Vital_mealStatus="mealStatus";
    public static final String COLUMN_Vital_HDL = "HDL";
    public static final String COLUMN_Vital_LDL = "LDL";
    public static final String COLUMN_Vital_Heart_Rate= "heart_rate";
    public static final String COLUMN_Vital_LMD = "last_menstruation_date";
    public static final String COLUMN_Vital_Created_At = "collected_datetime";
    public static final String COLUMN_Vital_STATUS = "status";

    //database version
    private static final int DB_VERSION = 2;

    //Constructor
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    //creating the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME
                + "(" + COLUMN_ECG_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_User_ID +
                " INTEGER, " + COLUMN_ECG +
                " TEXT, " + COLUMN_Created_At +
                " VARCHAR, " + COLUMN_STATUS +
                " TINYINT);";
        String vitalSignsSQL;
        vitalSignsSQL = "CREATE TABLE " + Vital_TABLE_NAME
                + "(" + COLUMN_Vital_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_Vital_User_ID + " INTEGER, "
                + COLUMN_Vital_EWS +" INTEGER, "
                + COLUMN_Vital_Temp + " DOUBLE, "
                + COLUMN_Vital_UpperBP + " INTEGER, "
                + COLUMN_Vital_LowerBP +" INTEGER, "
                + COLUMN_Vital_SPO2 +" INTEGER, "
                + COLUMN_Vital_pulse +" INTEGER, "
                + COLUMN_Vital_Blood_Sugar + " INTEGER, "
                + COLUMN_Vital_mealStatus + " VARCHAR, "
                + COLUMN_Vital_HDL + " INTEGER, "
                + COLUMN_Vital_LDL + " INTEGER, "
                + COLUMN_Vital_Heart_Rate + " INTEGER, "
                + COLUMN_Vital_Level_Consciousness + " VARCHAR, "

                + COLUMN_Vital_LMD + " VARCHAR, "
                + COLUMN_Vital_Created_At + " VARCHAR, "
                + COLUMN_Vital_STATUS +" TINYINT);";
        db.execSQL(sql);
        db.execSQL(vitalSignsSQL);
    }

    //upgrading the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS ecgdata";
        db.execSQL(sql);
        String vitalSql = "DROP TABLE IF EXISTS vital_records";
        db.execSQL(vitalSql);
        onCreate(db);
    }

    /*
     * This method is taking two arguments
     * first one is the name that is to be saved
     * second one is the status
     * 0 means the name is synced with the server
     * 1 means the name is not synced with the server
     * */
    public boolean addECG(int user_id, String ecg, String createdAt, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_User_ID, user_id);
        contentValues.put(COLUMN_ECG, ecg);
        contentValues.put(COLUMN_Created_At, createdAt);
        contentValues.put(COLUMN_STATUS, status);

        db.insert(TABLE_NAME, null, contentValues);
        db.close();
        Log.e("Database Helper:","inserted into table");
        return true;
    }

    /*
     * This method taking two arguments
     * first one is the id of the name for which
     * we have to update the sync status
     * and the second one is the status that will be changed
     * */
    public boolean updateECGStatus(int id, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_STATUS, status);
        db.update(TABLE_NAME, contentValues, COLUMN_ECG_ID + "=" + id, null);
        db.close();
        Log.e("Database Helper:","updated the table");
        return true;
    }

    /*
     * this method will give us all the name stored in sqlite
     * */
    public Cursor getECGData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_ECG_ID + " ASC;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    /*
     * this method is for getting all the unsynced name
     * so that we can sync it with database
     * */
    public Cursor getUnsyncedECGData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_STATUS + " = 0;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }
    //for vital_signs////////////////////////////////////////////////////////////////////
    public boolean addVitalsRecord(int user_id, int EWS, double temperature,int upperBP, int lowerBP, int SPO2, int pulse, int blood_sugar_level, String mealStatus, int HDL,
                                   int LDL, int heart_rate, String level_of_consciousness, String last_menstruation_date, String createdAt, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_Vital_User_ID, user_id);
        contentValues.put(COLUMN_Vital_EWS, EWS);
        contentValues.put(COLUMN_Vital_Temp, temperature);
        contentValues.put(COLUMN_Vital_UpperBP, upperBP);
        contentValues.put(COLUMN_Vital_LowerBP, lowerBP);
        contentValues.put(COLUMN_Vital_SPO2, SPO2);
        contentValues.put(COLUMN_Vital_pulse, pulse);
        contentValues.put(COLUMN_Vital_Blood_Sugar, blood_sugar_level);
        contentValues.put(COLUMN_Vital_mealStatus,mealStatus);
        contentValues.put(COLUMN_Vital_HDL, HDL);
        contentValues.put(COLUMN_Vital_LDL, LDL);
        contentValues.put(COLUMN_Vital_Heart_Rate, heart_rate);
        contentValues.put(COLUMN_Vital_Level_Consciousness, level_of_consciousness);
        contentValues.put(COLUMN_Vital_LMD, last_menstruation_date);
        contentValues.put(COLUMN_Vital_Created_At, createdAt);
        contentValues.put(COLUMN_Vital_STATUS, status);

        db.insert(Vital_TABLE_NAME, null, contentValues);
        db.close();
        Log.e("Database Helper:","inserted Vital data into table");
        return true;
    }

    /*
     * This method taking two arguments
     * first one is the id of the name for which
     * we have to update the sync status
     * and the second one is the status that will be changed
     * */
    public boolean updateVitalStatus(int id, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_Vital_STATUS, status);
        db.update(Vital_TABLE_NAME, contentValues, COLUMN_Vital_ID + "=" + id, null);
        db.close();
        Log.e("Database Helper:","updated the vital_record table");
        return true;
    }

    /*
     * this method will give us all the name stored in sqlite
     * */
    public Cursor getVitalSignsData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + Vital_TABLE_NAME + " ORDER BY " + COLUMN_Vital_ID + " ASC;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    /*
     * this method is for getting all the unsynced name
     * so that we can sync it with database
     * */
    public Cursor getUnsyncedVitalData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + Vital_TABLE_NAME + " WHERE " + COLUMN_Vital_STATUS + " = 0;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }
}