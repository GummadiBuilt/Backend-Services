package com.infra.gummadibuilt.common.exception;

/**
 * This class is used for invalid data submitted exception.
 */
public class InValidDataSubmittedException extends RuntimeException {

    private static final long serialVersionUID = 4794415318129531423L;

    public InValidDataSubmittedException() {
        super("Invalid data submitted. Please fill all mandatory fields");
    }

    public InValidDataSubmittedException(String message) {
        super(message);
    }

}
