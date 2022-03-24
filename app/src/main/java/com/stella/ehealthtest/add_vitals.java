package com.stella.ehealthtest;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.stella.ehealthtest.ecg.DatabaseHelper;
import com.stella.ehealthtest.ecg.NetworkStateChecker;
import com.stella.ehealthtest.ecg.ecg;
import com.stella.ehealthtest.ecg.ecgAdapter;
import com.stella.ehealthtest.vitalSigns.vitalSigns;
import com.stella.ehealthtest.vitalSigns.vitalSignsAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class add_vitals extends AppCompatActivity {
    TextView dateTimeTV, BMITV;
    EditText txt_UBloodPressure,txt_LBloodPressure, txtPulse, txt_SPO2,txt_BodyTemperature, txt_BloodSugar, txt_LDL, txt_HDL;
    int upperBP,lowerBP,Pulse, SPO2, bodyTemp,BloodSugar,LDL,HDL;
    RadioButton rdPre_Meal, rdPost_Meal;
    int userID=0;
    String user_name,mealStatus;
    Button btnLMD, btnVitalSave,btnVitalCancel;
    //for ecg chart
    LineChart lineChart;
    LineData lineData;
    LineDataSet lineDataSet;
    List<Entry> entryList = new ArrayList<>();
    //set up for btn last menstrual date
    DatePickerDialog datePickerDialog;
    //for saving vital data
    public static final String URL_SAVE_Vital = "http://192.168.0.101//his/saveVital.php";
    //database helper object
    private DatabaseHelper db;
    private ListView listViewVitalData;
    //List to store all the ecg
    private List<vitalSigns> VitalData;
    //1 means data is synced and 0 means data is not synced
    public static final int Vital_SYNCED_WITH_SERVER = 1;
    public static final int Vital_NOT_SYNCED_WITH_SERVER = 0;
    //a broadcast to know weather the data is synced or not
    public static final String DATA_SAVED_BROADCAST = "net.simplifiedcoding.datasaved";
    //Broadcast receiver to know the sync status
    private BroadcastReceiver broadcastReceiver;
    private com.stella.ehealthtest.vitalSigns.vitalSignsAdapter vitalAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vitals);
        //set up for Back to home screen bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getting passed data
        Intent i=getIntent();
        userID= Integer.parseInt(i.getStringExtra("ID"));
        System.out.print("User_ID:"+userID);
        user_name=i.getStringExtra("Username");
        System.out.print("User Name:"+user_name);
        //setting for synchronizatinon
        //to register the receiver
        registerReceiver(new NetworkStateChecker(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        //initializing views and objects
        db = new DatabaseHelper(this);
        VitalData = new ArrayList<>();
        listViewVitalData = (ListView) findViewById(R.id.listViewVital);
        //registering the broadcast receiver to update sync status
        registerReceiver(broadcastReceiver, new IntentFilter(DATA_SAVED_BROADCAST));

        //set up LMS button
        btnLMD=(Button) findViewById(R.id.btnLMD);
        btnLMD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(add_vitals.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // set day of month , month and year value in the edit text
                        btnLMD.setText(dayOfMonth + "/"
                                + (monthOfYear + 1) + "/" + year);

                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        //btn Save
        btnVitalSave=(Button) findViewById(R.id.btnVitalSave);
        btnVitalSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_vitalSigns(v);
                //Toast.makeText(userInfo_registration.this,"Ok",Toast.LENGTH_SHORT).show();
            }
        });
        //btn Cancel
        btnVitalCancel=(Button) findViewById(R.id.btnVitalCancel);
        btnVitalCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel_vitalSigns(v);
                //Toast.makeText(userInfo_registration.this,"Ok",Toast.LENGTH_SHORT).show();
            }
        });
        //getting user enter data
        dateTimeTV=(TextView) findViewById(R.id.original_DateTime);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        final String createdAt= sdf.format(new java.sql.Date(Calendar.getInstance().getTime().getTime()));
        dateTimeTV.setText(createdAt);
        //show BMI
        BMITV=(TextView) findViewById(R.id.BMI);
        double BMI=getBMI(userID);
        BMITV.setText("Current Body Mass Index: "+BMI);

        txt_UBloodPressure=(EditText)findViewById(R.id.txt_UBloodPressure);
        txt_LBloodPressure=(EditText)findViewById(R.id.txt_LBloodPressure);
        txtPulse=(EditText)findViewById(R.id.txt_Pulse);
        txt_SPO2=(EditText)findViewById(R.id.txt_SPO2);
        txt_BodyTemperature=(EditText)findViewById(R.id.txt_BodyTemperature);
        txt_BloodSugar=(EditText)findViewById(R.id.txt_BloodSugar);
        txt_LDL=(EditText)findViewById(R.id.txt_LDL);
        txt_HDL=(EditText)findViewById(R.id.txt_HDL);

    }
