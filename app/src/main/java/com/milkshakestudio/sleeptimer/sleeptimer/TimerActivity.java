package com.milkshakestudio.sleeptimer.sleeptimer;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.BidiFormatter;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.milkshakestudio.sleeptimer.sleeptimer.timer.FormattedTextUtils;
import com.milkshakestudio.sleeptimer.sleeptimer.timer.ThemeUtils;

import java.util.Arrays;
import java.util.Locale;

/**
 * Created by Syndkate on 2017-03-05.
 * --Abhishek--Milkshake Studios
 */

public class TimerActivity extends AppCompatActivity implements View.OnClickListener
    ,View.OnLongClickListener{


    private CharSequence mTimeTemplate;
    private final int[] mInput = { 0, 0, 0, 0, 0, 0 };
    private int mInputPointer = -1;

    private TextView mTimeView;
    private ImageButton mDeleteView;
    private View mDividerView;
    private TextView[] mDigitViews;
    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.timer_layout);

        final Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));


        final BidiFormatter bf = BidiFormatter.getInstance(false /* rtlContext */);
        final String hoursLabel = bf.unicodeWrap(this.getString(R.string.hours_label));
        final String minutesLabel = bf.unicodeWrap(this.getString(R.string.minutes_label));
        final String secondsLabel = bf.unicodeWrap(this.getString(R.string.seconds_label));



        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapButton();

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
        mDigitViews = new TextView[] {
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
                R.attr.colorControlNormal, new int[] { ~android.R.attr.state_enabled });
        ViewCompat.setBackgroundTintList(mDividerView, new ColorStateList(
                new int[][] { { android.R.attr.state_activated }, {} },
                new int[] { colorControlActivated, colorControlDisabled }));
        ViewCompat.setBackgroundTintMode(mDividerView, PorterDuff.Mode.SRC);

        for (final TextView digitView : mDigitViews) {
            final int digit = getDigitForId(digitView.getId());
            digitView.setText(getFormattedNumber(false,digit, 1));
            digitView.setOnClickListener(this);
        }

        mDeleteView.setOnClickListener(this);
        mDeleteView.setOnLongClickListener(this);


        updateTime();
        updateDeleteAndDivider();

    }

    private boolean isPlay = true;
    private void swapButton() {
        if(isPlay) {
            isPlay = false;
            floatingActionButton.setImageDrawable(getDrawable(R.drawable.ic_pause_white_24dp));
        }else {
            isPlay = true;
            floatingActionButton.setImageDrawable(getDrawable(R.drawable.ic_play_arrow_white_24dp));
        }
    }

    private void updateTime() {
        final int seconds = mInput[1] * 10 + mInput[0];
        final int minutes = mInput[3] * 10 + mInput[2];
        final int hours = mInput[5] * 10 + mInput[4];

        mTimeView.setText(TextUtils.expandTemplate(mTimeTemplate,
                getFormattedNumber(false,hours, 2),
                getFormattedNumber(false,minutes, 2),
                getFormattedNumber(false,seconds, 2)));

        final Resources r = getResources();
        mTimeView.setContentDescription(r.getString(R.string.timer_setup_description,
                r.getQuantityString(R.plurals.hours, hours, hours),
                r.getQuantityString(R.plurals.minutes, minutes, minutes),
                r.getQuantityString(R.plurals.seconds, seconds, seconds)));
    }

    private void updateDeleteAndDivider() {
        final boolean enabled = hasValidInput();
        mDeleteView.setEnabled(enabled);
        mDividerView.setActivated(enabled);
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

    private final SparseArray<SparseArray<String>> mNumberFormatCache = new SparseArray<>(3);

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
//            updateFab();
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
//            updateFab();
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
//            updateFab();
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

}
