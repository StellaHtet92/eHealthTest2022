package com.stella.ehealthtest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.stella.ehealthtest.ecg.DatabaseHelper;
import com.stella.ehealthtest.ecg.ecg;
import com.stella.ehealthtest.ecg.ecgAdapter;
import com.stella.ehealthtest.vitalSigns.vitalSigns;
import com.stella.ehealthtest.vitalSigns.vitalSignsAdapter;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;

public class home_screen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    Toolbar toolbar;
    int userID=0;
    String userName="default";
    //for ecg
    private ListView listViewECGData;
    //List to store all the ecg
    private List<ecg> ecgData;
    //adapterobject for list view
    private com.stella.ehealthtest.ecg.ecgAdapter ecgAdapter;
    private DatabaseHelper db;
    //a broadcast to know weather the data is synced or not
    public static final String DATA_SAVED_BROADCAST = "net.simplifiedcoding.datasaved";
    //Broadcast receiver to know the sync status
    private BroadcastReceiver broadcastReceiver;
    //for vital
    private ListView listViewVitalData;
    //List to store all the ecg
    private List<vitalSigns> VitalData;
    private com.stella.ehealthtest.vitalSigns.vitalSignsAdapter vitalAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        // drawer layout instance to toggle the menu icon to open
        // drawer and back button to close drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        Menu menu=navigationView.getMenu();

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.bringToFront();

        //end of navigatin drawer menu
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);
        //for displaying ecg and vital signs list view
        db = new DatabaseHelper(this);
        ecgData = new ArrayList<>();
        VitalData=new ArrayList<>();
        //load ecg data
        listViewECGData = (ListView) findViewById(R.id.listViewECGHomeScreen);
        loadECGData();
        //load vital data
        listViewVitalData = (ListView) findViewById(R.id.listViewVitalHomeScreen);
        loadVitalData();
        //the broadcast receiver to update sync status
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                //loading the names again
                loadECGData();
                loadVitalData();
            }
        };
        Intent intent = getIntent();
        userID = Integer.parseInt(intent.getStringExtra("ID"));
        userName=intent.getStringExtra("Username");
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home: break;
            case R.id.nav_profile:
                Intent intent1=new Intent(home_screen.this,userInfo_registration.class);
                startActivity(intent1);
                break;
            case R.id.nav_basicInfo:
                Intent intent2=new Intent(home_screen.this,basicInfo_Registration.class);
                startActivity(intent2);
                break;
            case R.id.nav_vital:
                Intent intent3=new Intent(home_screen.this,add_vitals.class);
                intent3.putExtra("ID", ""+userID);
                intent3.putExtra("Username", userName);
                startActivity(intent3);
                break;
            case R.id.nav_glucometer:

                break;
            case R.id.nav_ecg:
                Intent intent4=new Intent(home_screen.this,add_ecg.class);
                intent4.putExtra("ID", ""+userID);
                intent4.putExtra("Username", userName);
                startActivity(intent4);
                break;
            case R.id.nav_noti:

                break;
            case R.id.nav_logOut:
                Intent intent5=new Intent(home_screen.this,MainActivity.class);
                startActivity(intent5);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    //start of synchronizing ecg data
    private void loadECGData() {
        ecgData.clear();
        Cursor cursor = db.getECGData();
        if (cursor != null && cursor.getCount() >0 && cursor.moveToFirst()) {
            do {
                ecg ecg = new ecg(
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_User_ID)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ECG)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_Created_At)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_STATUS))
                );
                ecgData.add(ecg);
            } while (cursor.moveToNext());
        }

        ecgAdapter = new ecgAdapter(this, R.layout.ecgdata, ecgData);
        listViewECGData.setAdapter(ecgAdapter);
    }

    /*
     * this method will simply refresh the list
     * */
    private void refreshList() {
        ecgAdapter.notifyDataSetChanged();
    }
    //start of synchronizing vital data
    private void loadVitalData() {
        VitalData.clear();
        Cursor cursorVital = db.getVitalSignsData();
        Log.e("home_screen", ""+cursorVital.getCount());
        if (cursorVital != null && cursorVital.getCount() >0 && cursorVital.moveToFirst()) {
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
                        cursorVital.getString(cursorVital.getColumnIndex(DatabaseHelper.COLUMN_Vital_Created_At)),
                        cursorVital.getInt(cursorVital.getColumnIndex(DatabaseHelper.COLUMN_Vital_STATUS))
                );
                VitalData.add(vital);
            } while (cursorVital.moveToNext());
        }

        vitalAdapter = new vitalSignsAdapter(this, R.layout.vitaldata, VitalData);
        listViewVitalData.setAdapter(vitalAdapter);
    }
}
