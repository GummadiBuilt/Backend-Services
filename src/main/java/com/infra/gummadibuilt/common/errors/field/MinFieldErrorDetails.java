package com.infra.gummadibuilt.common.errors.field;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Contains additional information value to minimum of 1
 * code {@link FieldErrorCode#MIN}.
 */
public class MinFieldErrorDetails extends FieldErrorDetails {

    private final long valueMin;

    MinFieldErrorDetails(@JsonProperty("valueMin") long valueMin) {
        super(FieldErrorCode.MIN);
        this.valueMin = valueMin;
    }

    public long getValueMin() {
        return valueMin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MinFieldErrorDetails that = (MinFieldErrorDetails) o;
        return Objects.equals(valueMin, that.valueMin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), valueMin);
    }


}
