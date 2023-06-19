package com.ecom.craftbid.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(NotFoundException ex) {
        System.out.println(ex.getMessage());
        ex.printStackTrace();
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        System.out.println(ex.getMessage());
        ex.printStackTrace();
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(UnauthorizedException ex) {
        System.out.println(ex.getMessage());
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {
        System.out.println(ex.getMessage());
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

