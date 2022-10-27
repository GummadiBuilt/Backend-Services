
package com.infra.gummadibuilt.common.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.google.common.collect.ImmutableSet;
import com.infra.gummadibuilt.common.errors.ErrorDetails;
import com.infra.gummadibuilt.common.errors.PermissionDeniedErrorDetails;
import com.infra.gummadibuilt.common.errors.ValidationErrorDetails;
import com.infra.gummadibuilt.common.errors.field.FieldErrorDetails;
import com.infra.gummadibuilt.common.errors.field.FieldErrors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.*;
import javax.validation.metadata.ConstraintDescriptor;
import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.AccessDeniedException;
import java.util.Set;
import java.util.stream.Collectors;

@ControllerAdvice
@RestController
public class GummadiBuiltExceptionHandler {

    private static final String UNHANDLED_EXCEPTION = "We've run into an error. If the issue persists, please contact support team with Correlation ID: ";
    private static final Logger LOGGER = LoggerFactory.getLogger(GummadiBuiltExceptionHandler.class);
    // These field-types lead to the assumption the value needed to be an
    // integer
    private static final Set<Class<?>> INTEGER_CLASSES = ImmutableSet.of(int.class, short.class, byte.class,
            Integer.class, Short.class, Byte.class, BigInteger.class);
    // These field types leads to the assumption that the value needed to be a
    // decimal
    private static final Set<Class<?>> DECIMAL_CLASSES = ImmutableSet.of(float.class, double.class, Float.class,
            Double.class, BigDecimal.class);

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ErrorDetails handleException(Exception ex) {
        String correlationId = MDC.get("CID");
        LOGGER.error("Unhandled exception occurred while processing request.CorrelationID:{}. Stack Trace:{}", correlationId, ex.getMessage());
        return new ErrorDetails(UNHANDLED_EXCEPTION + correlationId);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ValidationErrorDetails handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        ValidationErrorDetails valErrDetails = new ValidationErrorDetails();
        valErrDetails.getGlobal().add("Invalid value for parameter " + ex.getName() + ": " + ex.getMessage());
        return valErrDetails;
    }

    @ExceptionHandler(EntityFieldSizeLimitException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorDetails handleEntityFieldSizeLimitException(EntityFieldSizeLimitException e) {
        ValidationErrorDetails error = new ValidationErrorDetails();
        error.getFields().put(e.getPropertyName(), FieldErrors.size(e.getMinSize(), e.getMaxSize()));
        return error;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ValidationErrorDetails handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {

        ValidationErrorDetails valErrDetails = new ValidationErrorDetails();

        for (ObjectError objError : ex.getBindingResult().getAllErrors()) {

            if (objError instanceof FieldError) {
                FieldError fieldError = (FieldError) objError;

                FieldErrorDetails fieldErrorDetails = null;

                // Try to find the descriptor
                ConstraintDescriptor<?> descriptor = null;
                for (Object arg : fieldError.getArguments()) {
                    if (arg instanceof ConstraintDescriptor) {
                        descriptor = (ConstraintDescriptor<?>) arg;
                        break;
                    }
                }

                // If we found the descriptor, we can provide a nicer error
                // resolution
                if (descriptor != null) {
                    fieldErrorDetails = getFieldErrorFromAnnotation(descriptor.getAnnotation());
                }

                // Fall back to an otherwise obtained error message (this might
                // be from the message attribute of a
                // constraint for example)
                if (fieldErrorDetails == null) {
                    fieldErrorDetails = FieldErrors.generic(fieldError.getDefaultMessage());
                }
                String path = fieldError.getField();
                valErrDetails.getFields().put(path, fieldErrorDetails);
            } else {
                valErrDetails.getGlobal().add(objError.getDefaultMessage());
            }

        }

        return valErrDetails;

    }

    private FieldErrorDetails getFieldErrorFromAnnotation(Annotation annotation) {
        if (annotation instanceof NotEmpty || annotation instanceof NotNull) {
            return FieldErrors.required();
        } else if (annotation instanceof Digits) {
            Digits digits = (Digits) annotation;
            return FieldErrors.digits(digits.integer(), digits.fraction());
        } else if (annotation instanceof Size) {
            Size size = (Size) annotation;
            return FieldErrors.size(size.min(), size.max());
        } else if (annotation instanceof Min) {
            Min min = (Min) annotation;
            return FieldErrors.min(min.value());
        }

        return null;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ValidationErrorDetails handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {

        // Explicitly converting to string here because we dont want the full
        // exception stack
        LOGGER.info("Unable to read request body of request: {}", ex);

        ValidationErrorDetails valErrDetails = new ValidationErrorDetails();

        // Handle JSON mapping errors which can occur when a field type is not
        // compatible with the given value
        // Best example: entering text into a numbers only field
        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) cause;

            // The path to the property/field which caused this error, converted
            // into a.b.c format.
            String path = ife.getPath().stream().map(JsonMappingException.Reference::getFieldName)
                    .collect(Collectors.joining("."));

            // The target type helps in determining what the expected data
            // format is
            Class<?> targetType = ife.getTargetType();

            FieldErrorDetails fieldError = null;
            if (DECIMAL_CLASSES.contains(targetType)) {
                fieldError = FieldErrors.decimal();
            } else if (INTEGER_CLASSES.contains(targetType)) {
                fieldError = FieldErrors.integer();
            }

            if (fieldError != null) {
                valErrDetails.getFields().put(path, fieldError);
            } else {
                valErrDetails.getFields().put(path, FieldErrors.generic(ife.getMessage()));
            }
        }

        return valErrDetails;

    }

    @ExceptionHandler(UniqueConstraintException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorDetails handleUniqueConstraintViolation(UniqueConstraintException e) {
        ValidationErrorDetails error = new ValidationErrorDetails();
        error.getFields().put(e.getPropertyName(), FieldErrors.duplicate());
        return error;
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorDetails handleAccessDeniedException(AccessDeniedException ex) {
        return new PermissionDeniedErrorDetails(ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleEntityNotFoundException(EntityNotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(InValidFileUploadedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleInValidFileUploadedException(InValidFileUploadedException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(BlobNotUploadedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleFileNotUploadedException(BlobNotUploadedException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(BlobNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleFileNotFoundException(BlobNotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(UserExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String userExistsException(UserExistsException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(InvalidActionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String invalidActionException(InvalidActionException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(InvalidJsonException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleInvalidJsonException(InvalidJsonException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(InValidDataSubmittedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleInvalidDataException(InValidDataSubmittedException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(EntityFieldNotNullException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorDetails handleEntityNullException(EntityFieldNotNullException ex) {
        String path = ex.getPropertyName().toString();
        ValidationErrorDetails error = new ValidationErrorDetails();
        error.getFields().put(path, FieldErrors.required());
        return error;
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleInvalidRequestException(MissingServletRequestPartException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ValidationErrorDetails handleConstraintViolation(ConstraintViolationException ex) {

        ValidationErrorDetails valErrDetails = new ValidationErrorDetails();

        Set<ConstraintViolation<?>> constraints = ex.getConstraintViolations();
        for (ConstraintViolation<?> constraintViolation : constraints) {

            // The path to the property/field which caused this error, converted
            // into a.b.c format.
            String path = constraintViolation.getPropertyPath().toString();
            valErrDetails.getFields().put(path, FieldErrors.generic(constraintViolation.getMessage()));
        }

        return valErrDetails;

    }

}
