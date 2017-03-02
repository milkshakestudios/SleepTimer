package com.milkshakestudio.sleeptimer.sleeptimer;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private int timeHours;
    private int timeFromPrefs;

    private static String HOUR_KEY = "hourKey";
    private int a = 1;

    private ArrayList<Integer> colorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        timeHours = Calendar.getInstance().getTime().getHours();


        SharedPreferences sharedPreferences = getSharedPreferences("sleepTimer",MODE_PRIVATE);


        timeFromPrefs = sharedPreferences.getInt(HOUR_KEY,-1);

        sharedPreferences.edit().putInt(HOUR_KEY,timeHours).apply();

        TextView timeField = (TextView) findViewById(R.id.timeField);


        timeField.setText(""+ timeHours%12);
//        timeField.setText(Calendar.getInstance().getTime().getHours());




        AppCompatButton startButton = (AppCompatButton) findViewById(R.id.start_stop_btn);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeState();
            }
        });

//        SeekArc arc = (SeekArc) findViewById(R.id.seekArc);
//        arc.setStartAngle(0);
//        arc.setSweepAngle(360);
//        NumberPicker numberPicker = (NumberPicker) findViewById(R.id.number_picker);
//        numberPicker.setMinValue(0);
//        numberPicker.setMaxValue(60);
//        numberPicker.setWrapSelectorWheel(true);

        setupColor();

    }

    private void setupColor() {
        colorList = new ArrayList<Integer>();

        colorList.add(getResources().getColor(R.color.colorVeryEarlyMorning));
    }


    private void changeState() {

        int colorFrom = setStartColor();

        int colorTo = setEndColor();

        final RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.activity_main);
        final Window window = this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);


        ValueAnimator colorAnimation = new ValueAnimator();
        colorAnimation.setIntValues(colorFrom,colorTo);
        colorAnimation.setEvaluator(new ArgbEvaluator());

        colorAnimation.setDuration(2500); // milliseconds

        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                relativeLayout.setBackgroundColor((int) animator.getAnimatedValue());
                window.setStatusBarColor((int) animator.getAnimatedValue());
            }

        });
        colorAnimation.start();

    }

    private int setStartColor() {
        int colorStart = getResources().getColor(R.color.colorPrimaryDark);

        if(timeFromPrefs == -1){
        }else if(timeFromPrefs >= 0 && timeFromPrefs <= 2){     //12am  to 3am
            colorStart = getResources().getColor(R.color.colorVeryEarlyMorning);
        }else if(timeFromPrefs >= 3 && timeFromPrefs <= 5){     //3am   to 6am
            colorStart = getResources().getColor(R.color.colorEarlyMorning);
        }else if(timeFromPrefs >= 6 && timeFromPrefs <= 8){     //6am   to 9am
            colorStart = getResources().getColor(R.color.colorMorning);
        }else if(timeFromPrefs >= 9 && timeFromPrefs <= 11){    //9am   to 12pm
            colorStart = getResources().getColor(R.color.colorMidMorning);
        }else if(timeFromPrefs >= 12 && timeFromPrefs <= 14){    //12pm   to 3pm
            colorStart = getResources().getColor(R.color.colorAfternoon);
        }else if(timeFromPrefs >= 15 && timeFromPrefs <= 17){    //3pm   to 6pm
            colorStart = getResources().getColor(R.color.colorEarlyEvening);
        }else if(timeFromPrefs >= 18 && timeFromPrefs <= 20){    //6pm   to 9pm
            colorStart = getResources().getColor(R.color.colorEvening);
        }else if(timeFromPrefs >= 21 && timeFromPrefs <= 23){    //9pm   to 12am
            colorStart = getResources().getColor(R.color.colorMidnight);
        }else{
            colorStart = getResources().getColor(R.color.colorPrimaryDark);
        }
        return colorStart;
    }

    private int setEndColor() {
        int colorEnd = getResources().getColor(R.color.colorMidnight);

        if(timeHours == -1){
        }else if(timeHours >= 0 && timeHours <= 2){     //12am  to 3am
            colorEnd = getResources().getColor(R.color.colorVeryEarlyMorning);
        }else if(timeHours >= 3 && timeHours <= 5){     //3am   to 6am
            colorEnd = getResources().getColor(R.color.colorEarlyMorning);
        }else if(timeHours >= 6 && timeHours <= 8){     //6am   to 9am
            colorEnd = getResources().getColor(R.color.colorMorning);
        }else if(timeHours >= 9 && timeHours <= 11){    //9am   to 12pm
            colorEnd = getResources().getColor(R.color.colorMidMorning);
        }else if(timeHours >= 12 && timeHours <= 14){    //12pm   to 3pm
            colorEnd = getResources().getColor(R.color.colorAfternoon);
        }else if(timeHours >= 15 && timeHours <= 17){    //3pm   to 6pm
            colorEnd = getResources().getColor(R.color.colorEarlyEvening);
        }else if(timeHours >= 18 && timeHours <= 20){    //6pm   to 9pm
            colorEnd = getResources().getColor(R.color.colorEvening);
        }else if(timeHours >= 21 && timeHours <= 23){    //9pm   to 12am
            colorEnd = getResources().getColor(R.color.colorMidnight);
        }else{
            colorEnd = getResources().getColor(R.color.colorPrimaryDark);
        }
        return colorEnd;
    }


//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus) {
//            getWindow().getDecorView().setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
//    }

}
