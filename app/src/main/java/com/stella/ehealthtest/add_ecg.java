package com.stella.ehealthtest;

import androidx.appcompat.app.AppCompatActivity;
import com.stella.ehealthtest.ecg.*;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.Buffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
//this is for creating line graph
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.stella.ehealthtest.ecg.ecgAdapter;

import org.json.JSONException;
import org.json.JSONObject;


public class add_ecg extends AppCompatActivity implements View.OnClickListener{
    TextView myLabel;
    EditText myTextbox;
    BluetoothAdapter mBluetoothAdapter;
    UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    String TAG="Main Activity:Tutorial";
    private InputStream mmInputStream;
    private OutputStream mmOutputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;
    //this is for plotting line graph for ECG data
    String[] ECGEntries;
    int[] numbers;
    int ECG=0;
    int i=0;
    LineChart lineChart;
    LineData lineData;
    LineDataSet lineDataSet;
    List<Entry> entryList = new ArrayList<>();
    //code from https://stackoverflow.com/questions/10327506/android-arduino-bluetooth-data-transfer
    //for ecg data to be synchronized
    public static final String URL_SAVE_ECG = "http://192.168.0.101//his/saveECG.php";
    //database helper object
    private DatabaseHelper db;
    //View objects
    private Button buttonSave;
    private String ecgID;
    private ListView listViewECGData;
    //List to store all the ecg
    private List<ecg> ecgData;
    //1 means data is synced and 0 means data is not synced
    public static final int ECG_SYNCED_WITH_SERVER = 1;
    public static final int ECG_NOT_SYNCED_WITH_SERVER = 0;
    //a broadcast to know weather the data is synced or not
    public static final String DATA_SAVED_BROADCAST = "net.simplifiedcoding.datasaved";
    //Broadcast receiver to know the sync status
    private BroadcastReceiver broadcastReceiver;
    String CurrentECGData="";
    TextView labelID,labelUsername;
    Intent intent;
    int userID=0;
    String userName="";

