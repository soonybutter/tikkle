package com.secure_tikkle.advice;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;


// 공통 예외/검증 에러를 JSON으로 깔끔하게 내려준다.
// - 400: 잘못된 요청(검증 실패, 파라미터 타입 불일치, 도메인 유효성 에러 등)
 
@RestControllerAdvice
public class ApiExceptionHandler {

	/** Bean Validation 실패 처리 (DTO @Valid) */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> beanValidation(MethodArgumentNotValidException e) {
        var errs = e.getBindingResult().getFieldErrors().stream()
                .map(f -> Map.of("field", f.getField(), "msg", f.getDefaultMessage()))
                .toList();
        return ResponseEntity.badRequest().body(Map.of("ok", false, "errors", errs));
    }

    /** 잘못된 요청(도메인/타입 등) */
    @ExceptionHandler({ IllegalArgumentException.class, MethodArgumentTypeMismatchException.class })
    public ResponseEntity<?> badReq(RuntimeException e) {
        return ResponseEntity.badRequest().body(Map.of("ok", false, "error", e.getMessage()));
    }
}
