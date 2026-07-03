package com.ahngow95.incidents.common;

import java.time.OffsetDateTime;
import java.util.List;

public record ApiError(
        OffsetDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldViolation> fieldViolations
) {
    public static ApiError of(int status, String error, String message, String path) {
        return new ApiError(OffsetDateTime.now(), status, error, message, path, List.of());
    }

    public static ApiError withViolations(int status, String error, String message, String path,
                                          List<FieldViolation> fieldViolations) {
        return new ApiError(OffsetDateTime.now(), status, error, message, path, fieldViolations);
    }

    public record FieldViolation(String field, String message) {
    }
}
