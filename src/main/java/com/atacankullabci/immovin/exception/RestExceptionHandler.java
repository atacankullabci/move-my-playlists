package com.atacankullabci.immovin.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.xml.crypto.dsig.TransformException;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        String error = "Bad request";
        return buildResponseEntity(new ApiErrorResponseBody(HttpStatus.BAD_REQUEST, error));
    }

    private ResponseEntity<Object> buildResponseEntity(ApiErrorResponseBody apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    // All other exceptions are handled below 
    /*@ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleEntityNotFound() {
        return buildResponseEntity(new ApiErrorResponseBody(HttpStatus.BAD_REQUEST, "Bad request"));
    }

     */

    @ExceptionHandler(InvalidFileException.class)
    protected ResponseEntity<Object> handleInvalidFileException(InvalidFileException exception) {
        return buildResponseEntity(new ApiErrorResponseBody(HttpStatus.BAD_REQUEST, exception.getMessage()));
    }

    @ExceptionHandler(TransformException.class)
    protected ResponseEntity<Object> handleTransformerException(TransformException exception) {
        return buildResponseEntity(new ApiErrorResponseBody(HttpStatus.BAD_REQUEST, exception.getMessage()));
    }
}
