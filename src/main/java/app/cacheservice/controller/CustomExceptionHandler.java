package app.cacheservice.controller;

import app.cacheservice.exception.ErrorResponse;
import app.cacheservice.exception.MissingHeaderInfoException;
import app.cacheservice.exception.RecordNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    private String INCORRECT_REQUEST = "INCORRECT_REQUEST";
    private String BAD_REQUEST = "BAD_REQUEST";

    @ExceptionHandler(RecordNotFoundException.class)
    public final ResponseEntity<ErrorResponse> handleUserNotFoundException
        (RecordNotFoundException ex, WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(INCORRECT_REQUEST, List.of(ex.getLocalizedMessage()));
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(MissingHeaderInfoException.class)
    public final ResponseEntity<ErrorResponse> handleMissingHeaderException
        (MissingHeaderInfoException ex, WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(BAD_REQUEST, List.of(ex.getLocalizedMessage()));
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

    }
}
