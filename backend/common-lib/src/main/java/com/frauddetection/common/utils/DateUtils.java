package com.frauddetection.common.utils;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public final class DateUtils {

    private static final DateTimeFormatter ISO_FORMATTER =
            DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC);

    private DateUtils() {
    }

    public static Instant now() {
        return Instant.now();
    }

    public static String toIsoString(Instant instant) {
        return instant == null ? null : ISO_FORMATTER.format(instant);
    }
}
