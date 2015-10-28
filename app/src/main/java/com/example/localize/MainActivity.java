package com.example.localize;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.Firebase;

public class MainActivity extends Activity {

	Button bt;
	EditText etx1, ety1, aix1, aiy1;
	HashMap<String, ArrayList<String>> rssiData;
	ArrayList<String> coods;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.d("test", "wifitest");
		Firebase.setAndroidContext(this);
		init();
		if (rssiData == null) {
			rssiData = readDataFromFile();
		}
	}

	private void init() {
		getViewIds();
		initializeFirebase();

	}

	private void initializeFirebase() {

		Firebase myFirebaseRef = new Firebase("https://torrid-torch-2612.firebaseio.com/");
        myFirebaseRef.child("message").setValue("Do you have data? You'll love Firebase.");
	}

	private void getViewIds() {
		etx1 = (EditText) findViewById(R.id.editText1);
		ety1 = (EditText) findViewById(R.id.editText2);
		aix1 = (EditText) findViewById(R.id.editText3);
		aiy1 = (EditText) findViewById(R.id.editText4);
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, ArrayList<String>> readDataFromFile() {
		FileInputStream fileInputStream;
		HashMap<String, ArrayList<String>> myNewlyReadInMap = null;
		File baseDir = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/localize");
		baseDir.mkdirs();
		String fileName = "myMap.txt";
		File file = new File(baseDir, fileName);
		try {
			fileInputStream = new FileInputStream(file);
			ObjectInputStream objectInputStream = new ObjectInputStream(
					fileInputStream);

			myNewlyReadInMap = (HashMap<String, ArrayList<String>>) objectInputStream
					.readObject();
			objectInputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return myNewlyReadInMap;
	}

	public void onBtPollClick(View v) {
		int incX = Integer.parseInt(aix1.getText().toString());
		int startX = Integer.parseInt(etx1.getText().toString());

		int incY = Integer.parseInt(aiy1.getText().toString());
		int startY = Integer.parseInt(ety1.getText().toString());

		if (rssiData == null) {
			rssiData = new HashMap<String, ArrayList<String>>();
		}
		WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		String info = "" + wifi.getConnectionInfo().getRssi();
		Log.d("WiFi: ", info);

		HashSet<String> aps = new HashSet<String>();
		HashMap<String, String> rssiValues = new HashMap<String, String>();
		HashMap<String, ArrayList<String>> table = new HashMap<String, ArrayList<String>>();
		ArrayList<String> temp_bssids = new ArrayList<String>();

		aps.add("00229092da8");
		aps.add("00229092f40");
		aps.add("00229092d32");
		aps.add("00229092f0a");
		aps.add("00229092d38");
		aps.add("0023eb606ce");
		aps.add("00229092df5");
		aps.add("00229092fd2");
		aps.add("0022909302b");
		aps.add("0022909301c");
		aps.add("e804620b333");
		aps.add("00229092d9e");
		aps.add("00229092fdf");
		aps.add("00229092fe0");
		aps.add("0022909302d");
		aps.add("00229092f4d");
		aps.add("00229093031");
		aps.add("00229092eb9");
		aps.add("00229092fe7");
		aps.add("00229092fda");
		aps.add("00229092d59");
		aps.add("00229092eaf");
		aps.add("00229092f40");

		List<ScanResult> result = new ArrayList<ScanResult>();

		result = wifi.getScanResults();

		for (int i = 0; i < result.size(); i++) {
			// String ssid = result.get(i).SSID;
			String bssid = result.get(i).BSSID;
			int rssi = result.get(i).level;
			String rssiString = String.valueOf(rssi);
			// String key = ssid + "   " + bssid;
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
					ArrayList<String> temp = new ArrayList<String>();
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
					ArrayList<String> temp = new ArrayList<String>();
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

			String coodStr = "(" + String.valueOf(startX) + ","
					+ String.valueOf(startY) + ")";
			if (!rssiData.containsKey(s)) {
				ArrayList<String> a = new ArrayList<String>();
				a.add(coodStr + "->" + String.valueOf(temp_sum / temp.size()));
				rssiData.put(s, a);
			} else {
				ArrayList<String> t = rssiData.get(s);
				t.add(coodStr + "->" + String.valueOf(temp_sum / temp.size()));
				rssiData.put(s, t);
			}

		}

		for (String key : rssiData.keySet()) {
			String val_string = "";
			for (String s : rssiData.get(key)) {
				val_string += s + " ";
			}
			String display = key + "  " + val_string;
			Log.d("finalData", display);
		}

		storeDataInFile(rssiData);
	}

	private void storeDataInFile(HashMap<String, ArrayList<String>> rssiData2) {
		FileOutputStream fileOutputStream;
		File baseDir = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/localize");
		baseDir.mkdirs();
		String fileName = "myMap.txt";
		String fileName2 = "myMap2.txt";

		File file = new File(baseDir, fileName);
		File file2 = new File(baseDir, fileName2);

		try {
			FileWriter fstream = new FileWriter(file2);
			BufferedWriter out = new BufferedWriter(fstream);
			fileOutputStream = new FileOutputStream(file);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(
					fileOutputStream);
			objectOutputStream.writeObject(rssiData);
			for (String key : rssiData.keySet()) {
				String val_string = "";
				for (String s : rssiData.get(key)) {
					val_string += s + " ";
				}
				out.write(key + "--->" + val_string);
				out.write("\n");
				String display = key + "  " + val_string;
				Log.d("finalData", display);
			}
			out.close();
			objectOutputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void onBtMapClick(View v) {
		Intent i = new Intent(this, MapActivity.class);
		startActivity(i);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
