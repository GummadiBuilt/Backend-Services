package com.infra.gummadibuilt.common.exception;

import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

/**
 * Exception for unrecoverable application errors in Bit Inspector.
 */
public class GummadiBuiltException extends RuntimeException {

    private final String message;

    public GummadiBuiltException(String message) {
        this.message = message;
    }

    public GummadiBuiltException(String message, Object... args) {
        FormattingTuple formattingTuple = MessageFormatter.arrayFormat(message, args);

        if (formattingTuple.getThrowable() != null) {
            initCause(formattingTuple.getThrowable());
        }

        this.message = formattingTuple.getMessage();
    }

    @Override
    public String getMessage() {
        return message;
    }

}
