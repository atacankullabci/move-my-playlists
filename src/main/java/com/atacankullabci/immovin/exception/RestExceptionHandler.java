package com.atacankullabci.immovin.exception;

/*
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
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleEntityNotFound(Exception exception) {
        return buildResponseEntity(new ApiErrorResponseBody(HttpStatus.BAD_REQUEST, "Bad request"));
    }

    @ExceptionHandler(TransformerException.class)
    protected ResponseEntity<Object> handleTransformerException(TransformerException exception) {
        return buildResponseEntity(new ApiErrorResponseBody(HttpStatus.BAD_REQUEST, "File content is unacceptable"));
    }
}
*/
