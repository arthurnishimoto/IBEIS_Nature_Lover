package edu.uic.ibeis_tourist.utils;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateTimeUtils {

    private static String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static String PRESENTATION_FORMAT = "MM/dd/yyyy 'at' h:mm a";

    public static String convertToDatetimeString (GregorianCalendar dateTime) {
        return new SimpleDateFormat(DATETIME_FORMAT).format(dateTime.getTime());
    }

    public static GregorianCalendar convertToGregorianCalendar (String dateTimeString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATETIME_FORMAT);

        GregorianCalendar dateTime = new GregorianCalendar();

        Date d = dateFormat.parse(dateTimeString, new ParsePosition(0));
        if (d == null) {
            return null;
        }
        else {
            dateTime.setTimeInMillis(d.getTime());
            return dateTime;
        }
    }

    public static String presentationFormat (GregorianCalendar dateTime) {
        return new SimpleDateFormat(PRESENTATION_FORMAT).format(dateTime.getTime());
    }
}
