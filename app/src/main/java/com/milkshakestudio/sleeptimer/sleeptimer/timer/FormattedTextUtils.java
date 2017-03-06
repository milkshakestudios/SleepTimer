package com.milkshakestudio.sleeptimer.sleeptimer.timer;

import android.text.Spannable;
import android.text.SpannableString;

/**
 * Created by Syndkate on 2017-03-05.
 * --Abhishek--Milkshake Studios
 */

public class FormattedTextUtils {

    private FormattedTextUtils() {
    }

    /**
     * Applies a span over the length of the given text.
     *
     * @param text the {@link CharSequence} to be formatted
     * @param span the span to apply
     * @return the text with the span applied
     */
    public static CharSequence formatText(CharSequence text, Object span) {
        if (text == null) {
            return null;
        }

        final SpannableString formattedText = SpannableString.valueOf(text);
        formattedText.setSpan(span, 0, formattedText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return formattedText;
    }
}
