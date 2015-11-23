package com.example.localize;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.Firebase;

import java.util.TreeMap;

public class MainActivity extends Activity {

    Button bt;
    EditText etx1, ety1, aix1, aiy1;
    TextView tv_pollResults;
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
        getLocationPermission();
    }

    private void initializeFirebase() {
        myFirebaseRef = new Firebase("https://torrid-torch-2612.firebaseio.com/");
    }

    private void getViewIds() {
        etx1 = (EditText) findViewById(R.id.et_x);
        ety1 = (EditText) findViewById(R.id.et_y);
        aix1 = (EditText) findViewById(R.id.ai_x);
        aiy1 = (EditText) findViewById(R.id.ai_y);
        tv_pollResults = (TextView) findViewById(R.id.tv_rssi);
    }

    public void onBtPollClick(View v) {

        tv_pollResults.setText("");
        tv_pollResults.setVisibility(View.GONE);

        if(wifiSQLData == null){
            wifiSQLData = new TreeMap<>();
        }

        //Get coordinate values from user
        int incX = Integer.parseInt(aix1.getText().toString());
        int startX = Integer.parseInt(etx1.getText().toString());
        int incY = Integer.parseInt(aiy1.getText().toString());
        int startY = Integer.parseInt(ety1.getText().toString());

        TreeMap<String, String> rssiData = Utilities.getUtilities().getRssiData(this);

        for (String key : rssiData.keySet()) {
            String display = key + "  " + rssiData.get(key);
            Log.d("finalData", display);
        }

        String newValue = "";
        for (String val: rssiData.keySet()){
            String newKey = startX + "," + startY;
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
        startX += incX;
        etx1.setText(String.valueOf(startX));
        startY += incY;
        ety1.setText(String.valueOf(startY));


        if(tv_pollResults.getVisibility() == View.GONE){
            String display = "";
            for(String cood: wifiSQLData.keySet())
            {
                String values = wifiSQLData.get(cood);
                display = values.replaceAll(":", "/s");

            }
            tv_pollResults.setText(display);
            tv_pollResults.setVisibility(View.VISIBLE);
        }

        rssiData.clear();
        wifiSQLData.clear();
    }


    public void onBtMapClick(View v) {
        Intent i = new Intent(this, MapActivity.class);
        startActivity(i);
    }

    private void getLocationPermission() {
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
