package com.milkshakestudio.sleeptimer.sleeptimer;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {


    private static final int MAXHOUR_INDAY = 23;
    private static String HOUR_KEY = "hourKey";
    int incrementalTime;
    ValueAnimator animator;
    private int timeFromPrefs;
    private int timeCurrent;
    private ArrayList<Integer> arrayList = new ArrayList<Integer>();

    public static int[] convertIntegers(ArrayList<Integer> integers)
    {
        int[] ret = new int[integers.size()];
        for (int i=0; i < ret.length; i++)
        {
            ret[i] = integers.get(i).intValue();
        }
        return ret;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timer_layout);

//        timeFromPrefs = loadTimeFromPreferences();
//        timeCurrent = fetchCurrentTime();

//        TextView timeField = (TextView) findViewById(R.id.timeField);
//        timeField.setText(getString(R.string.currentHour,String.valueOf(timeCurrent)));

//        AppCompatButton startButton = (AppCompatButton) findViewById(R.id.start_stop_btn);
//        startButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(animator!=null)
//                    if(!animator.isRunning()) {
//                        animator.start();
//                    }
//            }
//        });


//        setUpScrollColorsAndAnimate();

    }

    private void setUpScrollColorsAndAnimate() {

        int colorBasedOnLastLoginTime = getColorValueBasedOnHour(timeFromPrefs);

        final RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.activity_main);
        final Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        relativeLayout.setBackgroundColor(colorBasedOnLastLoginTime);
        window.setStatusBarColor(colorBasedOnLastLoginTime);

        if(timeFromPrefs == timeCurrent){
            return;
        }

        incrementalTime = timeFromPrefs;

        calc(incrementalTime);


        ArgbEvaluator evaluator = new ArgbEvaluator();
        animator = new ValueAnimator();
        animator.setIntValues(convertIntegers(arrayList));
        animator.setEvaluator(evaluator);
        animator.setDuration((arrayList.size()-1)*1000);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                relativeLayout.setBackgroundColor((int) animation.getAnimatedValue());
                window.setStatusBarColor((int) animation.getAnimatedValue());
            }
        });
    }

    public void calc(int copy){
        if(timeCurrent == copy){
            arrayList.add(getColorValueBasedOnHour(copy));
//            arrayList.add(copy);
            return;
        }
        if(copy < timeCurrent){
            arrayList.add(getColorValueBasedOnHour(copy));
//            arrayList.add(copy);
            copy+=3;
            if(copy>timeCurrent) {

                if(copy>MAXHOUR_INDAY){
                    copy = copy-MAXHOUR_INDAY-1;
                }
                if (withinRange(copy)) {
//                    arrayList.add(copy);
                    arrayList.add(getColorValueBasedOnHour(copy));
                }else {
                    return;
                }
            }else {
                calc(copy);
            }
        } else if (timeCurrent < copy){
//            arrayList.add(copy);
            arrayList.add(getColorValueBasedOnHour(copy));
            int diffCopy = 24 - copy;
            int diffTimeCurrent = timeCurrent;
            int sumDiff = diffCopy + diffTimeCurrent;
            for(int i =0;i<sumDiff/3;i++){
                copy+=3;
                if(copy>MAXHOUR_INDAY){
                    copy=copy-MAXHOUR_INDAY-1;
                }
//                arrayList.add(copy);
                arrayList.add(getColorValueBasedOnHour(copy));
            }
        }
    }

    private boolean withinRange(int copy) {
        double a = Math.floor((double)timeCurrent/3)*3;
        if(copy >= a && copy <= a+2){
            return true;
        }else{
            return false;
        }
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


    private int getColorValueBasedOnHour(int timeCurrent) {
        int colorCurrent;

        if(timeCurrent == -1){
            colorCurrent = getResources().getColor(R.color.colorPrimary);
        }else if(timeCurrent >= 0 && timeCurrent <= 2){     //12am  to 3am
            colorCurrent = getResources().getColor(R.color.colorVeryEarlyMorning);
        }else if(timeCurrent >= 3 && timeCurrent <= 5){     //3am   to 6am
            colorCurrent = getResources().getColor(R.color.colorEarlyMorning);
        }else if(timeCurrent >= 6 && timeCurrent <= 8){     //6am   to 9am
            colorCurrent = getResources().getColor(R.color.colorMorning);
        }else if(timeCurrent >= 9 && timeCurrent <= 11){    //9am   to 12pm
            colorCurrent = getResources().getColor(R.color.colorMidMorning);
        }else if(timeCurrent >= 12 && timeCurrent <= 14){    //12pm   to 3pm
            colorCurrent = getResources().getColor(R.color.colorAfternoon);
        }else if(timeCurrent >= 15 && timeCurrent <= 17){    //3pm   to 6pm
            colorCurrent = getResources().getColor(R.color.colorEarlyEvening);
        }else if(timeCurrent >= 18 && timeCurrent <= 20){    //6pm   to 9pm
            colorCurrent = getResources().getColor(R.color.colorEvening);
        }else if(timeCurrent >= 21 && timeCurrent <= 23){    //9pm   to 12am
            colorCurrent = getResources().getColor(R.color.colorMidnight);
        }else if(timeCurrent == 24) {
            colorCurrent = getResources().getColor(R.color.colorMidnight);
        }else{
            colorCurrent = getResources().getColor(R.color.colorPrimary);
        }
        return colorCurrent;
    }


    private int loadTimeFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("sleepTimer",MODE_PRIVATE);
        timeFromPrefs = sharedPreferences.getInt(HOUR_KEY,-1);
//        if(timeFromPrefs==-1){
//            return fetchCurrentTime();
//        }
        return timeFromPrefs;
    }
    private int fetchCurrentTime() {
        int currentTime = Calendar.getInstance().getTime().getHours();
        SharedPreferences sharedPreferences = getSharedPreferences("sleepTimer",MODE_PRIVATE);
        sharedPreferences.edit().putInt(HOUR_KEY,currentTime).apply();
        return currentTime;
    }

    private void runTest(int i, int i1) {
        timeCurrent = i1;
        calc(i);

        Log.v("abhi","timeFromPrefs = " + i + " | timeCurrent = " + i1);
        for(Integer a: arrayList){
            Log.d("abhi", "onCreate: + x = "+ a);
        }
        Log.d("abhi", "-----------");
        arrayList.clear();
//        setUpScrollColorsAndAnimate();
    }

}
