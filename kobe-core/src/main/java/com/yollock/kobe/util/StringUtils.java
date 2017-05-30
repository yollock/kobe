package com.yollock.kobe.util;

import com.google.common.base.Strings;
import com.yollock.kobe.common.Constants;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public final class StringUtils {

    public static String toString(Throwable throwable) {
        return toString(null, throwable);
    }

    public static String toString(String head, Throwable throwable) {
        StringWriter w = new StringWriter(1024);
        if (head != null) w.write(head + "\n");
        PrintWriter p = new PrintWriter(w);
        try {
            throwable.printStackTrace(p);
            return w.toString();
        } finally {
            p.close();
        }
    }

    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }

    public static boolean isBlank(String s) {
        return s == null || s.trim().length() == 0;
    }

    public static String toDotSpiteString(String input) {
        char[] charArray = input.toCharArray();
        StringBuilder sb = new StringBuilder(128);
        for (int i = 0; i < charArray.length; i++) {
            if (Character.isUpperCase(charArray[i])) {
                if (i != 0 && charArray[i - 1] != '.') {
                    sb.append('.');
                }
                sb.append(Character.toLowerCase(charArray[i]));
            } else {
                sb.append(charArray[i]);
            }
        }
        return sb.toString();
    }

    public static String attribute2Getter(String attribute) {
        return "get" + attribute.substring(0, 1).toUpperCase() + attribute.substring(1);
    }

    public static String urlDecode(String value) {
        if (Strings.isNullOrEmpty(value)) {
            return "";
        }
        try {
            return URLDecoder.decode(value, Constants.DEFAULT_CHARACTER);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private StringUtils() {
    }
}
