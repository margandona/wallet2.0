package com.wallet.presentation.web;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

/**
 * Simple helpers for web form handling.
 */
public final class WebFormUtils {

    private WebFormUtils() {
    }

    public static String trimmed(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        return value == null ? "" : value.trim();
    }

    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static BigDecimal parseAmount(String value) {
        try {
            return new BigDecimal(value);
        } catch (Exception e) {
            throw new IllegalArgumentException("Monto invalido.");
        }
    }

    public static Integer parseInt(String value, String errorMessage) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
