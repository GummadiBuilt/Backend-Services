package com.infra.gummadibuilt.pqform;


import com.infra.gummadibuilt.common.exception.UniqueConstraintException;

/**
 * Thrown when PQ Form header is already for a Tender ID
 */
public class PqFormAlreadyExistsException extends UniqueConstraintException {

    public PqFormAlreadyExistsException() {
        super("tenderInfoId");
    }

}
