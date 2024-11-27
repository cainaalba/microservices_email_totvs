package com.vda.email.exceptionhandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.util.Date;

@RestControllerAdvice(basePackages = {
        "com.vda.email.controller"})
public class ControllerAdvice {

    @ResponseBody
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<Object> sqlServerError(SQLException exception) {
        MessageExceptionHandler error = new MessageExceptionHandler(new Date(), HttpStatus.BAD_REQUEST.value(), exception.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ResponseBody
    @ExceptionHandler(JpaSystemException.class)
    public ResponseEntity<Object> jpaException(JpaSystemException exception) {
        MessageExceptionHandler error = new MessageExceptionHandler(new Date(), HttpStatus.BAD_REQUEST.value(), exception.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ResponseBody
    @ExceptionHandler(ValidacaoException.class)
    public ResponseEntity<MessageExceptionHandler> validacaoException(ValidacaoException notFound) {
        MessageExceptionHandler error = new MessageExceptionHandler(new Date(), HttpStatus.INTERNAL_SERVER_ERROR.value(), notFound.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
