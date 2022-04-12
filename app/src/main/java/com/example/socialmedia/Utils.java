package com.example.socialmedia;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

//import java.sql.Date;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;

public class Utils {

    public String convert(long time)  {
        int SECOND_MILLIS = 1000;
        int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        int DAY_MILLIS = 24 * HOUR_MILLIS;

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        String result;
        long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            result = "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            result = "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            result = (diff / MINUTE_MILLIS) + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            result = "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            result = (diff / HOUR_MILLIS) + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            result = "yesterday";
        } else {
            result = (diff / DAY_MILLIS) + " days ago";
        }

        return result;
    }

    public String testDateFormat(long  time) {
        String format = "hh:mm aa"; //24 hours format HH:mm
        //hh:mm aa for 12 hours format
        DateFormat dateFormat = new SimpleDateFormat(format);
        String date = dateFormat.format(new Date(time));
        return date;
    }

    public String long_ToDateFormat(long  time) {
        String format = "MMM dd"; //24 hours format HH:mm
        //hh:mm aa for 12 hours format
        DateFormat dateFormat = new SimpleDateFormat(format);
        String date = dateFormat.format(new Date(time));
        return date;
    }
}
