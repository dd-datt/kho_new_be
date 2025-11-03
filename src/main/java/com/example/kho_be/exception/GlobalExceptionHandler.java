package com.example.kho_be.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Xử lý lỗi validation
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        response.put("status", "error");
        response.put("message", "Validation failed");
        response.put("errors", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Xử lý lỗi BadCredentialsException (sai username/password)
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentialsException(BadCredentialsException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * Xử lý lỗi UsernameNotFoundException
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Xử lý lỗi ràng buộc khóa ngoại (Foreign Key Constraint)
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex) {
        logger.error("DataIntegrityViolationException caught: {}", ex.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");

        String errorMessage = ex.getMessage();
        Throwable rootCause = ex.getRootCause();
        String rootCauseMessage = rootCause != null ? rootCause.getMessage() : null;

        logger.debug("Root cause: {}", rootCauseMessage);

        // Xử lý lỗi khóa ngoại với thông báo dễ hiểu
        String fullMessage = errorMessage + (rootCauseMessage != null ? " " + rootCauseMessage : "");

        if (fullMessage != null) {
            if (fullMessage.contains("foreign key constraint fails")
                    || fullMessage.contains("Cannot delete or update a parent row")) {
                if (fullMessage.contains("export_slip_details")) {
                    response.put("message", "Không thể xóa sản phẩm này vì đã có trong phiếu xuất kho");
                    response.put("detail",
                            "Sản phẩm đã được sử dụng trong các phiếu xuất kho. Vui lòng xóa các phiếu xuất liên quan trước.");
                } else if (fullMessage.contains("import_slip_details")) {
                    response.put("message", "Không thể xóa sản phẩm này vì đã có trong phiếu nhập kho");
                    response.put("detail",
                            "Sản phẩm đã được sử dụng trong các phiếu nhập kho. Vui lòng xóa các phiếu nhập liên quan trước.");
                } else {
                    response.put("message", "Không thể xóa do có dữ liệu liên quan");
                    response.put("detail", "Dữ liệu này đang được sử dụng ở nơi khác trong hệ thống.");
                }
            } else if (fullMessage.contains("Duplicate entry")) {
                response.put("message", "Dữ liệu bị trùng lặp");
                response.put("detail", errorMessage);
            } else {
                response.put("message", "Lỗi ràng buộc dữ liệu");
                response.put("detail", errorMessage);
            }
        } else {
            response.put("message", "Lỗi ràng buộc dữ liệu");
        }

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     * Xử lý các lỗi chung
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Xử lý tất cả các exception khác
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "Đã xảy ra lỗi không mong muốn");
        response.put("details", ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
