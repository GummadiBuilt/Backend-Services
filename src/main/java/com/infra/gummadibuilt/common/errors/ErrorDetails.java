package com.infra.gummadibuilt.common.errors;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Presents additional details about a server-side error to the client.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(name = "internal_server_error", value = ErrorDetails.class),
        @JsonSubTypes.Type(name = "validation", value = ValidationErrorDetails.class),
        @JsonSubTypes.Type(name = "permission_denied", value = PermissionDeniedErrorDetails.class),
        @JsonSubTypes.Type(name = "invalid_data", value = InvalidDataErrorDetails.class),
        @JsonSubTypes.Type(name = "file_not_found", value = FileNotFoundErrorDetails.class),
})
public class ErrorDetails {
    private String message;

    public ErrorDetails(String message) {
        this.message = message;
    }

    public ErrorDetails() {
    }

    public String getMessage() {
        return message;
    }


}
