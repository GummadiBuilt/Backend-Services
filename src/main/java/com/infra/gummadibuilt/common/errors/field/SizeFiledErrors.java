package com.infra.gummadibuilt.common.errors.field;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Contains additional information (number of min and max Length of text box
 * allowed) for the error code {@link FieldErrorCode#SIZE}.
 */
public class SizeFiledErrors extends FieldErrorDetails {
    private final int minSize;
    private final int maxSize;

    public SizeFiledErrors(@JsonProperty("minSize") int minSize, @JsonProperty("maxSize") int maxSize) {
        super(FieldErrorCode.SIZE);
        this.minSize = minSize;
        this.maxSize = maxSize;
    }

    public int getMinSize() {
        return minSize;
    }

    public int getMaxSize() {
        return maxSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        SizeFiledErrors that = (SizeFiledErrors) o;
        return minSize == that.minSize && maxSize == that.maxSize;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), maxSize, maxSize);
    }

}