//TODO write BMI data
    private double getBMI(int userID) {

        return 26.0;
    }

    private void cancel_vitalSigns(View v) {
        txt_UBloodPressure.setText("");
        txt_LBloodPressure.setText("");
        txtPulse.setText("");
        txt_SPO2.setText("");
        txt_BodyTemperature.setText("");
        txt_BloodSugar.setText("");
        txt_LDL.setText("");
        txt_HDL.setText("");
        rdPre_Meal.setChecked(true);
        btnLMD.setText("Last Menstruation Period");
    }

    private void add_vitalSigns(View v) {
        //for upper blood pressure
        String uBP=txt_UBloodPressure.getText().toString();
        if(uBP!=null && uBP.isEmpty()==false)
        {
            upperBP = Integer.parseInt(uBP);
        }
        else
        {
            txt_UBloodPressure.setError("");
            return;
        }
        //for lower blood pressure
        String lBP=txt_LBloodPressure.getText().toString();
        if(lBP!=null && lBP.isEmpty()==false)
        {
            lowerBP = Integer.parseInt(lBP);
        }
        else
        {
            txt_LBloodPressure.setError("");
            return;
        }
        //Pulse
        String txtpulse=txtPulse.getText().toString();
        if(txtpulse!=null && txtpulse.isEmpty()==false)
        {
            Pulse = Integer.parseInt(txtpulse);
        }
        else
        {
            txtPulse.setError("");
            return;
        }
        //spo2
        String txtsp02=txt_SPO2.getText().toString();
        if(txtsp02!=null && txtsp02.isEmpty()==false)
        {
            SPO2 = Integer.parseInt(txtsp02);
        }
        else
        {
            txt_SPO2.setError("");
            return;
        }
        //body temperature
        String txtTemp=txt_BodyTemperature.getText().toString();
        if(txtTemp!=null && txtTemp.isEmpty()==false)
        {
            bodyTemp = Integer.parseInt(txtTemp);
        }
        else
        {
            txt_BodyTemperature.setError("");
            return;
        }
        //blood sugar level
        String txtSugar=txt_BloodSugar.getText().toString();
        if(txtSugar!=null && txtSugar.isEmpty()==false)
        {
            BloodSugar = Integer.parseInt(txtSugar);
        }
        else
        {
            txt_BloodSugar.setError("");
            return;
        }
        //premeal/postmeal
        mealStatus=this.onRadioButtonClicked(v);
        //LDL
        String txtLDL=txt_LDL.getText().toString();
        if(txtLDL!=null && txtLDL.isEmpty()==false)
        {
            LDL = Integer.parseInt(txtLDL);
        }
        else
        {
            txt_LDL.setError("");
            return;
        }
        //HDL
        String txtHDL=txt_HDL.getText().toString();
        if(txtHDL!=null && txtHDL.isEmpty()==false)
        {
            HDL = Integer.parseInt(txtHDL);
        }
        else
        {
            txt_HDL.setError("");
            return;
        }
        //get LMS date
        btnLMD=(Button) findViewById(R.id.btnLMD);
        String lmd = ""+btnLMD.getText();
        java.sql.Date dateOfLMD = null;
        if(lmd!=null) {
            Toast.makeText(this, "Date of LMD from Spinner:" + lmd, Toast.LENGTH_LONG).show();
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            try {
                dateOfLMD = new java.sql.Date(format.parse(lmd).getTime());
                System.out.println(dateOfLMD);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //          Toast.makeText(RegisterActivity02.this, dateOfBirth.toString(), Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "Date is null" + lmd, Toast.LENGTH_LONG).show();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        final String createdAt= sdf.format(new java.sql.Date(Calendar.getInstance().getTime().getTime()));
        //insert into database
        //EWS calculation
        int EWS=this.EWSCalculation(bodyTemp, upperBP,SPO2,Pulse,"A");
        saveVitalToServer(userID,EWS,bodyTemp, upperBP,lowerBP,SPO2,Pulse,BloodSugar,mealStatus,HDL,LDL,10,"A",dateOfLMD.toString(),createdAt);

    }

    public void openDatePicker(View view) {
    }
    //premeal and post meal
    public String onRadioButtonClicked(View view) {

        boolean checked = false;
        rdPre_Meal=findViewById(R.id.rdPreMeal);
        rdPost_Meal=findViewById(R.id.rdPostMeal);
        String result;
        if(rdPre_Meal.isChecked())
        {

            result="Pre_Meal";
        }
        else if(rdPost_Meal.isChecked())
        {

            result="rdPost_Meal";
        }
        else
        {
            Toast.makeText(this,"Please select PreMeal/PostMeal status",Toast.LENGTH_LONG).show();
            result="Not Selected";
            finish();
        }
        return result;
    }
    //start of synchronizing vital data
    private void loadVitalData() {
        VitalData.clear();
        Cursor cursorVital = db.getVitalSignsData();
        if (cursorVital.moveToFirst()) {
            do {
                vitalSigns vital = new vitalSigns(
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
                        cursorVital.getString(cursorVital.getColumnIndex(DatabaseHelper.COLUMN_Created_At)),
                        cursorVital.getInt(cursorVital.getColumnIndex(DatabaseHelper.COLUMN_Vital_STATUS))
                );
                VitalData.add(vital);
            } while (cursorVital.moveToNext());
        }

        vitalAdapter = new vitalSignsAdapter(this, R.layout.vitaldata, VitalData);
        listViewVitalData.setAdapter(vitalAdapter);
    }

    /*
     * this method will simply refresh the list
     *
    private void refreshList() {
        vitalAdapter.notifyDataSetChanged();
    }*/
    /*
     * this method is saving the name to ther server
     * */
    private void saveVitalToServer(int user_id, int EWS, double temperature,int upperBP, int lowerBP, int SPO2, int pulse, int blood_sugar_level, String mealStatus, int HDL,
                                   int LDL, int heart_rate, String level_of_consciousness, String last_menstruation_date, String createdAt) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving ECG...");
        progressDialog.show();

   //     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
   //     final String createdAt= sdf.format(new Date());
        Log.e("add_vital", createdAt);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SAVE_Vital,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            Log.e("MainActivity_Line136:",""+obj.getBoolean("error"));
                            if (!obj.getBoolean("error")) {
                                //if there is a success
                                //storing the name to sqlite with status synced
                                saveVitalToLocalStorage(user_id,EWS,temperature,upperBP,lowerBP,SPO2,pulse,blood_sugar_level,mealStatus,HDL,LDL,heart_rate,level_of_consciousness,last_menstruation_date,createdAt, Vital_SYNCED_WITH_SERVER);
                                Log.e("MainActivity","save name to local storage with name_synced status");
                            } else if (obj.getBoolean("error")){
                                //if there is some error
                                //saving the name to sqlite with status unsynced
                                Log.e("MainActivity","save name to local storage with ECG_not_synced status Because of error");
                                Log.e("MainActivity_error__php",obj.getString("message"));
                                saveVitalToLocalStorage(user_id,EWS,temperature,upperBP,lowerBP,SPO2,pulse,blood_sugar_level,mealStatus,HDL,LDL,heart_rate,level_of_consciousness,last_menstruation_date,createdAt, Vital_NOT_SYNCED_WITH_SERVER);
                            }
                            else
                            {
                                Log.e("MainActivity_error__php",obj.getString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("MainActivity_Line156","onErrorResponse");
                        Log.e("MainActivity_156_VOLLEY", error.toString());
                        progressDialog.dismiss();
                        //on error storing the name to sqlite with status unsynced
                        saveVitalToLocalStorage(user_id,EWS,temperature,upperBP,lowerBP,SPO2,pulse,blood_sugar_level,mealStatus,HDL,LDL,heart_rate,level_of_consciousness,last_menstruation_date,createdAt, Vital_NOT_SYNCED_WITH_SERVER);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
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
                Log.e("MainActivity_Line167","inside map");
                return params;
            }
        };

        MySingleton.getmInstance(this).addToRequestQueue(stringRequest);
    }
    //saving the name to local storage
    private void saveVitalToLocalStorage(int user_id, int EWS, double temperature,int upperBP, int lowerBP, int SPO2, int pulse, int blood_sugar_level, String mealStatus, int HDL,
                                       int LDL, int heart_rate, String level_of_consciousness, String last_menstruation_date, String createdAt, int status) {
        db.addVitalsRecord(user_id,EWS,temperature,upperBP,lowerBP,SPO2,pulse,blood_sugar_level,mealStatus,HDL,LDL,heart_rate,level_of_consciousness,last_menstruation_date,createdAt,status);
        vitalSigns vitalD = new vitalSigns(user_id,EWS,temperature, upperBP,lowerBP,SPO2,pulse,blood_sugar_level,mealStatus,HDL,LDL,heart_rate,level_of_consciousness,last_menstruation_date,createdAt,status);
        this.VitalData.add(vitalD);
        //refreshList();
    }
    //EWS calculation
    private int EWSCalculation(double temperature,int upperBP, int SPO2, int pulse, String level_of_consciousness)
    {
        //https://www.mdcalc.com/national-early-warning-score-news#pearls-pitfalls
        int EWS=0;
        int EWSTemp=this.EWSTemperature(temperature);
        int EWSupperBP=this.EWSSysBP(upperBP);
        int EWSSPO2=this.EWSSPO2(SPO2);
        int EWSPulse=this.EWSPulse(pulse);
        //default EWS score for any supplremental Oxygen is set to zero.
        //default EWS score for heart rate is set to zero.
        //default EWS score for AVPU is set to zero.
        EWS=EWSTemp+EWSupperBP+EWSSPO2+EWSPulse;

        return EWS;
    }
    private int EWSTemperature(double temp)
    {
        int EWS=0;
        if(temp<=Double.valueOf(35))
        {
            EWS=3;
        }
        else if(temp>=Double.valueOf(35.1) && temp<=Double.valueOf(36))
        {
            EWS=1;
        }
        else if(temp>=Double.valueOf(36.1) && temp<=Double.valueOf(38))
        {
            EWS=0;
        }
        else if(temp>=Double.valueOf(38.1) && temp<=Double.valueOf(39))
        {
            EWS=1;
        }
        else if(temp>=Double.valueOf(39.1))
        {
            EWS=2;
        }
        return EWS;
    }
    private int EWSSPO2(int SPO2)
    {
        int EWS=0;
        if(SPO2<=91)
        {
            EWS=3;
        }
        else if(SPO2>=92 && SPO2<=93)
        {
            EWS=2;
        }
        else if(SPO2>=94 && SPO2<=95)
        {
            EWS=1;
        }
        else if(SPO2>=96)
        {
            EWS=0;
        }
        return EWS;
    }
    private int EWSPulse(int pulse)
    {
        int EWS=0;
        if(pulse<=8)
        {
            EWS=3;
        }
        else if(pulse>=9 && pulse<=11)
        {
            EWS=1;
        }
        else if(pulse>=12 && pulse<=20)
        {
            EWS=0;
        }
        else if(pulse>=21 && pulse<=24)
        {
            EWS=2;
        }
        else if(pulse>=25)
        {
            EWS=3;
        }
        return EWS;
    }
    private int EWSSysBP(int sysBP)
    {
        int EWS=0;
        if(sysBP<=90)
        {
            EWS=3;
        }
        else if(sysBP>=91 && sysBP<=100)
        {
            EWS=2;
        }
        else if(sysBP>=101 && sysBP<=110)
        {
            EWS=1;
        }
        else if(sysBP>=111 && sysBP<=219)
        {
            EWS=0;
        }
        else if(sysBP>=220)
        {
            EWS=3;
        }
        return EWS;
    }

}