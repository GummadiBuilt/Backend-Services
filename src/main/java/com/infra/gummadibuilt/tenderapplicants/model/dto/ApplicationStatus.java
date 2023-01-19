package com.infra.gummadibuilt.tenderapplicants.model.dto;

import com.infra.gummadibuilt.common.options.CommonOptionsEnum;

public enum ApplicationStatus implements CommonOptionsEnum {

    QUALIFIED("Qualified"),
    UNDER_PROCESS("Under process"),
    NOT_QUALIFIED("Not Qualified");

    private final String text;

    ApplicationStatus(String text) {
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }

}
