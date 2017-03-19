package com.milkshakestudio.sleeptimer.sleeptimer;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by Syndkate on 2017-03-11.
 * --Abhishek--Milkshake Studios
 */

public class TestBench extends AppCompatActivity {


    private TextView mAutofitOutput;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);

//        mAutofitOutput = (TextView)findViewById(R.id.output_autofit);
//
//        ((EditText)findViewById(R.id.input)).addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
//                // do nothing
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
//                mAutofitOutput.setText(charSequence);
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                // do nothing
//            }
//        });


        findViewById(R.id.background).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
                ObjectAnimator animation = ObjectAnimator.ofInt (progressBar, "progress", 500, 0);
                animation.setDuration (5000); //in milliseconds
                animation.setInterpolator (new LinearInterpolator());
                animation.start();
            }
        });

    }
}
