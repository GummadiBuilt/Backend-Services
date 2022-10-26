package com.infra.gummadibuilt.common.exception;

public class InValidFileUploadedException extends RuntimeException {
	 private static final long serialVersionUID = 4794415318129531423L;

	    public InValidFileUploadedException() {
	        super("Invalid File.");
	    }

	    public InValidFileUploadedException(String message) {
	        super(message);
	    }
}
