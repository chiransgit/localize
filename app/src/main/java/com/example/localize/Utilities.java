package com.example.localize;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.firebase.client.core.utilities.Tree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by Chiran on 11/18/15.
 */
public class Utilities {

    private static Utilities utility;
    TreeMap<String, String> rssiData;


    private Utilities(){
    }

    public static Utilities getUtilities(){
        if(utility == null){
            utility = new Utilities();
        }
        return utility;
    }

    public TreeMap getRssiData(Context context){

        if (rssiData == null) {
            rssiData = new TreeMap<>();
        }

        WifiManager wifi = (WifiManager) context.getSystemService(context.WIFI_SERVICE);

        //current connected wifi strength
        String info = "" + wifi.getConnectionInfo().getRssi();
        Log.d("WiFi: ", info);


        HashSet<String> aps = new HashSet<>();
        TreeMap<String, String> rssiValues = new TreeMap<>();
        TreeMap<String, ArrayList<String>> table = new TreeMap<>();
        ArrayList<String> temp_bssids = new ArrayList<>();

        aps.add("00229092d32"); //1
        aps.add("00229092d38"); //2
        aps.add("00229092d59"); //3
        aps.add("00229092d9e"); //4
        aps.add("00229092da8"); //5
        aps.add("00229092df5"); //6
        aps.add("00229092eaf"); //7
        aps.add("00229092eb9"); //8
        aps.add("00229092f0a"); //9
        aps.add("00229092f40"); //10
        aps.add("00229092f4d"); //11
        aps.add("00229092fd2"); //12
        aps.add("00229092fda"); //13
        aps.add("00229092fdf"); //14
        aps.add("00229092fe0"); //15
        aps.add("00229092fe7"); //16
        aps.add("0022909301c"); //17
        aps.add("0022909302b"); //18
        aps.add("0022909302d"); //19
        aps.add("00229093031"); //20
        aps.add("0023eb606ce"); //21
        aps.add("e804620b333"); //22

        List<ScanResult> result = new ArrayList<>();

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

        rssiValues.clear();
        table.clear();

        return rssiData;
    }
}
