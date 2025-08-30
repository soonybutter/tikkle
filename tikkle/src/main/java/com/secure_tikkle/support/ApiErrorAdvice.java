package com.secure_tikkle.support;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ApiErrorAdvice {

  private Map<String, Object> body(
      String error,
      String message,
      List<Map<String, Object>> details,
      int status
  ) {
    Map<String, Object> m = new LinkedHashMap<>();
    m.put("ok", false);
    m.put("error", error);
    m.put("message", message);
    m.put("status", status);
    m.put("ts", Instant.now().toString());
    if (details != null) m.put("details", details);
    return m;
  }

  // @Valid 본문 검증 실패 (DTO 필드 단위) 
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleMethodArgNotValid(MethodArgumentNotValidException ex) {
    List<Map<String, Object>> details = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(fe -> {
          Map<String, Object> d = new LinkedHashMap<>();
          d.put("field", fe.getField());
          d.put("code", fe.getCode());
          d.put("message", fe.getDefaultMessage());
          return d;
        })
        .toList();

    return ResponseEntity.badRequest()
        .body(body("validation_error", "요청 본문 검증 에러", details, 400));
  }

  // @Validated 파라미터/PathVariable 검증 실패 
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException ex) {
    List<Map<String, Object>> details = ex.getConstraintViolations()
        .stream()
        .map(v -> {
          Map<String, Object> d = new LinkedHashMap<>();
          d.put("field", v.getPropertyPath().toString());
          d.put("code", v.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName());
          d.put("message", v.getMessage());
          return d;
        })
        .toList();

    return ResponseEntity.badRequest()
        .body(body("validation_error", "요청 파라미터 검증 에러", details, 400));
  }

  // 타입 바인딩 실패(예: 숫자 자리에 문자), 폼 바인딩 등 
  @ExceptionHandler(BindException.class)
  public ResponseEntity<Map<String, Object>> handleBind(BindException ex) {
    List<Map<String, Object>> details = ex.getFieldErrors()
        .stream()
        .map(fe -> {
          Map<String, Object> d = new LinkedHashMap<>();
          d.put("field", fe.getField());
          d.put("code", fe.getCode());
          d.put("message", fe.getDefaultMessage());
          return d;
        })
        .toList();

    return ResponseEntity.badRequest()
        .body(body("validation_error", "바인딩 에러", details, 400));
  }

  // JSON 파싱 실패 등 
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<Map<String, Object>> handleNotReadable(HttpMessageNotReadableException ex) {
    return ResponseEntity.badRequest()
        .body(body("bad_json", "잘못된 JSON 형식", null, 400));
  }

  // 도메인 쪽에서 던지는 400 계열 
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(body("bad_request", ex.getMessage(), null, 400));
  }
}
