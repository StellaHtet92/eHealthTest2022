package com.stella.ehealthtest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    Button LogIn;
    EditText edit1,edit2;
    CheckBox loginState;
    String username,password;
    TextView txtRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtRegister=(TextView) findViewById(R.id.textViewRegister);
        loginState=findViewById(R.id.checkbox);
        LogIn=(Button) findViewById(R.id.login);
        LogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit1=(EditText) findViewById(R.id.username);
                edit2=(EditText) findViewById(R.id.password);
                username=edit1.getText().toString();
                password=edit2.getText().toString();
                login(username,password);
            }
        });
        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewActivity();
            }
        });
    }
    public void openNewActivity(){
        Intent intent = new Intent(this,userInfo_registration.class);
        startActivity(intent);
        finish();
    }
    public void login(String username, String password) {
        final String[] TAG_ID = {"error","message","user_id"};
        final int[] user_id = {0};
        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setTitle("Checking Input Information....");
        progressDialog.show();
        //String uRL="http://10.0.2.2/login_register_app/login.php";//for android emulator, you need to open http://10.0.2.2 on the emulator browser
        // String uRL="http://10.90.0.229/login_register_app/insert_user_info.php";//for android emulator, you need to open http://10.0.2.2 on the emulator browser
        String uRL="http://192.168.0.101/login_register_app/login.php";
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
                if(error!=null && error.equals("false")) {
                    try {
                        user_id[0] = Integer.parseInt(jsonObject.getString(TAG_ID[2]));
                        System.out.println("userid:" + user_id[0]);
                        Toast.makeText(MainActivity.this, jsonObject.getString(TAG_ID[1]), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
                        Intent i = new Intent(MainActivity.this, home_screen.class);
                        i.putExtra("ID", ""+user_id[0]);
                        i.putExtra("Username", username);
                        startActivity(i);
                        finish();

                    } catch (JSONException e) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Catch Exception", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this,response, Toast.LENGTH_LONG).show();
                }
            }
        }, new com.android.volley.Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this,"Fail to get response="+error, Toast.LENGTH_LONG).show();
            }
        })

        {
            protected Map<String, String> getParams() throws AuthFailureError
            {
                HashMap<String, String> param = new HashMap<>();
                param.put("username",username);
                param.put("password",password);
                return param;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(MainActivity.this).addToRequestQueue(request);

    }
}