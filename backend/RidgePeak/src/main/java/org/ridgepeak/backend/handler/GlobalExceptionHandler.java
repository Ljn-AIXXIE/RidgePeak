package org.ridgepeak.backend.handler;

import org.ridgepeak.backend.exceptions.BizException;
import org.ridgepeak.backend.exceptions.ForbiddenException;
import org.ridgepeak.backend.utils.Result;
import org.ridgepeak.backend.exceptions.UnauthorizedException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<Void> handleJsonError() {
        return Result.fail("请求体格式错误，请检查 JSON 结构");
    }

    @ExceptionHandler(BizException.class)
    public Result<Void> handleBiz(BizException e) {
        return Result.fail(e.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public Result<Void> handleUnauthorized(UnauthorizedException e) {
        return Result.unauthorized(e.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    public Result<Void> handleForbidden(ForbiddenException e) {
        return Result.forbidden(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidation(MethodArgumentNotValidException e) {
        String msg = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
        return Result.fail(msg);
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleOther(Exception ex) {
        return Result.fail("服务器未知错误");
    }
}
