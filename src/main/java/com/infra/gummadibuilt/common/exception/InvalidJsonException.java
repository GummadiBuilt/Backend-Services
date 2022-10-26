package com.infra.gummadibuilt.common.exception;
/**
 * This class is used for invalid json exception.

 */
public class InvalidJsonException extends RuntimeException {
	 private static final long serialVersionUID = 1L;

	    public InvalidJsonException(String msg) {
	        super(msg);
	    }

}
