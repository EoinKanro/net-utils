package io.github.eoinkanro.net.utils.core.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class TimeConverter {

    /**
     * Format specific string to ms
     * Example: 1H, 1M, 1S, 1m
     *
     * @param str formatted string
     * @return ms
     */
    public static Long formattedStringToLongMs(String str) {
        if (str == null || str.isEmpty()) {
            return 0L;
        }

        Unit unit = Unit.value(str.trim().substring(str.length() - 1));
        return Long.parseLong(str.substring(0, str.length() - 1)) * unit.getMultiplayer();
    }

    @Getter
    @RequiredArgsConstructor
    private enum Unit {
        HOUR(360000),
        MINUTE(60000),
        SECOND(1000),
        MILLIS(1);

        private final long multiplayer;

        public static Unit value(String str) {
            if (str == null || str.isEmpty()) {
                return MILLIS;
            }

            return switch (str) {
                case "H" -> HOUR;
                case "M" -> MINUTE;
                case "S" -> SECOND;
                default -> MILLIS;
            };
        }
    }
}
