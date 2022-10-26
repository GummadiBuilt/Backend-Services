package com.infra.gummadibuilt.common.exception;

public class BlobNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 4794415318129531423L;

    public BlobNotFoundException(String message) {
        super(message);
    }

}
