package se.lexicon.todo_it_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import se.lexicon.todo_it_api.exception.AppResourceNotFoundException;
import se.lexicon.todo_it_api.exception.GeneralExceptionResponse;
import se.lexicon.todo_it_api.exception.ValidationErrorResponse;
import se.lexicon.todo_it_api.exception.Violation;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
@CrossOrigin("*")
public class ApplicationControllerAdvice {

    public static final String VALIDATIONS_FAILED = "One or several validations failed";

    public GeneralExceptionResponse build(HttpStatus status, String message, WebRequest request){
        return new GeneralExceptionResponse(
                LocalDateTime.now(),
                status.value(),
                status.name(),
                message,
                request.getDescription(false)
        );
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public GeneralExceptionResponse handleRunTimeException(RuntimeException ex, WebRequest request){
        return build(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public GeneralExceptionResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, WebRequest request){
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public GeneralExceptionResponse handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex, WebRequest request){
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public GeneralExceptionResponse handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request){
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(AppResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public GeneralExceptionResponse handleAppResourceNotFoundException(AppResourceNotFoundException ex, WebRequest request){
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(NumberFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public GeneralExceptionResponse handleNumberFormatException(NumberFormatException ex, WebRequest request){
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(DateTimeParseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public GeneralExceptionResponse handleDateTimeParseException(DateTimeParseException ex, WebRequest request){
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ValidationErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request){
        List<Violation> violationList = new ArrayList<>();
        for(FieldError err : ex.getBindingResult().getFieldErrors()){
            violationList.add(new Violation(err.getField(), err.getDefaultMessage()));
        }
        return new ValidationErrorResponse(build(HttpStatus.BAD_REQUEST, VALIDATIONS_FAILED, request), violationList);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ValidationErrorResponse handleConstraintViolationException(ConstraintViolationException ex, WebRequest request){
        List<Violation> violationList = new ArrayList<>();
        for(ConstraintViolation<?> violation : ex.getConstraintViolations()){
            violationList.add(new Violation(violation.getPropertyPath().toString(), violation.getMessage()));
        }
        return new ValidationErrorResponse(build(HttpStatus.BAD_REQUEST, VALIDATIONS_FAILED, request), violationList);
    }
}