    //adapterobject for list view
    private com.stella.ehealthtest.ecg.ecgAdapter ecgAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ecg);
        Button openButton = (Button)findViewById(R.id.open);
        Button closeButton = (Button)findViewById(R.id.close);
        Button saveButton=(Button) findViewById((R.id.buttonSave));

        //to register the receiver
        registerReceiver(new NetworkStateChecker(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        //initializing views and objects
        db = new DatabaseHelper(this);
        ecgData = new ArrayList<>();
        labelID=(TextView)findViewById(R.id.labelUserID);
        labelUsername=(TextView)findViewById(R.id.labelUserName);
        Intent i=getIntent();
        userID= Integer.parseInt(i.getStringExtra("ID"));
        userName=i.getStringExtra("Username");
        labelID.append(""+userID);
        labelUsername.append(userName);
        myLabel = (TextView)findViewById(R.id.label);
        listViewECGData = (ListView) findViewById(R.id.listViewECG);

        //registering the broadcast receiver to update sync status
        registerReceiver(broadcastReceiver, new IntentFilter(DATA_SAVED_BROADCAST));
        //Open Button
        openButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    findBT();
                    openBT();
                }
                catch (IOException ex) { }
            }
        });
        //Close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    closeBT();
                }
                catch (IOException ex) { }
            }
        });
        //adding click listener to button
        saveButton.setOnClickListener(this);

        //calling the method to load all the stored names
        loadECGData();
        //the broadcast receiver to update sync status
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                //loading the names again
                loadECGData();
            }
        };
    }
    void findBT() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null) {
            myLabel.setText("");
            myLabel.setText("No bluetooth adapter available");
        }

        if(!mBluetoothAdapter.isEnabled()) {
            myLabel.setText("");
            myLabel.setText("Please turn on Bluetooth!!");
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBluetooth);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0) {
            for(BluetoothDevice device : pairedDevices) {
                //hardcoded with sensor name
                if(device.getName().equals("ESP32_ECG")) {
                    mmDevice = device;
                    break;
                }
            }
        }
        myLabel.setText("");
        myLabel.setText("Bluetooth Device Found");
    }
    //end of find BT
    void openBT() throws IOException {
        if(!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBluetooth);
        }
        else
        {
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard //SerialPortService ID

            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            if(mmSocket!=null) {
                mmSocket.connect();
                mmOutputStream = mmSocket.getOutputStream();
                mmInputStream = mmSocket.getInputStream();
                beginListenForData();
                myLabel.setText("");
                myLabel.setText("Bluetooth Opened and Listen for data");
            }
        }
    }
    //end of openBT
    void beginListenForData() {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character
        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        int count=0;
        long startTime=System.currentTimeMillis();
        ProgressDialog progress = new ProgressDialog(add_ecg.this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
        workerThread = new Thread(new Runnable() {
            public void run() {

                while(!Thread.currentThread().isInterrupted() && !stopWorker && (System.currentTimeMillis()-startTime)<=10000) {

                    try {
                        int bytesAvailable = mmInputStream.available();

                        if(bytesAvailable > 0) {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++) {
                                byte b = packetBytes[i];
                                if(b == delimiter) {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;
                                    handler.post(new Runnable() {

                                        public void run() {
                                            // myLabel.append("\n");
                                            // myLabel.append(data);
                                            CurrentECGData=CurrentECGData+"\n"+data;
                                            String result[] = data.split(",");
                                            String timestamp=result[0];
                                            String value = result[result.length-1].replace("E","");
                                            value = value.replace("\r","");
                                            int j=Integer.parseInt(timestamp);
                                            entryList.add(new Entry(j,Integer.parseInt(value)));
                                        }
                                    });
                                }
                                else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex) {
                        stopWorker = true;
                    }
                }

                progress.dismiss();
                ArrayList<Entry> chartEntry=new ArrayList<>(entryList);
                create_graph(chartEntry);
                //  MainActivity.this.create_graph(entryList);
            }

        });

        workerThread.start();
    }

    void sendData() throws IOException {
        String msg = myTextbox.getText().toString();
        msg += "\n";
        //mmOutputStream.write(msg.getBytes());
        mmOutputStream.write('A');
        myLabel.append("Data Sent");
    }

    void closeBT() throws IOException {
        stopWorker = true;
        /**
         * Reset input and output streams and make sure socket is closed.**/
        if (mmInputStream != null) {
            try {mmInputStream.close();} catch (Exception e) {}
            mmInputStream = null;
        }

        if (mmOutputStream != null) {
            try {mmOutputStream.close();} catch (Exception e) {}
            mmOutputStream = null;
        }

        if (mmSocket != null) {
            try {mmSocket.close();} catch (Exception e) {}
            mmSocket = null;
        }
        myLabel.append("Bluetooth Closed");
    }

    private void showMessage(String theMsg) {
        Toast msg = Toast.makeText(getBaseContext(),
                theMsg, (Toast.LENGTH_LONG)/160);
        msg.show();
    }
    //plotting line graph
    private void create_graph(List<Entry> InputData)
    {
        lineChart = findViewById(R.id.lineChart);
        lineDataSet = new LineDataSet(InputData, "ECG Data");
        //lineDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        //lineDataSet.setFillAlpha(110);
        lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);

       // lineChart.setVisibleXRangeMaximum(10000);
       // lineChart.moveViewToX(500);
        lineChart.invalidate();
       // lineDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
       // lineDataSet.setValueTextColor(Color.BLACK);
        //lineDataSet.setValueTextSize(5f);

    }
    //start of synchronizing ecg data
    private void loadECGData() {
        ecgData.clear();
        Cursor cursor = db.getECGData();
        if (cursor.moveToFirst()) {
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
    /*
     * this method is saving the name to ther server
     * */
    private void saveECGToServer() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving ECG...");
        progressDialog.show();
        final String ecg=this.CurrentECGData;//this.lineChart.getLineData().toString();
        this.CurrentECGData="";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        final String createdAt= sdf.format(new Date());
        Log.e("MainActivity", createdAt);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SAVE_ECG,
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
                                saveECGToLocalStorage(userID,ecg,createdAt, ECG_SYNCED_WITH_SERVER);
                                Log.e("MainActivity","save name to local storage with name_synced status");
                            } else if (obj.getBoolean("error")){
                                //if there is some error
                                //saving the name to sqlite with status unsynced
                                Log.e("MainActivity","save name to local storage with ECG_not_synced status Because of error");
                                Log.e("MainActivity_error__php",obj.getString("message"));
                                saveECGToLocalStorage(userID,ecg,createdAt, ECG_NOT_SYNCED_WITH_SERVER);
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
                        saveECGToLocalStorage(userID,ecg,createdAt, ECG_NOT_SYNCED_WITH_SERVER);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", ""+userID);
               // params.put("vital_id", ""+vital_id);
                params.put("ecg", ecg);
                params.put("createdAt",createdAt);
                Log.e("MainActivity_Line167","inside map");
                return params;
            }
        };

        MySingleton.getmInstance(this).addToRequestQueue(stringRequest);
    }
    //saving the name to local storage
    private void saveECGToLocalStorage(int user_id, String ecgData, String createdAt, int status) {
        db.addECG(user_id,ecgData,createdAt, status);
        ecg ecgD = new ecg(user_id,ecgData,createdAt,status);
        this.ecgData.add(ecgD);
        refreshList();
    }

    @Override
    public void onClick(View view) {
        Log.e("MainActivity_Line184","calling saveECGtoServer()");
        saveECGToServer();
    }
}