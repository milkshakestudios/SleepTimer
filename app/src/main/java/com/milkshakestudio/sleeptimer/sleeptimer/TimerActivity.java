package com.milkshakestudio.sleeptimer.sleeptimer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.BidiFormatter;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.transition.Transition;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.milkshakestudio.sleeptimer.sleeptimer.timer.FormattedTextUtils;
import com.milkshakestudio.sleeptimer.sleeptimer.timer.ThemeUtils;

import java.util.Arrays;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;
import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK;

/**
 * Created by Syndkate on 2017-03-05.
 * --Abhishek--Milkshake Studios
 */

public class TimerActivity extends AppCompatActivity implements View.OnClickListener
        , View.OnLongClickListener {


    private final int[] mInput = {0, 0, 0, 0, 0, 0};
    private final long HOUR_TO_MILLISECONDS = 60 * 60 * 1000;
    private final long MINUTE_TO_MILLISECONDS = 60 * 1000;
    private final long SECOND_TO_MILLISECONDS = 1 * 1000;
    private final SparseArray<SparseArray<String>> mNumberFormatCache = new SparseArray<>(3);
    long startTime;
    long milliSecondsLeft;
    StringBuilder sb = new StringBuilder();
    private ObjectAnimator animation;
    ProgressBar progressBar;
    Boolean isActivityRunning = false;
    Timer timer;
    TimerTask timerTask;
    AudioManager.OnAudioFocusChangeListener afChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                public void onAudioFocusChange(int focusChange) {
                    if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                        // Permanent loss of audio focus
                        // Pause playback immediately
                    } else if (focusChange == AUDIOFOCUS_LOSS_TRANSIENT) {
                        // Pause playback
                    } else if (focusChange == AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                        // Lower the volume, keep playing
                    } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                        // Your app has been granted audio focus again
                        // Raise volume to normal, restart playback if necessary
                    }
                }
            };
    ImageButton deleteButton;
    private CharSequence mTimeTemplate;
    private int mInputPointer = -1;
    private TextView mTimeView;
    private ImageButton mDeleteView;
    private View mDividerView;
    private TextView[] mDigitViews;
    private FloatingActionButton floatingActionButton;
    private Handler updateLabel = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (isActivityRunning) {
                updateTimer(milliSecondsLeft);

            }else{
                startTime = SystemClock.elapsedRealtime();

            }

        }
    };
    private Transition.TransitionListener mEnterTransitionListener;

    void enterReveal() {
        // previously invisible view
        final View myView = findViewById(R.id.my_view);
        final View timerView = findViewById(R.id.timer_view);
        final View button = findViewById(R.id.timer_delete);

        // get the center for the clipping circle
        int cx = myView.getMeasuredWidth() / 2;
        int cy = myView.getMeasuredHeight() / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(myView.getWidth(), myView.getHeight()) / 2;

        // create the animator for this view (the start radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);

        // make the view visible and start the animation
        myView.setVisibility(View.VISIBLE);
        timerView.setVisibility(View.GONE);
        button.setVisibility(View.INVISIBLE);

        floatingActionButton.setVisibility(View.INVISIBLE);
        if(!isActivityRunning){
            floatingActionButton.setImageDrawable(getDrawable(R.drawable.ic_play_arrow_white_24dp));
        }
        delete();
        delete();
        delete();
        delete();
        delete();
        delete();

        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.start();
    }

    void exitReveal() {
        // previously visible view
        final View myView = findViewById(R.id.my_view);
        final View timerView = findViewById(R.id.timer_view);

        // get the center for the clipping circle
        int cx = myView.getMeasuredWidth() / 2;
        int cy = myView.getMeasuredHeight() / 2;

        // get the initial radius for the clipping circle
        int initialRadius = myView.getWidth() / 2;

        // create the animation (the final radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, initialRadius, 0);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                myView.setVisibility(View.GONE);
                timerView.setVisibility(View.VISIBLE);
            }
        });

        // start the animation
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.start();
    }

    private void changeVisibility() {
        if (findViewById(R.id.my_view).getVisibility() == View.VISIBLE) {
            floatingActionButton.setImageDrawable(getDrawable(R.drawable.ic_pause_white_24dp));
            ((ImageButton) findViewById(R.id.timer_delete)).setVisibility(View.VISIBLE);
            exitReveal();
            startTimer();
        } else {
            if (isActivityRunning) {
                pauseTimer();
            } else {
                resumeTimer();
            }
        }
    }

    private void resumeTimer() {
        isActivityRunning = true;
        if(animation.isPaused()) {
            findViewById(R.id.add_time_button).setVisibility(View.VISIBLE);
            findViewById(R.id.timer_reset).setVisibility(View.INVISIBLE);
            animation.resume();
            floatingActionButton.setImageDrawable(getDrawable(R.drawable.ic_pause_white_24dp));
        }
    }

    private void pauseTimer() {
        isActivityRunning = false;
        findViewById(R.id.add_time_button).setVisibility(View.INVISIBLE);
        findViewById(R.id.timer_reset).setVisibility(View.VISIBLE);
        if(animation.isRunning()){
            animation.pause();
            floatingActionButton.setImageDrawable(getDrawable(R.drawable.ic_play_arrow_white_24dp));
        }
    }

    private void startTimer() {
        final int seconds = mInput[1] * 10 + mInput[0];
        final int minutes = mInput[3] * 10 + mInput[2];
        final int hours = mInput[5] * 10 + mInput[4];

        findViewById(R.id.add_time_button).setVisibility(View.VISIBLE);
        findViewById(R.id.timer_reset).setVisibility(View.INVISIBLE);
        milliSecondsLeft = seconds * SECOND_TO_MILLISECONDS + minutes * MINUTE_TO_MILLISECONDS + hours * HOUR_TO_MILLISECONDS;

        if(progressBar == null && animation == null) {
            progressBar = (ProgressBar) findViewById(R.id.progressBar);
            animation = ObjectAnimator.ofInt(progressBar, "progress", 5000, 0);
            animation.setDuration(milliSecondsLeft); //in milliseconds
            animation.setInterpolator(new LinearInterpolator());
            animation.start();
        }

        startTime = SystemClock.elapsedRealtime();
        isActivityRunning = true;
        reScheduleTimer();
    }

    private void updateTimer(long milliSecondsLeft) {
        int sec = (int) (milliSecondsLeft / 1000) % 60;
        int min = (int) ((milliSecondsLeft / (1000 * 60)) % 60);
        int hr = (int) ((milliSecondsLeft / (1000 * 60 * 60)));
        if (hr == 0) {

        } else {
            sb.append(hr).append(" ");
        }
        if (min == 0) {
            sb.append("0").append(" ");
        } else {
            sb.append(min).append(" ");
        }
        if (sec < 10) {
            sb.append("0").append(sec);
        } else {
            sb.append(sec);
        }

        ((TextView) findViewById(R.id.output_autofit)).setText(sb.toString());
        sb.setLength(0);
        final long endTime = SystemClock.elapsedRealtime();
        final long timeDiff = endTime - startTime;
        this.milliSecondsLeft = milliSecondsLeft - timeDiff;
        startTime = endTime;
        if (this.milliSecondsLeft <= 0) {
            isActivityRunning = false;
            progressBar.setProgress(5000);
            AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

            if (audioManager.isMusicActive()) {
                int result = audioManager.requestAudioFocus(afChangeListener,
                        // Use the music stream.
                        AudioManager.STREAM_MUSIC,
                        // Request permanent focus.
                        AudioManager.AUDIOFOCUS_GAIN);
                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    // Start playback
                }
            }
//            Notification.Builder mBuilder =
//                    new Notification.Builder(this)
//                            .setSmallIcon(R.drawable.ic_play_arrow_white_24dp)
//                            .setContentTitle("Beauty Sleep Timer")
//                            .setContentText("Stopping music now");
//            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            manager.notify(123, mBuilder.build());
        }


    }

    public void reScheduleTimer() {
        timer = new Timer();
        timerTask = new myTimerTask();
        timer.schedule(timerTask, 0, 1000);
    }

    private void updateTime() {
        final int seconds = mInput[1] * 10 + mInput[0];
        final int minutes = mInput[3] * 10 + mInput[2];
        final int hours = mInput[5] * 10 + mInput[4];

        mTimeView.setText(TextUtils.expandTemplate(mTimeTemplate,
                getFormattedNumber(false, hours, 2),
                getFormattedNumber(false, minutes, 2),
                getFormattedNumber(false, seconds, 2)));

        final Resources r = getResources();
        mTimeView.setContentDescription(r.getString(R.string.timer_setup_description,
                r.getQuantityString(R.plurals.hours, hours, hours),
                r.getQuantityString(R.plurals.minutes, minutes, minutes),
                r.getQuantityString(R.plurals.seconds, seconds, seconds)));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.timer_layout);

        final Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));


