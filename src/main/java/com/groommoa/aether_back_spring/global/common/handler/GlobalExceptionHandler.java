package com.groommoa.aether_back_spring.global.common.handler;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.groommoa.aether_back_spring.domain.user.exception.UserException;
import com.groommoa.aether_back_spring.global.common.constants.HttpStatus;
import com.groommoa.aether_back_spring.global.common.exception.ErrorCode;
import com.groommoa.aether_back_spring.global.common.response.ErrorResponse;
import com.mongodb.MongoException;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        Throwable cause = ExceptionUtils.getRootCause(ex);
        if (cause == null) cause = ex.getCause();

        String message = "유효하지 않은 입력값입니다.";
        List<Map<String, String>> errors = new ArrayList<>();

        if (cause instanceof InvalidFormatException ife) {
            Class<?> targetType = ife.getTargetType();
            Object invalidValue = ife.getValue();
            String field = extractFieldFromPath(ife.getPathReference());

            if (targetType.isEnum()) {
                message = String.format("'%s'은(는) 지원하지 않는 값입니다.", invalidValue);
                errors.add(Map.of(
                        "field", field,
                        "reason", String.format("가능한 값: %s", Arrays.toString(targetType.getEnumConstants()))
                ));
            } else if (targetType == ObjectId.class) {
                message = "잘못된 ObjectId 형식입니다.";
                errors.add(Map.of(
                        "field", field,
                        "reason", String.format("'%s'은(는) 올바르지 않은 ObjectId입니다.", invalidValue)
                ));
            } else {
                message = "입력값 형식 오류";
                errors.add(Map.of(
                        "field", field,
                        "reason", String.format("'%s'은(는) %s 타입으로 변환할 수 없습니다.",
                                invalidValue, targetType.getSimpleName())
                ));
            }
        } else if (cause instanceof IllegalArgumentException iae && iae.getMessage() != null) {
            String msg = iae.getMessage();
            if (msg.contains("No enum constant")) {
                try {
                    String[] parts = msg.split("No enum constant ")[1].split("\\.");
                    String className = String.join(".", Arrays.copyOf(parts, parts.length - 1));
                    String invalidValue = parts[parts.length - 1];
                    String simpleClassName = className.substring(className.lastIndexOf(".") + 1);

                    message = "Enum 값이 올바르지 않습니다.";
                    errors.add(Map.of(
                            "field", simpleClassName,
                            "reason", String.format("'%s'은(는) %s에서 지원하지 않는 Enum 값입니다. 대소문자나 오타를 확인해주세요.",
                                    invalidValue, simpleClassName)
                    ));
                } catch (Exception e) {
                    message = "Enum 값이 올바르지 않습니다.";
                }
            } else {
                message = msg;
            }
        } else if (cause instanceof MismatchedInputException) {
            message = "입력값의 형식이 맞지 않습니다.";
        } else if (cause instanceof JsonParseException) {
            message = "잘못된 JSON 형식입니다.";
        } else message = Objects.requireNonNullElse(cause, ex).getMessage();

        Map<String, Object> details = errors.isEmpty() ? null : Map.of("errors", errors);
        ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST, message, details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> Map.of(
                        "field", error.getField(),
                        "reason", error.getDefaultMessage()
                ))
                .toList();

        Map<String, Object> details = Map.of("errors", errors);

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST, "유효하지 않은 입력값입니다.", details
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorResponse> handleUserException(UserException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        ErrorResponse response = new ErrorResponse(
                errorCode.getHttpStatus().value(), errorCode.getMessage(), null);
        return ResponseEntity.status(ex.getErrorCode().getHttpStatus()).body(response);
    }


    @ExceptionHandler(MongoException.class)
    public ResponseEntity<ErrorResponse> handleMongoException(MongoException ex) {
        ex.printStackTrace();
        ErrorResponse response = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR, "데이터베이스 오류가 발생했습니다.", null);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        ex.printStackTrace();
        ErrorResponse response = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.", null);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    private String extractFieldFromPath(String pathReference) {
        if (pathReference == null) return "unknown";
        int start = pathReference.lastIndexOf("[\"") + 2;
        int end = pathReference.lastIndexOf("\"]");
        if (start > 1 && end > start) {
            return pathReference.substring(start, end);
        }
        return "unknown";
    }
}
