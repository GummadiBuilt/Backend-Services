package com.infra.gummadibuilt.common.errors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class InvalidDataErrorDetails extends ErrorDetails {

    private final String message;

    @JsonCreator
    public InvalidDataErrorDetails(@JsonProperty("message") String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
