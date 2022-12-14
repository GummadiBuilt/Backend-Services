package com.infra.gummadibuilt.tenderapplicationform.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ApplicationFormDto extends ApplicationFormCreateDto implements Serializable {

    private int applicationId;

    private String tenderId;

    private String lastDateOfSubmission;

}
