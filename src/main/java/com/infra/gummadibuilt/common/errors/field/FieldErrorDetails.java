package com.infra.gummadibuilt.common.errors.field;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;

/**
 * Contains additional information about the validation that failed for a specific field of the
 * request.
 * <p>
 * The type of the failed validation is indicated by the subclass of this class that is used.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, property = "code", visible = true)
@JsonTypeIdResolver(FieldErrorDetailsTypeResolver.class)
public class FieldErrorDetails {

    private final FieldErrorCode code;

    @JsonCreator
    FieldErrorDetails(@JsonProperty("code") FieldErrorCode code) {
        this.code = code;
    }

    // serialization of this field is handled by @JsonTypeInfo
    @JsonIgnore
    public FieldErrorCode getCode() {
        return code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FieldErrorDetails)) return false;

        FieldErrorDetails that = (FieldErrorDetails) o;

        return code == that.code;
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }

}
