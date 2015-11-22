package com.example.localize;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.TreeMap;

public class MapActivity extends Activity {

	ImageView dot;
	Thread t;
    MySQLiteDatabaseAdapter sqlHelper;
    RelativeLayout rl;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
        sqlHelper = new MySQLiteDatabaseAdapter(this);
		ImageView view = (ImageView) findViewById(R.id.iv_map);
		dot = (ImageView) findViewById(R.id.iv_dot);
        rl = (RelativeLayout) findViewById(R.id.rl_map);
		view.setScaleType(ScaleType.FIT_XY);
		DisplayMetrics metrics = this.getResources().getDisplayMetrics();
		int width = metrics.widthPixels;
		int height = metrics.heightPixels;
		//Log.d("MapActivity", "height " + String.valueOf(height));
		//Log.d("MapActivity", "width " + String.valueOf(width));
//		dot.setX(170);
//		dot.setY(-660);


		
//		// Runnable
//		Runnable runnable = new Runnable() {
//			@Override
//			public void run() {
//				moveTheDot();
//			}
//		};
//		new Thread(runnable).start();

		// view.setOnTouchListener(this);
	}

	public void onBtLocalizeClick(View v) {
        ArrayList<Integer> current = getRssiFromAP();
        TreeMap<String, ArrayList<Integer>> rssiDB = getRssiFromDB();

        TreeMap<String, ArrayList<Integer>> filteredDB;

        filteredDB = zeroMatch(current, rssiDB);

        String coordinates = "";
        int min = 99999;

        for(String cood: rssiDB.keySet()){
            printArrayList(current, "TBT");
            printArrayList(rssiDB.get(cood), cood);

            int diff = findMatch(current, rssiDB.get(cood));
            if(diff < min){
                min = diff;
                coordinates = cood;
                Log.d("MIN SET", String.valueOf(coordinates));

            }
        }
        Message.message(this, "MIN SET ====>" + String.valueOf(coordinates));
        Log.d("Match" , coordinates);
        setBlueDot(coordinates);

    }

    private TreeMap<String, ArrayList<Integer>> zeroMatch(ArrayList<Integer> current, TreeMap<String, ArrayList<Integer>> rssiDB) {

        TreeMap<String, ArrayList<Integer>> filteredDB = new TreeMap<>();
        for(String cood: rssiDB.keySet()){

            boolean flagMatch = checkZeroAlignment(current, rssiDB.get(cood));
            if(flagMatch){
                //printArrayList(rssiDB.get(cood), cood);
                filteredDB.put(cood, rssiDB.get(cood));
            }
        }
        return filteredDB;
    }

    private void printArrayList(ArrayList<Integer> integers, String cood){
        String out = "";
        for(Integer i : integers){
            out += i + "  ";
        }
        Log.d("ZERO MATCH", out + "  " + cood);
    }

    private boolean checkZeroAlignment(ArrayList<Integer> current, ArrayList<Integer> integers) {

        for(int i = 0; i<integers.size(); i++){
            int x = current.get(i);
            int y = integers.get(i);

            if(x == 0 || y == 0){
                if(x != y){
                    return false;
                }
            }
        }
        return true;
    }

    private void setBlueDot(String coordinates) {
        //ViewGroup.MarginLayoutParams absParams = (ViewGroup.MarginLayoutParams) dot.getLayoutParams();
        RelativeLayout.LayoutParams absParams = new RelativeLayout.LayoutParams(dot.getLayoutParams());
        String [] cood = coordinates.split(",");
        float x = Integer.parseInt(cood[0]);
        float y = Integer.parseInt(cood[1]);
        Log.d("Localize" , String.valueOf(x) + " " + String.valueOf(y));

//        if(y <= 171){
//            y = y - 171;
//            y = y * 4.f;
//             new_Y = Math.round(y);
//            absParams.topMargin = new_Y + 140;
//        }else{
//            y = y - 171;
//            y = y * -4.18f;
//            new_Y = Math.round(y);
//            absParams.topMargin = new_Y - 140;
//
//        }
        x = x * 9.7f + 170;
        y = y * 4.0f + 280;
        Log.d("Localize", String.valueOf(x) + " " + String.valueOf(y));
        final int new_X = Math.round(x);
        final int new_Y = Math.round(y);

        absParams.leftMargin = new_X;
        absParams.topMargin = 1830 - new_Y;
        Log.d("Localize", String.valueOf(new_X) + " " + String.valueOf(new_Y));
        rl.removeView(dot);
        rl.addView(dot, absParams);
        Message.message(this, new_X + "," + new_Y);
    }

    private int findMatch(ArrayList<Integer> current, ArrayList<Integer> integers) {
        int diff = 0;
        for(int i = 0; i<integers.size(); i++){

            diff += Math.abs(current.get(i) - integers.get(i));
        }
        return diff;
    }


    private ArrayList<Integer> getRssiFromAP() {
        TreeMap<String, String> rssiData = Utilities.getUtilities().getRssiData(this);
        String newValue = "";
        for (String val: rssiData.keySet()){
            newValue += rssiData.get(val) + ":";
        }

        ArrayList<Integer> rssiValues = new ArrayList<>();
        String rssiString = newValue.substring(0, newValue.lastIndexOf(":"));
        String [] rssiParts = rssiString.split(":");
        for(String rssi: rssiParts){
            rssiValues.add(Integer.parseInt(rssi));
        }
        return rssiValues;
    }

    private TreeMap<String, ArrayList<Integer>> getRssiFromDB() {
        String data = sqlHelper.getAllData();
        TreeMap<String, ArrayList<Integer>> rssiMap = new TreeMap<>();
        ArrayList<Integer> rssiValues = null;
        //Message.message(this, data);
        Log.d("Data", data);
        String []lines = data.split("[\\r\\n]+");
        Log.d("Lines" , lines.toString());
        for(String line: lines){
            //Log.d("Line" , line);
            String[] parts = line.split(":");
            String coordinates = parts[parts.length - 1];
            //Log.d("Coordinates" , coordinates);
            rssiValues = new ArrayList<>();
            String rssiString = line.substring(0, line.lastIndexOf(":"));
            String[] rssiParts = rssiString.split(":");
            for (String rssi : rssiParts) {
                rssiValues.add(Integer.parseInt(rssi));
            }
            Log.d("MapActivity" , coordinates + "-->" + rssiString);
            rssiMap.put(coordinates, rssiValues);
        }
        return rssiMap;
    }


    protected void moveTheDot(int x , int y){
        dot.setX(x);
        dot.setY(y);
    }

    protected void moveTheDot() {
        dot.setX(978);
        dot.setY(-5);
//		// TODO Auto-generated method stub
//		Log.d("MapActivity", "Idharhun");
//		int posx = 350;
//		int posy = 450;
//		while (posy<1570) {
//
//			dot.setX(posx);
//			dot.setY(posy);
//			try {
//				Thread.sleep(50);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			Log.d("MapActivity", String.valueOf(posy));
//			posy += 5;
//		}
//		while(posx < 1050 ){
//			dot.setX(posx);
//			dot.setY(posy);
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			Log.d("MapActivity", String.valueOf(posx));
//			posx += 10;
//		}
//		while(posy > 570 ){
//			dot.setX(posx);
//			dot.setY(posy);
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			Log.d("MapActivity", String.valueOf(posy));
//			posy -= 10;
//		}
//		while(posx > 800 ){
//			dot.setX(posx);
//			dot.setY(posy);
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			Log.d("MapActivity", String.valueOf(posx));
//			posx -= 10;
//		}
	}
}