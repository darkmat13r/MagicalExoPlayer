package com.potyvideo.library.utils;


import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import androidx.annotation.Nullable;


import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Pattern;

public class Tools {

    private static final String TAG = "VLC/Tools";
    private static final ThreadLocal<NumberFormat> TWO_DIGITS = new ThreadLocal<NumberFormat>() {
        @Override
        protected NumberFormat initialValue() {
            NumberFormat fmt = NumberFormat.getInstance(Locale.US);
            if (fmt instanceof DecimalFormat) ((DecimalFormat) fmt).applyPattern("00");
            return fmt;
        }
    };

    /*
     * Convert file:// uri from real path to emulated FS path.
     */
    public static Uri convertLocalUri(Uri uri) {
        if (!TextUtils.equals(uri.getScheme(), "file") || !uri.getPath().startsWith("/sdcard"))
            return uri;
        String path = uri.toString();
        return Uri.parse(path.replace("/sdcard", Environment.getExternalStorageDirectory().getPath()));
    }

    public static boolean isArrayEmpty(@Nullable Object[] array) {
        return array == null || array.length == 0;
    }


    /**
     * Convert time to a string
     * @param millis e.g.time/length from file
     * @return formated string (hh:)mm:ss
     */
    public static String millisToString(long millis) {
        return millisToString(millis, false, true, false);
    }

    /**
     * Convert time to a string
     * @param millis e.g.time/length from file
     * @return formated string "[hh]h[mm]min" / "[mm]min[s]s"
     */
    public static String millisToText(long millis) {
        return millisToString(millis, true, true, false);
    }

    /**
     * Convert time to a string with large formatting
     *
     * @param millis e.g.time/length from file
     * @return formated string "[hh]h [mm]min " / "[mm]min [s]s"
     */
    public static String millisToTextLarge(long millis) {
        return millisToString(millis, true, true, true);
    }


    public static String millisToString(long millis, boolean text, boolean seconds, boolean large) {
        StringBuilder sb = new StringBuilder();
        if (millis < 0) {
            millis = -millis;
            sb.append("-");
        }

        millis /= 1000;
        int sec = (int) (millis % 60);
        millis /= 60;
        int min = (int) (millis % 60);
        millis /= 60;
        int hours = (int) millis;

        if (text) {
            if (hours > 0)
                sb.append(hours).append('h').append(large ? " " : "");
            if (min > 0)
                sb.append(min).append("min").append(large ? " " : "");
            if ((seconds || sb.length() == 0) && sec > 0)
                sb.append(sec).append("s").append(large ? " " : "");
        } else {
            if (hours > 0)
                sb.append(hours).append(':').append(large ? " " : "").append(TWO_DIGITS.get().format(min)).append(':').append(large ? " " : "").append(TWO_DIGITS.get().format(sec));
            else
                sb.append(min).append(':').append(large ? " " : "").append(TWO_DIGITS.get().format(sec));
        }
        return sb.toString();
    }


}
