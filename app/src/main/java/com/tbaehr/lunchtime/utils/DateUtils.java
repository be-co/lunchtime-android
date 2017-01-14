package com.tbaehr.lunchtime.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by timo.baehr@gmail.com on 14.01.17.
 */
public class DateUtils {

    /**
     * example for date: Jan 12 2017 11:30:00 GMT+0100
     */
    private static final String DATE_FORMAT = "MMM dd yyyy HH:mm:ss 'GMT'Z";

    public static Date createDateFromString(String date) {
        if (date == null) {
            return null;
        }

        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }
}
