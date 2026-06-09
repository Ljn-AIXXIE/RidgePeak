package org.ridgepeak.backend.utils;

import org.springframework.http.HttpStatus;

public record Result<T>(int code, String message, T data) {
    public static Result<Void> ok() {
        return new Result<>(HttpStatus.OK.value(), "success", null);
    }

    public static Result<Void> fail(String message) {
        return new Result<>(HttpStatus.BAD_REQUEST.value(), message, null);
    }

    public static Result<Void> unauthorized(String message) {
        return new Result<>(HttpStatus.UNAUTHORIZED.value(), message, null);
    }

    public static Result<Void> forbidden(String message) {
        return new Result<>(HttpStatus.FORBIDDEN.value(), message, null);
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(HttpStatus.OK.value(), "success", data);
    }
}