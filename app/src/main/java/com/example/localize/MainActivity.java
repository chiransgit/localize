package com.example.localize;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;

public class MainActivity extends Activity {

    Button bt;
    EditText etx1, ety1, aix1, aiy1;
    TreeMap<String, String> rssiData;
    TreeMap<String, String> wifiSQLData;
    Firebase myFirebaseRef = null;
    MySQLiteDatabaseAdapter sqlHelper;
    private static final int REQUEST_FINE_LOCATION=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sqlHelper = new MySQLiteDatabaseAdapter(this);
        Firebase.setAndroidContext(this);
        init();
    }

    private void init() {
        getViewIds();
        initializeFirebase();

    }

    private void initializeFirebase() {
        myFirebaseRef = new Firebase("https://torrid-torch-2612.firebaseio.com/");
    }

    private void getViewIds() {
        etx1 = (EditText) findViewById(R.id.editText1);
        ety1 = (EditText) findViewById(R.id.editText2);
        aix1 = (EditText) findViewById(R.id.editText3);
        aiy1 = (EditText) findViewById(R.id.editText4);
    }

    public void onBtPollClick(View v) {

        //Get coordinate values from user
        int incX = Integer.parseInt(aix1.getText().toString());
        int startX = Integer.parseInt(etx1.getText().toString());
        int incY = Integer.parseInt(aiy1.getText().toString());
        int startY = Integer.parseInt(ety1.getText().toString());

        if (rssiData == null) {
            rssiData = new TreeMap<>();
        }
        if(wifiSQLData == null){
            wifiSQLData = new TreeMap<>();
        }
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        //current connected wifi strength
        String info = "" + wifi.getConnectionInfo().getRssi();
        Log.d("WiFi: ", info);


        HashSet<String> aps = new HashSet<>();
        TreeMap<String, String> rssiValues = new TreeMap<>();
        TreeMap<String, ArrayList<String>> table = new TreeMap<>();
        ArrayList<String> temp_bssids = new ArrayList<>();

        aps.add("00229092da8"); //5
        aps.add("00229092d32"); //1
        aps.add("00229092f0a"); //9
        aps.add("00229092d38"); //2
        aps.add("0023eb606ce"); //21
        aps.add("00229092df5"); //6
        aps.add("00229092fd2"); //12
        aps.add("0022909302b"); //18
        aps.add("0022909301c"); //17
        aps.add("e804620b333"); //22
        aps.add("00229092d9e"); //4
        aps.add("00229092fdf"); //14
        aps.add("00229092fe0"); //15
        aps.add("0022909302d"); //19
        aps.add("00229092f4d"); //11
        aps.add("00229093031"); //20
        aps.add("00229092eb9"); //8
        aps.add("00229092fe7"); //16
        aps.add("00229092fda"); //13
        aps.add("00229092d59"); //3
        aps.add("00229092eaf");  //7
        aps.add("00229092f40"); //10

        List<ScanResult> result = new ArrayList<>();

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.


            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);


                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        result = wifi.getScanResults();
        if(result != null){
            Log.d("Scan Result Size ", String.valueOf(result.size()));
        }


        for (int i = 0; i < result.size(); i++) {
            String bssid = result.get(i).BSSID;
            int rssi = result.get(i).level;
            String rssiString = String.valueOf(rssi);


            String[] bssidSubStringArr = bssid.split(":");
            String ss_bssid = "";

            for (int j = 0; j < bssidSubStringArr.length; j++) {
                ss_bssid += bssidSubStringArr[j];
            }

            ss_bssid = ss_bssid.substring(0, 11);
            rssiValues.put(bssid, rssiString);
            temp_bssids.add(ss_bssid);
            Log.d("bssid", ss_bssid);

            if (aps.contains(ss_bssid)) {
                if (!table.containsKey(ss_bssid)) {
                    ArrayList<String> temp = new ArrayList<>();
                    temp.add(rssiString);
                    table.put(ss_bssid, temp);
                } else {
                    ArrayList<String> t = table.get(ss_bssid);
                    t.add(rssiString);
                    table.put(ss_bssid, t);
                }
            }
        }

        for (String s : aps) {
            if (!temp_bssids.contains(s)) {
                if (!table.containsKey(s)) {
                    ArrayList<String> temp = new ArrayList<>();
                    temp.add("0");
                    table.put(s, temp);
                } else {
                    ArrayList<String> t = table.get(s);
                    t.add("0");
                    table.put(s, t);
                }
            }
        }

        for (String key : table.keySet()) {
            String val_string = "";
            for (String s : table.get(key)) {
                val_string += s + " ";
            }
            String display = key + "  " + val_string;
            Log.d("Result", display);
        }

        for (String key : rssiValues.keySet()) {
            String display = key + "  " + rssiValues.get(key);
            Log.d("scanresults", display);
        }

        temp_bssids.clear();

        startX += incX;
        etx1.setText(String.valueOf(startX));

        startY += incY;
        ety1.setText(String.valueOf(startY));

        for (String s : table.keySet()) {
            ArrayList<String> temp = table.get(s);
            int temp_sum = 0;
            if (temp.size() > 1) {
                for (String x : temp) {
                    temp_sum += Integer.parseInt(x);
                }
            }

            if (!rssiData.containsKey(s)) {
                String a = String.valueOf(temp_sum / temp.size());
                rssiData.put(s, a);
            } else {
                String t = String.valueOf(temp_sum / temp.size());
                rssiData.put(s, t);
            }

        }

        for (String key : rssiData.keySet()) {

            String display = key + "  " + rssiData.get(key);
            Log.d("finalData", display);
        }

        String newValue = "";
        for (String val: rssiData.keySet()){

            String newKey = "(" + startX + "," + startY + ")";
            newValue += rssiData.get(val) + ":";
            wifiSQLData.put(newKey, newValue);
        }

        for(String cood: wifiSQLData.keySet()){
            Log.d("WIFI SQL DATA", cood + " " + wifiSQLData.get(cood));
            long id = sqlHelper.insertData(cood, wifiSQLData.get(cood));
            if(id<0){
                Message.message(this, "Unsuccessful");
            }
            else{
                Message.message(this, "Successfully inserted a row");
            }
        }

        rssiValues.clear();
        table.clear();
        rssiData.clear();
        wifiSQLData.clear();


    }


    public void onBtMapClick(View v) {
        Intent i = new Intent(this, MapActivity.class);
        startActivity(i);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
