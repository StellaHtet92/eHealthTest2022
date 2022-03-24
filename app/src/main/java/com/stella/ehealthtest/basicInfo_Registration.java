package com.stella.ehealthtest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class basicInfo_Registration extends AppCompatActivity {
    int user_id;
    String name;
    String HCV;
    String HCB;
    String TB;
    String HIV;
    String Diabetes;
    String Other;
    String Allergies;
    String weight;
    String height;
    String BMI;//double will change in php
    String Smoking_status;
    String Num_Ciga_per_day;
    String created_at;
    String updated_at;
    EditText edit_name,edit_allergies,edit_Num_Ciga;
    Spinner spinner_gender,spinner_weight,spinner_height;
    CheckBox chkHCV, chkHCB, chkTB, chkHIV, chkDiabetes, chkOther;
    String user_name;
    Button btnSave, btnCancel;
    private static final int CENTIMETERS_IN_METER = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_info_registration);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle extras=getIntent().getExtras();
        if(extras!=null)
        {
            user_id=extras.getInt("ID");
            System.out.print("User_ID:"+user_id);
            user_name=extras.getString("Username");
            System.out.print("User Name:"+user_name);
        }
        else  System.out.print("Extras is null");
        btnSave=(Button) findViewById(R.id.btnBasicInfo_Save);
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                edit_name=(EditText) findViewById(R.id.reg_name);
                name=edit_name.getText().toString();
                //spinner_gender=(Spinner) findViewById(R.id.genderSpinner);
               // gender=spinner_gender.getSelectedItem().toString();
                chkHCV = (CheckBox) findViewById(R.id.chkHCV);
                if(chkHCV.isChecked())
                {
                    HCV="Yes";
                }
                else HCV="No";
                chkTB = (CheckBox) findViewById(R.id.chkTB);
                if(chkTB.isChecked())
                {
                    TB="Yes";
                }
                else TB="No";
                chkHCB = (CheckBox) findViewById(R.id.chkHCB);
                if(chkHCB.isChecked())
                {
                    HCB="Yes";
                }
                else HCB="No";
                chkHIV = (CheckBox) findViewById(R.id.chkHIV);
                if(chkHIV.isChecked())
                {
                    HIV="Yes";
                }
                else HIV="No";
                chkDiabetes = (CheckBox) findViewById(R.id.chkDiabetes);
                if(chkDiabetes.isChecked())
                {
                    Diabetes="Yes";
                }
                else Diabetes="No";
                chkOther = (CheckBox) findViewById(R.id.chkOthers);
                if(chkOther.isChecked())
                {
                    Other="Yes";
                }
                else Other="No";
                edit_allergies=(EditText) findViewById(R.id.txt_Allergies);
                Allergies=edit_allergies.getText().toString();
                spinner_height=(Spinner) findViewById(R.id.height);
                height=spinner_height.getSelectedItem().toString();
                System.out.println(""+height);
                spinner_weight=(Spinner) findViewById(R.id.weight);
                weight=spinner_weight.getSelectedItem().toString();
                System.out.println(""+weight);
                if((height.isEmpty() == false && weight.isEmpty()) == false)
                {
                    BMI=Double.toString(BMICalculator(Integer.parseInt(weight),Integer.parseInt(height)));
                }
                else BMI=Double.toString(0.0);
                Smoking_status=onRadioButtonClicked(v);
                edit_Num_Ciga=(EditText) findViewById(R.id.txt_NumCigarette);
                Num_Ciga_per_day=edit_Num_Ciga.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                created_at = sdf.format(new Date()).toString();
                System.out.println("Created_at:"+created_at);
                updated_at=created_at;
                System.out.println("Updated_at:"+updated_at);
                insertBasicInfo(user_id,user_name, name,HCV,TB,HCB,HIV,Diabetes,Other,Allergies,height,weight,BMI,Smoking_status,Num_Ciga_per_day,created_at,updated_at);

            }
        });
    }
    public double BMICalculator(int weightKg,int heightCm)
    {
        double bmi = ((weightKg / heightCm )/(heightCm )*10000); //(16.6 kg / 99.1 cm / 99.1 cm) x 10,000 = 16.9
        return bmi;
    }
    public String onRadioButtonClicked(View view) {
        RadioButton yes,no;
        boolean checked = false;
        yes=findViewById(R.id.rdsmokeYes);
        no=findViewById(R.id.rdsmokeNo);
        TextView noOfCigarette=(TextView) findViewById(R.id.txt_NumCigarette);
        String result;
        if(yes.isChecked())
        {
            checked=true;
            noOfCigarette.setEnabled(true);
            result="Yes";
        }
        else if(no.isChecked())
        {
            checked=false;
            noOfCigarette.setText("0");
            noOfCigarette.setEnabled(false);
            result="No";
        }
        else
        {
            Toast.makeText(this,"Please select smoke status",Toast.LENGTH_LONG).show();
            result="No";
            finish();
        }
        return result;
    }
    private void insertBasicInfo(int user_id, final String username, String name, String HCV, String TB, String HCB, String HIV, String Diabetes, String Other, String Allergies,
                                 String height, String weight,String BMI,String Smoking_status,String Num_Ciga_per_day, String created_at, String updated_at)
    {
        final String[] TAG_ID = {"user_id"};
        //final int[] user_id = {0};
        ProgressDialog progressDialog = new ProgressDialog(basicInfo_Registration.this);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setTitle("Inserting Basic Information");
        progressDialog.show();
         String uRL="http://192.168.0.101/his/insert_user_info.php";//for android emulator, you need to open http://10.0.2.2 on the emulator browser
       // String uRL="http://10.90.0.229/login_register_app/insert_user_info.php";//for android emulator, you need to open http://10.0.2.2 on the emulator browser
        StringRequest request = new StringRequest(Request.Method.POST, uRL, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                String error=null;
                try {
                    jsonObject = new JSONObject(response);
                    System.out.println("Json object:"+jsonObject);
                    error=jsonObject.getString(TAG_ID[0]);
                    System.out.println("Json object error:"+error);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(error!=null && error.equals("false"))
                try
                {
                        Toast.makeText(basicInfo_Registration.this, jsonObject.getString("message: "), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        Toast.makeText(basicInfo_Registration.this, response, Toast.LENGTH_LONG).show();
                        Intent i=new Intent(basicInfo_Registration.this,home_screen.class);
                        i.putExtra("ID",user_id);
                        i.putExtra("Username",username);
                        startActivity(i);
                        finish();


                }catch (JSONException e)
                {
                    e.printStackTrace();
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(basicInfo_Registration.this,response, Toast.LENGTH_LONG).show();
                }
            }
        }, new com.android.volley.Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(basicInfo_Registration.this,"Fail to get response="+error, Toast.LENGTH_LONG).show();
            }
        })

        {
            protected Map<String, String> getParams() throws AuthFailureError
            {
                HashMap<String, String> param = new HashMap<>();
                param.put("user_id",""+user_id);
                param.put("username",username);
                param.put("name",name);
                param.put("HCV",HCV);
                param.put("TB",TB);
                param.put("HCB",HCB);
                param.put("HIV",HIV);
                param.put("Diabetes",Diabetes);
                param.put("Other",Other);
                param.put("Allergies",Allergies);
                param.put("height",height);
                param.put("weight",weight);
                param.put("BMI",BMI);
                param.put("Smoking_status",Smoking_status);
                param.put("Num_Ciga_per_day",Num_Ciga_per_day);
                param.put("created_at",created_at);
                param.put("updated_at",updated_at);
                return param;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(basicInfo_Registration.this).addToRequestQueue(request);
    }
}