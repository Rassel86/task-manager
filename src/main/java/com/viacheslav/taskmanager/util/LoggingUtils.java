package com.viacheslav.taskmanager.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public final class LoggingUtils {

    private static final String MASKED_PATTERN = "***";

    public static String maskEmail(String email) {
        if (email == null) return null;
        if (!email.contains("@")) return maskUsername(email);

        String[] parts = email.split("@", 2);

        String localPart = parts[0];
        String domainPart = parts[1];
        String maskedLocal = maskLocalPart(localPart);

        return maskedLocal + "@" + domainPart;
    }

    public static String maskUsername(String username) {
        if (username == null) return "null";
        if (username.isEmpty()) return "<Empty>";

        return username.charAt(0) + MASKED_PATTERN;
    }

    private static String maskLocalPart(String localPart) {
        if (localPart == null || localPart.isEmpty()) {
            return MASKED_PATTERN;
        }

        return switch (localPart.length()) {
            case 1 -> "*";
            case 2 -> localPart.charAt(0) + "*";
            default -> {
                char first = localPart.charAt(0);
                char last = localPart.charAt(localPart.length() - 1);
                yield first + MASKED_PATTERN + last;
            }
        };
    }
}
