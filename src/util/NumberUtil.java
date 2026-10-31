package util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class NumberUtil {
    
    private static final DecimalFormat DH_FORMAT = new DecimalFormat("#,##0.00 DH");
    private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("#,##0.00");
    
    public static String formatDH(double amount) {
        return DH_FORMAT.format(amount);
    }
    
    public static String formatNumber(double number, int decimalPlaces) {
        DecimalFormat format = new DecimalFormat();
        format.setMinimumFractionDigits(decimalPlaces);
        format.setMaximumFractionDigits(decimalPlaces);
        return format.format(number);
    }
    
    public static String formatNumber(double number) {
        return NUMBER_FORMAT.format(number);
    }
    
    public static String formatInteger(int number) {
        return NumberFormat.getNumberInstance(Locale.FRENCH).format(number);
    }
    
    public static double parseDouble(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(text.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    public static int parseInt(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}