package com.infra.gummadibuilt.tenderapplicationform.model.dto;

import com.infra.gummadibuilt.common.options.CommonOptionsEnum;

public enum ActionTaken implements CommonOptionsEnum {

    DRAFT("Draft"),
    SUBMIT("Submit");

    private final String text;

    ActionTaken(String text) {
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }

}
