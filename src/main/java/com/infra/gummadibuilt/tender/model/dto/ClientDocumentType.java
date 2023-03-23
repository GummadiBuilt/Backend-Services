package com.infra.gummadibuilt.tender.model.dto;

import com.infra.gummadibuilt.common.options.CommonOptionsEnum;

public enum ClientDocumentType implements CommonOptionsEnum {

    FINANCE_DOC("FINANCE_DOC"),
    TECHNICAL_BID("TECHNICAL_BID");

    private final String text;

    ClientDocumentType(String text) {
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }

}
