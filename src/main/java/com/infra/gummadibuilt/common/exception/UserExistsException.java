package com.infra.gummadibuilt.common.exception;

public class UserExistsException extends RuntimeException {

    private static final long serialVersionUID = 479441512381821423L;

    public UserExistsException(String message) {
        super(message);
    }

}
