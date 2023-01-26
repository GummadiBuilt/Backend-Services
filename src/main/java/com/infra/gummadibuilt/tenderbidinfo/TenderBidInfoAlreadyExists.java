package com.infra.gummadibuilt.tenderbidinfo;


import com.infra.gummadibuilt.common.exception.UniqueConstraintException;

/**
 * Thrown when an application form exist for a Tender ID
 */
public class TenderBidInfoAlreadyExists extends UniqueConstraintException {

    public TenderBidInfoAlreadyExists() {
        super("tenderInfoId");
    }
}
