package com.milkshakestudio.sleeptimer.sleeptimer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Syndkate on 2017-02-27.
 * --Abhishek--Milkshake Studios
 */

public class SplashScreen extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("myname","abhi");
        startActivity(intent);
        finish();
    }
}
