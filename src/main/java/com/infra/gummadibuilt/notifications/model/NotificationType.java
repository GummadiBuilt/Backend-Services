package com.infra.gummadibuilt.notifications.model;

import com.infra.gummadibuilt.common.options.CommonOptionsEnum;

public enum NotificationType implements CommonOptionsEnum {

    ACCESS_REQUEST("Access"),

    TENDER_PQ("TENDER_PQ"),
    TENDER_PUBLISHED("TENDER_PUBLISHED"),
    TENDER_SHORT_LISTED("Shortlisted"),

    DONE_RECOMMENDED("DONE_RECOMMENDED");

    private final String text;

    NotificationType(String text) {
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }

}