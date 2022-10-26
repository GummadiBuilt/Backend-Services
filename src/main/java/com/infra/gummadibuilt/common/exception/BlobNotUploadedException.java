package com.infra.gummadibuilt.common.exception;

public class BlobNotUploadedException extends RuntimeException {

    private static final long serialVersionUID = 4794415318129231423L;

    public BlobNotUploadedException(String message) {
        super(message);
    }

}
