package org.ridgepeak.backend.utils;

import org.springframework.http.HttpStatus;

public record Result<T>(int code, String message, T data) {
    public static Result<Void> ok() {
        return new Result<>(200, "success", null);
    }

    public static Result<Void> fail(String message) {
        return new Result<>(400, message, null);
    }

    public static Result<Void> unauthorized(String message) {
        return new Result<>(401, message, null);
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(HttpStatus.OK.value(), "success", data);
    }
}