package com.infra.gummadibuilt.common.errors.field;

import com.fasterxml.jackson.annotation.JsonValue;

public enum FieldErrorCode {

    /**
     * A generic error code that can be used in cases where only a text based validation message can be
     * shown for a field and none of the other codes fit.
     */
    GENERIC(GenericFieldErrorDetails.class),

    /**
     * The field is required but wasn't filled out.
     */
    REQUIRED,

    /**
     * The field needs to be unique, but a duplicate value was given (i.e. unique project names are required,
     * but the project name already exists).
     */
    DUPLICATE,

    /**
     * The field value should be an integer, but was something else.
     */
    INTEGER,

    /**
     * The field value should be an integer or a decimal number, but was neither.
     */
    DECIMAL,

    /**
     * The given number exceeds the precision of the database field.
     */
    DIGITS(DigitsFieldErrorDetails.class),

    /**
     * The given number is less than 0
     */
    MIN(MinFieldErrorDetails.class),

    SIZE(SizeFiledErrors.class);

    private final Class<? extends FieldErrorDetails> errorClass;

    FieldErrorCode() {
        this(FieldErrorDetails.class); // Default to the base object with no extra properties
    }

    FieldErrorCode(Class<? extends FieldErrorDetails> errorClass) {
        this.errorClass = errorClass;
    }

    public static FieldErrorCode fromJsonCode(String jsonCode) {
        for (FieldErrorCode fieldErrorCode : values()) {
            if (fieldErrorCode.name().equalsIgnoreCase(jsonCode)) {
                return fieldErrorCode;
            }
        }

        return null;
    }

    public Class<? extends FieldErrorDetails> getErrorClass() {
        return errorClass;
    }

    @JsonValue
    public String getJsonCode() {
        return name().toLowerCase();
    }

}
