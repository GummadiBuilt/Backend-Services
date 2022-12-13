package com.infra.gummadibuilt.tenderapplicants.model.dto;

import com.infra.gummadibuilt.common.options.CommonOptionsEnum;

public enum ActionTaken implements CommonOptionsEnum {

    SAVE("Save"),
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
