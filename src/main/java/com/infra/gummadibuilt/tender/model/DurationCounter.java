package com.infra.gummadibuilt.tender.model;

import com.infra.gummadibuilt.common.options.CommonOptionsEnum;

public enum DurationCounter implements CommonOptionsEnum {

    MONTHS("MONTHS"),
    DAYS("DAYS");

    private final String text;

    DurationCounter(String text) {
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }

}
