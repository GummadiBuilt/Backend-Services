package com.infra.gummadibuilt.common.errors.field;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Contains additional information (number of integer and fractional digits allowed) for the error
 * code {@link FieldErrorCode#DIGITS}.
 */
public class DigitsFieldErrorDetails extends FieldErrorDetails {

    private final int integerDigits;

    private final int fractionalDigits;

    public DigitsFieldErrorDetails(@JsonProperty("integerDigits") int integerDigits,
                                   @JsonProperty("fractionalDigits") int fractionalDigits) {
        super(FieldErrorCode.DIGITS);
        this.integerDigits = integerDigits;
        this.fractionalDigits = fractionalDigits;
    }

    public int getIntegerDigits() {
        return integerDigits;
    }

    public int getFractionalDigits() {
        return fractionalDigits;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DigitsFieldErrorDetails that = (DigitsFieldErrorDetails) o;
        return integerDigits == that.integerDigits &&
                fractionalDigits == that.fractionalDigits;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), integerDigits, fractionalDigits);
    }

}
