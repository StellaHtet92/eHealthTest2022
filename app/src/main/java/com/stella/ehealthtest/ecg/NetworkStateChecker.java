package com.stella.ehealthtest.ecg;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import com.stella.ehealthtest.*;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Belal on 1/27/2017.
 */
public class NetworkStateChecker extends BroadcastReceiver {
    //context and database helper object
    private Context context;
    private DatabaseHelper db;
    private final String TAG="NetworkStateChecker";

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        db = new DatabaseHelper(context);

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        Log.e(TAG,"activeNetwork=>"+activeNetwork.toString());

        //if there is a network
        if (activeNetwork != null) {
            //if connected to wifi or mobile data plan
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {

                //getting all the unsynced names
                Cursor cursor = db.getUnsyncedECGData();
                Cursor cursorVital=db.getUnsyncedVitalData();
                Log.e(TAG,"Cursor for db.getUnsyncedECG=>"+cursor.toString());
                Log.e(TAG,"Cursor for db.getUnsyncedVitalData=>"+cursorVital.toString());
                if (cursor.moveToFirst()) {
                    do {
                        Log.e(TAG,"saving ECG Data");
                        //calling the method to save the unsynced name to MySQL
                        saveECG(
                                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ECG_ID)),
                                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_User_ID)),
                                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ECG)),
                                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_Created_At))
                        );
                    } while (cursor.moveToNext());
                }
                //for vital
                if (cursorVital.moveToFirst()) {
                    do {
                        Log.e(TAG,"saving Vital Data");
                        //calling the method to save the unsynced name to MySQL
                        saveVital(
                                cursorVital.getInt(cursorVital.getColumnIndex(DatabaseHelper.COLUMN_Vital_ID)),
                                cursorVital.getInt(cursorVital.getColumnIndex(DatabaseHelper.COLUMN_Vital_User_ID)),
                                cursorVital.getInt(cursorVital.getColumnIndex(DatabaseHelper.COLUMN_Vital_EWS)),
                                cursorVital.getDouble(cursorVital.getColumnIndex(DatabaseHelper.COLUMN_Vital_Temp)),
                                cursorVital.getInt(cursorVital.getColumnIndex(DatabaseHelper.COLUMN_Vital_UpperBP)),
                                cursorVital.getInt(cursorVital.getColumnIndex(DatabaseHelper.COLUMN_Vital_LowerBP)),
                                cursorVital.getInt(cursorVital.getColumnIndex(DatabaseHelper.COLUMN_Vital_SPO2)),
                                cursorVital.getInt(cursorVital.getColumnIndex(DatabaseHelper.COLUMN_Vital_pulse)),
                                cursorVital.getInt(cursorVital.getColumnIndex(DatabaseHelper.COLUMN_Vital_Blood_Sugar)),
                                cursorVital.getString(cursorVital.getColumnIndex(DatabaseHelper.COLUMN_Vital_mealStatus)),
                                cursorVital.getInt(cursorVital.getColumnIndex(DatabaseHelper.COLUMN_Vital_HDL)),
                                cursorVital.getInt(cursorVital.getColumnIndex(DatabaseHelper.COLUMN_Vital_LDL)),
                                cursorVital.getInt(cursorVital.getColumnIndex(DatabaseHelper.COLUMN_Vital_Heart_Rate)),
                                cursorVital.getString(cursorVital.getColumnIndex(DatabaseHelper.COLUMN_Vital_Level_Consciousness)),
                                cursorVital.getString(cursorVital.getColumnIndex(DatabaseHelper.COLUMN_Vital_LMD)),
                                cursorVital.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_Created_At))
                        );
                    } while (cursorVital.moveToNext());
                }
            }
        }
    }

    /*
     * method taking two arguments
     * name that is to be saved and id of the name from SQLite
     * if the name is successfully sent
     * we will update the status as synced in SQLite
     * */
    private void saveECG(final int ecg_id, final int user_id, final String ECG, final String createdAt) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, add_ecg.URL_SAVE_ECG,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.i("tagconvertstr", "["+response+"]");
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                //updating the status in sqlite
                                db.updateECGStatus(ecg_id, add_ecg.ECG_SYNCED_WITH_SERVER);

                                //sending the broadcast to refresh the list
                                context.sendBroadcast(new Intent(add_ecg.DATA_SAVED_BROADCAST));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("ecg_id", ""+ecg_id);
                params.put("user_id", ""+user_id);
              //  params.put("vital_id", ""+vital_id);
                params.put("ecg", ECG);
                params.put("createdAt",createdAt);
                return params;
            }
        };

        MySingleton.getmInstance(context).addToRequestQueue(stringRequest);
    }
    //for vital
    private void saveVital(final int vital_id,final int user_id,final int EWS,final double temperature,final int upperBP,final int lowerBP,final int SPO2,final int pulse,final int blood_sugar_level,final String mealStatus, final int HDL,
                           final int LDL,final int heart_rate,final String level_of_consciousness,final String last_menstruation_date,final String createdAt) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, add_vitals.URL_SAVE_Vital,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.i("tagconvertstr", "["+response+"]");
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                //updating the status in sqlite
                                db.updateVitalStatus(vital_id, add_vitals.Vital_SYNCED_WITH_SERVER);

                                //sending the broadcast to refresh the list
                                context.sendBroadcast(new Intent(add_vitals.DATA_SAVED_BROADCAST));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("vital_id", ""+vital_id);
                params.put("user_id", ""+user_id);
                params.put("EWS", ""+EWS);
                params.put("temp",""+ temperature);
                params.put("BP_Sys",""+ upperBP);
                params.put("BP_Dia",""+ lowerBP);
                params.put("spO2",""+ SPO2);
                params.put("pulse",""+ pulse);
                params.put("blood_sugar_level",""+ blood_sugar_level);
                params.put("mealStatus",mealStatus);
                params.put("HDL",""+ HDL);
                params.put("LDL",""+ LDL);
                params.put("heart_rate",""+ heart_rate);
                params.put("level_of_consciousness",level_of_consciousness);
                params.put("last_menstruation_date",last_menstruation_date);
                params.put("collected_datetime",createdAt);
                return params;
            }
        };

        MySingleton.getmInstance(context).addToRequestQueue(stringRequest);
    }
}
