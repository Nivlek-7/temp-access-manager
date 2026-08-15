package com.example.demo.util;

import java.util.Locale;

public final class EmailUtils {

    private EmailUtils() {
    }

    public static String normalize(String email) {
        return email.strip().toLowerCase(Locale.ROOT);
    }
}
