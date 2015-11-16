package com.example.localize;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Chiran on 11/15/15.
 */
public class Message {

    public static void message(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
