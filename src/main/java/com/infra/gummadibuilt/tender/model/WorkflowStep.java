package com.infra.gummadibuilt.tender.model;

import com.infra.gummadibuilt.common.options.CommonOptionsEnum;

public enum WorkflowStep implements CommonOptionsEnum {

    DRAFT("Draft"),
    YET_TO_BE_PUBLISHED("Yet to be published"),
    PUBLISHED("Published"),
    UNDER_PROCESS("Under Process"),
    QUALIFIED("Qualified"),
    IN_REVIEW("In Review"),
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
