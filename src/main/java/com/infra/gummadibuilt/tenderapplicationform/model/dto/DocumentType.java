package com.infra.gummadibuilt.tenderapplicationform.model.dto;

import com.infra.gummadibuilt.common.options.CommonOptionsEnum;

public enum DocumentType implements CommonOptionsEnum {

    ESI("ESI"),
    EPF("EPF"),
    GST("GST"),
    PAN("PAN"),
    YEAR_ONE("YEAR_ONE"),
    YEAR_TWO("YEAR_TWO"),
    YEAR_THREE("YEAR_THREE");

    private final String text;

    DocumentType(String text) {
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }

}
