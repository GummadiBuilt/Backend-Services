package com.infra.gummadibuilt.tenderapplicationform;


import com.infra.gummadibuilt.common.exception.UniqueConstraintException;

/**
 * Thrown when an application form exist for a Tender ID
 */
public class ApplicationFormAlreadyExistsException extends UniqueConstraintException {

    public ApplicationFormAlreadyExistsException() {
        super("tenderInfoId");
    }
}
