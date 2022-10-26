package com.infra.gummadibuilt.common.errors.field;

/**
 * Convenient factory class for field error objects.
 */
public final class FieldErrors {

    private FieldErrors() {

    }

    /**
     * Creates a new field error that will show a generic text message.
     */
    public static GenericFieldErrorDetails generic(String message) {
        return new GenericFieldErrorDetails(message);
    }

    /**
     * Creates a new field error that indicates that a field value needs to be unique, but isn't (i.e. project names).
     */
    public static FieldErrorDetails duplicate() {
        return new FieldErrorDetails(FieldErrorCode.DUPLICATE);
    }

    /**
     * Creates a new field error that indicates that a field value is required but wasn't filled in.
     */
    public static FieldErrorDetails required() {
        return new FieldErrorDetails(FieldErrorCode.REQUIRED);
    }

    /**
     * Creates a new field error that indicates that a field value couldn't be parsed as an integer number.
     */
    public static FieldErrorDetails integer() {
        return new FieldErrorDetails(FieldErrorCode.INTEGER);
    }

    /**
     * Creates a new field error that indicates that a field value couldn't be parsed as a decimal number.
     */
    public static FieldErrorDetails decimal() {
        return new FieldErrorDetails(FieldErrorCode.DECIMAL);
    }

    /**
     * Creates a new field error that indicates that a field value exceeds the possible precision of the
     * database value.
     */
    public static DigitsFieldErrorDetails digits(int integerDigits, int fractionalDigits) {
        return new DigitsFieldErrorDetails(integerDigits, fractionalDigits);
    }

    /**
     * Creates a new field error that indicates that a field value doesnt meet the minimum
     * value that is needed for the field.
     */
    public static MinFieldErrorDetails min(long valueMin) {
        return new MinFieldErrorDetails(valueMin);
    }

    /**
     * Creates a new field error that indicates that a field value exceeds the possible length of the
     * database value.
     */
    public static SizeFiledErrors size(int minSize, int maxSize) {
        return new SizeFiledErrors(minSize, maxSize);
    }
}
