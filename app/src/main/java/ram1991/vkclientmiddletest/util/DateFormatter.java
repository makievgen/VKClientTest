package ram1991.vkclientmiddletest.util;


import java.text.SimpleDateFormat;

public class DateFormatter {
    public static final String FORMAT_1 = "HH:mm, dd.MM.yyyy";
    public static final String FORMAT_2 = "yyyy MMM dd HH:mm:ss";
    public static final String FORMAT_3 = "HH:mm";
    public static final String FORMAT_4 = "dd.MM.yyyy";

    public static String timeMillisToString(long timeInMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_3, java.util.Locale.getDefault());
        return sdf.format(timeInMillis);
    }
}
