package com.infra.gummadibuilt.tender.model;

import com.infra.gummadibuilt.common.options.CommonOptionsEnum;

public enum WorkflowStep implements CommonOptionsEnum {

    SAVE("SAVE"),
    YET_TO_BE_PUBLISHED("Yet to be published"),
    PUBLISHED("Published"),
    UNDER_PROCESS("Under Process"),
    SHORTLISTED("SHORTLISTED"),
    NOT_SHORTLISTED("Not Shortlisted"),
    RECOMMENDED("Recommended"),
    ARCHIVED("Archived"),
    SUSPENDED("Suspended");

    private final String text;

    WorkflowStep(String text) {
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }

}
