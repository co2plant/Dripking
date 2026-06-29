package kr.co.inntavern.dripking.controller;

import kr.co.inntavern.dripking.service.InsufficientCreditException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException exception) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", false);
        response.put("code", "INVALID_REQUEST");
        response.put("message", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(InsufficientCreditException.class)
    public ResponseEntity<Map<String, Object>> handleInsufficientCreditException(InsufficientCreditException exception) {
        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("requiredCredit", exception.getRequiredCredit());
        detail.put("currentBalance", exception.getCurrentBalance());

        Map<String, Object> error = new LinkedHashMap<>();
        error.put("code", "INSUFFICIENT_CREDIT");
        error.put("message", exception.getMessage());
        error.put("detail", detail);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("error", error);
        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(response);
    }
}
