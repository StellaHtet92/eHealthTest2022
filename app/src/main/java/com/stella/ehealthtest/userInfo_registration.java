package com.stella.ehealthtest;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.stella.ehealthtest.databinding.ActivityUserInfoRegistrationBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class userInfo_registration extends AppCompatActivity {
    private ActivityUserInfoRegistrationBinding binding; //it is based from your layout file name
    DatePickerDialog datePickerDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_registration);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //view binding
        /*binding=ActivityUserInfoRegistrationBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);*/
        Button btn1=(Button) findViewById(R.id.btnDoB);
        btn1.setOnClickListener(new View.OnClickListener() {
        @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(userInfo_registration.this, new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                btn1.setText(dayOfMonth + "/"
                                        + (monthOfYear + 1) + "/" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        Button btnRegister=(Button) findViewById(R.id.btnUserinfo_Register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register_user(v);
                //Toast.makeText(userInfo_registration.this,"Ok",Toast.LENGTH_SHORT).show();
            }
        });

    }
    public void register_user(View v)
    {
        EditText edit1=(EditText) findViewById(R.id.reg_username);
        String username=edit1.getText().toString();
        EditText edit2=(EditText)findViewById(R.id.reg_password);
        String password=edit2.getText().toString();
        EditText edit3=(EditText)findViewById(R.id.reg_confirmpwd);
        String confirmPwd=edit3.getText().toString();
        EditText edit4=(EditText)findViewById(R.id.reg_mobileNumber);
        String mobile=edit4.getText().toString();
        //get date of birth data
        Button dateButton=(Button) findViewById(R.id.btnDoB);
        String dob = ""+dateButton.getText();
        Date dateOfBirth = null;
        if(dob!=null) {
            Toast.makeText(this, "Date of Birth from Spinner:" + dob, Toast.LENGTH_LONG).show();
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            try {
                dateOfBirth = new Date(format.parse(dob).getTime());
                System.out.println(dateOfBirth);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //          Toast.makeText(RegisterActivity02.this, dateOfBirth.toString(), Toast.LENGTH_SHORT).show();
        }
        //get gender
        Spinner genderSpinner=(Spinner)findViewById(R.id.spinner_Gender);
        String gender=genderSpinner.getSelectedItem().toString();
        //get BloodType
        Spinner bloodTypeSpinner=(Spinner) findViewById(R.id.spinner_BloodType);
        String bloodType=bloodTypeSpinner.getSelectedItem().toString();
        if(TextUtils.isEmpty(username)|| TextUtils.isEmpty(password) || TextUtils.isEmpty(mobile))
        {
            Toast.makeText(userInfo_registration.this,"All fields required!!",Toast.LENGTH_LONG).show();
        }
        registerNewAccount(username,password,mobile,dateOfBirth.toString(),gender,bloodType);

    }

   /*public void register_user(View v) {


        EditText edit1=(EditText) findViewById(R.id.reg_username);
        String username=edit1.getText().toString();
        EditText edit2=(EditText)findViewById(R.id.reg_password);
        String password=edit2.getText().toString();
        EditText edit3=(EditText)findViewById(R.id.reg_confirmpwd);
        String confirmPwd=edit3.getText().toString();
        EditText edit4=(EditText)findViewById(R.id.reg_mobileNumber);
        String mobile=edit4.getText().toString();
        //get date of birth data
        Button dateButton=(Button) findViewById(R.id.btnDoB);
        String dob = ""+dateButton.getText();
        Date dateOfBirth = null;
        if(dob!=null) {
            Toast.makeText(this, "Date of Birth from Spinner:" + dob, Toast.LENGTH_LONG).show();
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            try {
                dateOfBirth = new java.sql.Date(format.parse(dob).getTime());
                System.out.println(dateOfBirth);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //          Toast.makeText(RegisterActivity02.this, dateOfBirth.toString(), Toast.LENGTH_SHORT).show();
        }
        //get gender
        Spinner genderSpinner=(Spinner)findViewById(R.id.spinner_Gender);
        String gender=genderSpinner.getSelectedItem().toString();
        //get BloodType
        Spinner bloodTypeSpinner=(Spinner) findViewById(R.id.spinner_BloodType);
        String bloodType=bloodTypeSpinner.getSelectedItem().toString();
        if(TextUtils.isEmpty(username)|| TextUtils.isEmpty(password) || TextUtils.isEmpty(mobile))
        {
            Toast.makeText(userInfo_registration.this,"All fields required!!",Toast.LENGTH_LONG).show();
        }
       Toast.makeText(userInfo_registration.this,password,Toast.LENGTH_SHORT).show();
        registerNewAccount(username,password,mobile,dateOfBirth.toString(),gender,bloodType);

    }*/
    private void registerNewAccount(final String username, String password, String mobile, String dateOfBirth, String gender, String bloodType )
    {
        final String[] TAG_ID = {"error","message","user_id"};
        final int[] user_id = {0};
        ProgressDialog progressDialog = new ProgressDialog(userInfo_registration.this);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setTitle("Registering New Account");
        progressDialog.show();
       String uRL="http://10.0.2.2/login_register_app/register.php";//for android emulator, you need to open http://10.0.2.2 on the emulator browser
       //String uRL="http://10.90.0.229/login_register_app/register.php";//for android emulator, you need to open http://10.0.2.2 on the emulator browser
        StringRequest request = new StringRequest(Request.Method.POST, uRL, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
               // System.out.println("Response at Line 176 UserInfo_Registration:"+response);
                //String subString=response.substring(0,24);
                //String restString=response.substring(24,response.length());
                //System.out.println("Splitted:"+subString);
                //System.out.println("Left:"+restString);
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
            {
                try
                {
                    user_id[0] =Integer.parseInt(jsonObject.getString(TAG_ID[2]));
                    System.out.println("userid:"+ user_id[0]);
                    // on below line we are displaying a success toast message.
                    Toast.makeText(userInfo_registration.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    Toast.makeText(userInfo_registration.this, "Before calling basic_info_registration", Toast.LENGTH_SHORT).show();
                    Intent i=new Intent(userInfo_registration.this,basicInfo_Registration.class);
                    i.putExtra("ID", user_id[0]);
                    i.putExtra("Username",username);
                    startActivity(i);
                    finish();
                }catch (JSONException e)
                {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
                Toast.makeText(userInfo_registration.this, response, Toast.LENGTH_LONG).show();

            }
            else {
                progressDialog.dismiss();
                Toast.makeText(userInfo_registration.this,response, Toast.LENGTH_LONG).show();
            }
            }
        }, new com.android.volley.Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(userInfo_registration.this,"Fail to get response="+error, Toast.LENGTH_LONG).show();
            }
        })

        {
            protected Map<String, String> getParams() throws AuthFailureError
            {
                HashMap<String, String> param = new HashMap<>();
                param.put("username",username);
                param.put("password",password);
                param.put("mobile",mobile);
                param.put("dateOfBirth",dateOfBirth);
                param.put("gender",gender);
                param.put("bloodType",bloodType);
                return param;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(userInfo_registration.this).addToRequestQueue(request);
    }
}