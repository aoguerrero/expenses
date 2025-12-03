package onl.andres.expenses.vm;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CustomFormatter {

    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String TIME_FORMAT = "HH:mm:ss";
    private static final String ZERO_HOUR = "00:00:00";

    private CustomFormatter() {
    }

    public static String formatDate(LocalDateTime localDateTime) {
        return localDateTime != null ? DateTimeFormatter.ofPattern(DATE_FORMAT).format(localDateTime) : "";
    }

    public static String formatDateTime(LocalDateTime localDateTime) {
        return localDateTime != null ? DateTimeFormatter.ofPattern(DATE_FORMAT + " " + TIME_FORMAT).format(localDateTime) : "";
    }

    public static LocalDateTime parseDate(String strDate) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATE_FORMAT + " " +TIME_FORMAT);
        return LocalDateTime.parse(strDate.length() == 10 ? strDate + " " + ZERO_HOUR : strDate, dtf);
    }

    public static String formatCurrency(Long value) {
        return value != null ? NumberFormat.getCurrencyInstance().format(value) : "";
    }
}
