package com.infra.gummadibuilt.common;

import com.infra.gummadibuilt.common.options.CommonOptionsEnum;

public enum ApproveReject implements CommonOptionsEnum {

    APPROVE("APPROVE"),
    REJECT("REJECT"),
    IN_REVIEW("IN REVIEW");

    private final String text;

    ApproveReject(String text) {
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }

}