//        LinearLayout parentLayout = (LinearLayout) findViewById(R.id.parent_view);
//        parentLayout.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                Intent i = new Intent(TimerActivity.this, DummyActivity.class);
//                i.putExtra("x", (int) event.getX()); //We'll cover why we put x and y into the Intent
//                i.putExtra("y", (int) event.getY());
//                startActivity(i);
//                return false;
//            }
//        });

        final BidiFormatter bf = BidiFormatter.getInstance(false /* rtlContext */);
        final String hoursLabel = bf.unicodeWrap(this.getString(R.string.hours_label));
        final String minutesLabel = bf.unicodeWrap(this.getString(R.string.minutes_label));
        final String secondsLabel = bf.unicodeWrap(this.getString(R.string.seconds_label));


        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeVisibility();
            }
        });

        deleteButton = (ImageButton) findViewById(R.id.timer_delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pauseTimer();
                animation = null;
                progressBar = null;
                isActivityRunning = false;
                enterReveal();
            }
        });


        mTimeTemplate = TextUtils.expandTemplate("^1^4 ^2^5 ^3^6",
                bf.unicodeWrap("^1"),
                bf.unicodeWrap("^2"),
                bf.unicodeWrap("^3"),
                FormattedTextUtils.formatText(hoursLabel, new RelativeSizeSpan(0.5f)),
                FormattedTextUtils.formatText(minutesLabel, new RelativeSizeSpan(0.5f)),
                FormattedTextUtils.formatText(secondsLabel, new RelativeSizeSpan(0.5f)));


        mTimeView = (TextView) findViewById(R.id.timer_setup_time);
        mDeleteView = (ImageButton) findViewById(R.id.timer_setup_delete);
        mDividerView = findViewById(R.id.timer_setup_divider);
        mDigitViews = new TextView[]{
                (TextView) findViewById(R.id.timer_setup_digit_0),
                (TextView) findViewById(R.id.timer_setup_digit_1),
                (TextView) findViewById(R.id.timer_setup_digit_2),
                (TextView) findViewById(R.id.timer_setup_digit_3),
                (TextView) findViewById(R.id.timer_setup_digit_4),
                (TextView) findViewById(R.id.timer_setup_digit_5),
                (TextView) findViewById(R.id.timer_setup_digit_6),
                (TextView) findViewById(R.id.timer_setup_digit_7),
                (TextView) findViewById(R.id.timer_setup_digit_8),
                (TextView) findViewById(R.id.timer_setup_digit_9),
        };

        final Context dividerContext = mDividerView.getContext();

        final int colorControlActivated = ThemeUtils.resolveColor(dividerContext,
                R.attr.colorControlActivated);
        final int colorControlDisabled = ThemeUtils.resolveColor(dividerContext,
                R.attr.colorControlNormal, new int[]{~android.R.attr.state_enabled});
        ViewCompat.setBackgroundTintList(mDividerView, new ColorStateList(
                new int[][]{{android.R.attr.state_activated}, {}},
                new int[]{colorControlActivated, colorControlDisabled}));
        ViewCompat.setBackgroundTintMode(mDividerView, PorterDuff.Mode.SRC);

        for (final TextView digitView : mDigitViews) {
            final int digit = getDigitForId(digitView.getId());
            digitView.setText(getFormattedNumber(false, digit, 1));
            digitView.setOnClickListener(this);
        }

        mDeleteView.setOnClickListener(this);
        mDeleteView.setOnLongClickListener(this);


        mEnterTransitionListener = new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {
                updateFab();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        };
        getWindow().getEnterTransition().addListener(mEnterTransitionListener);
        updateTime();
        updateDeleteAndDivider();
    }

    private void updateDeleteAndDivider() {
        final boolean enabled = hasValidInput();
        mDeleteView.setEnabled(enabled);
        mDividerView.setActivated(enabled);
    }


    private void updateFab() {
        final boolean enabled = hasValidInput();
//        mDeleteView.setEnabled(enabled);
//        mDividerView.setActivated(enabled);
        if (enabled && findViewById(R.id.fab).getVisibility() == View.INVISIBLE) {
            enterFAB();
        } else if (!enabled && findViewById(R.id.fab).getVisibility() == View.VISIBLE) {
            exitFAB();
        }
    }

    void enterFAB() {
        // previously invisible view
        final View myView = findViewById(R.id.fab);

        // get the center for the clipping circle
        int cx = myView.getMeasuredWidth() / 2;
        int cy = myView.getMeasuredHeight() / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(myView.getWidth(), myView.getHeight()) / 2;

        // create the animator for this view (the start radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);

        // make the view visible and start the animation
        myView.setVisibility(View.VISIBLE);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.start();
    }

    void exitFAB() {
        // previously visible view
        final View myView = findViewById(R.id.fab);

        // get the center for the clipping circle
        int cx = myView.getMeasuredWidth() / 2;
        int cy = myView.getMeasuredHeight() / 2;

        // get the initial radius for the clipping circle
        int initialRadius = myView.getWidth() / 2;

        // create the animation (the final radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, initialRadius, 0);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                myView.setVisibility(View.INVISIBLE);
            }
        });
        // start the animation
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.start();
    }


    public boolean hasValidInput() {
        return mInputPointer != -1;
    }


    private int getDigitForId(@IdRes int id) {
        switch (id) {
            case R.id.timer_setup_digit_0:
                return 0;
            case R.id.timer_setup_digit_1:
                return 1;
            case R.id.timer_setup_digit_2:
                return 2;
            case R.id.timer_setup_digit_3:
                return 3;
            case R.id.timer_setup_digit_4:
                return 4;
            case R.id.timer_setup_digit_5:
                return 5;
            case R.id.timer_setup_digit_6:
                return 6;
            case R.id.timer_setup_digit_7:
                return 7;
            case R.id.timer_setup_digit_8:
                return 8;
            case R.id.timer_setup_digit_9:
                return 9;
        }
        throw new IllegalArgumentException("Invalid id: " + id);
    }

    String getFormattedNumber(boolean negative, int value, int length) {
        if (value < 0) {
            throw new IllegalArgumentException("value may not be negative: " + value);
        }

        // Look up the value cache using the length; -ve and +ve values are cached separately.
        final int lengthCacheKey = negative ? -length : length;
        SparseArray<String> valueCache = mNumberFormatCache.get(lengthCacheKey);
        if (valueCache == null) {
            valueCache = new SparseArray<>((int) Math.pow(10, length));
            mNumberFormatCache.put(lengthCacheKey, valueCache);
        }

        // Look up the cached formatted value using the value.
        String formatted = valueCache.get(value);
        if (formatted == null) {
            final String sign = negative ? "−" : "";
            formatted = String.format(Locale.getDefault(), sign + "%0" + length + "d", value);
            valueCache.put(value, formatted);
        }

        return formatted;
    }

    String getFormattedNumber(int value) {
        final int length = value == 0 ? 1 : ((int) Math.log10(value) + 1);
        return getFormattedNumber(false, value, length);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        View view = null;
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            view = mDeleteView;
        } else if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
            view = mDigitViews[keyCode - KeyEvent.KEYCODE_0];
        }

        if (view != null) {
            final boolean result = view.performClick();
            if (result && hasValidInput()) {
//                mFabContainer.updateFab(FAB_REQUEST_FOCUS);
            }
            return result;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        if (view == mDeleteView) {
            delete();
        } else {
            append(getDigitForId(view.getId()));
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if (view == mDeleteView) {
            reset();
            updateFab();
            return true;
        }
        return false;
    }

    private void append(int digit) {
        if (digit < 0 || digit > 9) {
            throw new IllegalArgumentException("Invalid digit: " + digit);
        }

        // Pressing "0" as the first digit does nothing.
        if (mInputPointer == -1 && digit == 0) {
            return;
        }

        // No space for more digits, so ignore input.
        if (mInputPointer == mInput.length - 1) {
            return;
        }

        // Append the new digit.
        System.arraycopy(mInput, 0, mInput, 1, mInputPointer + 1);
        mInput[0] = digit;
        mInputPointer++;
        updateTime();

        // Update TalkBack to read the number being deleted.
        mDeleteView.setContentDescription(this.getString(
                R.string.timer_descriptive_delete,
                getFormattedNumber(digit)));

        // Update the fab, delete, and divider when we have valid input.
        if (mInputPointer == 0) {
            updateFab();
            updateDeleteAndDivider();
        }
    }

    private void delete() {
        // Nothing exists to delete so return.
        if (mInputPointer < 0) {
            return;
        }

        System.arraycopy(mInput, 1, mInput, 0, mInputPointer);
        mInput[mInputPointer] = 0;
        mInputPointer--;
        updateTime();

        // Update TalkBack to read the number being deleted or its original description.
        if (mInputPointer >= 0) {
            mDeleteView.setContentDescription(this.getString(
                    R.string.timer_descriptive_delete,
                    getFormattedNumber(mInput[0])));
        } else {
            mDeleteView.setContentDescription(this.getString(R.string.timer_delete));
        }

        // Update the fab, delete, and divider when we no longer have valid input.
        if (mInputPointer == -1) {
            updateFab();
            updateDeleteAndDivider();
        }
    }

    public void reset() {
        if (mInputPointer != -1) {
            Arrays.fill(mInput, 0);
            mInputPointer = -1;
            updateTime();
            updateDeleteAndDivider();
        }
    }

    private class myTimerTask extends TimerTask {
        @Override
        public void run() {
            updateLabel.sendEmptyMessage(0);
        }
    }

}
