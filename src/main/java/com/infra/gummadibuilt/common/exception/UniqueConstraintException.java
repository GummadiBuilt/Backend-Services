package com.infra.gummadibuilt.common.exception;

/**
 * A validation error that resulted from a field that needed to be unique not being unique.
 */
public class UniqueConstraintException extends RuntimeException {

    private final String propertyName;

    public UniqueConstraintException(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyName() {
        return propertyName;
    }

}
