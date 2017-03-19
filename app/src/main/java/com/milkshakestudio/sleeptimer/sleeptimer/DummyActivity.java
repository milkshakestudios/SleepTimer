package com.milkshakestudio.sleeptimer.sleeptimer;

import android.animation.Animator;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

/**
 * Created by Syndkate on 2017-03-09.
 * --Abhishek--Milkshake Studios
 */

public class DummyActivity extends AppCompatActivity {


    private TextView content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dummy_layout);
        content = (TextView) findViewById(R.id.reveal_content);

        /*
        We do post cause we need height and width
        Also it will be attached when runnable is executed
        */
        content.post(new Runnable() {
            @Override
            public void run() {
                /*
            	why am I handling older android versions here?
            	I don't know. Maybe because I'm douche.
            	*/
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    /*
                    We've passed the x and y coordinate
                    from previous activity, to reveal
                    layout from exact place where it was tapped
                    */
                    int x = getIntent().getIntExtra("x", 0);
                    int y = getIntent().getIntExtra("y", 0);
                    Animator animator = createRevealAnimator(false, x, y);
                    animator.start();
                }
                content.setVisibility(View.VISIBLE);
            }
        });

        content.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Animator animator =
                            createRevealAnimator(
                                    true,
                                    (int) event.getX(),
                                    (int) event.getY());
                    animator.start();
                } else {
                    finish();
                }
                return false;
            }
        });
    }



    private Animator createRevealAnimator(boolean reversed, int x, int y) {

        float hypot =
                (float) Math.hypot(content.getHeight(), content.getWidth());
        float startRadius = reversed ? hypot : 0;
        float endRadius = reversed ? 0 : hypot;
        Animator animator = ViewAnimationUtils.createCircularReveal(
                content, x, y, //center position of the animation
                startRadius,
                endRadius);
        animator.setDuration(800);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        if (reversed)
            animator.addListener(animatorListener);
        return animator;
    }

    //we add listener if it's revered to handle activity finish
    private Animator.AnimatorListener animatorListener =
            new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    //to remove default lollipop animation
                    content.setVisibility(View.INVISIBLE);
                    finish();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            };


}
