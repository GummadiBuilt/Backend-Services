package com.infra.gummadibuilt.common.errors.field;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class GenericFieldErrorDetails extends FieldErrorDetails {

    private final String message;

    GenericFieldErrorDetails(@JsonProperty("message") String message) {
        super(FieldErrorCode.GENERIC);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        GenericFieldErrorDetails that = (GenericFieldErrorDetails) o;
        return Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), message);
    }
}
