package org.ridgepeak.backend.utils;

public class StringUtils {
    public static String trimEnd(String str, char ch) {
        if (str == null) return null;
        int len = str.length();
        int i = len - 1;
        while (i >= 0 && str.charAt(i) == ch) {
            i--;
        }
        return str.substring(0, i + 1);
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static String upperFirst(String str) {
        str = str.substring(0, 1).toUpperCase() + str.substring(1);
        return str;
    }

    public static String format2f(double d) {
        return String.format("%.2f", d);
    }
}
