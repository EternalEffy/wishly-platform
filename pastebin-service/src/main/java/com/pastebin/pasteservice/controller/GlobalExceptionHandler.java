package com.pastebin.pasteservice.controller;

import com.pastebin.common.exception.PasteNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PasteNotFoundException.class)
    public ResponseEntity<Void> handleNotFound(PasteNotFoundException ex) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(com.pastebin.common.exception.PasteExpiredException.class)
    public ResponseEntity<Void> handleExpired(com.pastebin.common.exception.PasteExpiredException ex) {
        return ResponseEntity.status(HttpStatus.GONE).build();  // 410 Gone
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Void> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().build();
    }
}
