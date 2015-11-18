package com.example.localize;

import android.app.Activity;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import static java.lang.Math.sqrt;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import java.util.ArrayList;

public class MapActivity extends Activity {

	ImageView dot;
	Thread t;
    MySQLiteDatabaseAdapter sqlHelper;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
        sqlHelper = new MySQLiteDatabaseAdapter(this);
		ImageView view = (ImageView) findViewById(R.id.iv_map);
		dot = (ImageView) findViewById(R.id.iv_dot);
		view.setScaleType(ScaleType.FIT_XY);
		DisplayMetrics metrics = this.getResources().getDisplayMetrics();
		int width = metrics.widthPixels;
		int height = metrics.heightPixels;
		//Log.d("MapActivity", "height " + String.valueOf(height));
		//Log.d("MapActivity", "width " + String.valueOf(width));
		dot.setX(0);
		dot.setY(-920);


		
		// Runnable
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
        String data = sqlHelper.getAllData();
        Message.message(this, data);
        Log.d("Data", data);
        String []lines = data.split("[\\r\\n]+");
        Log.d("Lines" , lines.toString());
        for(String line: lines){
            Log.d("Line" , line);
            String[] parts = line.split(":");
            String coordinates = parts[parts.length - 1];
            Log.d("Coordinates" , coordinates);
            ArrayList<String> rssiValues = new ArrayList<>();
            String rssiString = line.substring(0, line.lastIndexOf(":"));
            String[] rssiParts = rssiString.split(":");
            for (String rssi : rssiParts) {
                rssiValues.add(rssi);
            }
            Log.d("MapActivity" , coordinates + "-->" + rssiString);
            rssiValues.clear();
        }



    }

	protected void moveTheDot() {
		// TODO Auto-generated method stub
		Log.d("MapActivity", "Idharhun");
		int posx = 350;
		int posy = 450;
		while (posy<1570) {

			dot.setX(posx);
			dot.setY(posy);
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d("MapActivity", String.valueOf(posy));
			posy += 5;
		}
		while(posx < 1050 ){
			dot.setX(posx);
			dot.setY(posy);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d("MapActivity", String.valueOf(posx));
			posx += 10;
		}
		while(posy > 570 ){
			dot.setX(posx);
			dot.setY(posy);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d("MapActivity", String.valueOf(posy));
			posy -= 10;
		}
		while(posx > 800 ){
			dot.setX(posx);
			dot.setY(posy);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d("MapActivity", String.valueOf(posx));
			posx -= 10;
		}
	}
}