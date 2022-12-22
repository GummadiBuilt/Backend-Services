package com.infra.gummadibuilt.pqform.model.dto;

import com.infra.gummadibuilt.tender.model.WorkflowStep;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class PqFormHeaderCreateDto implements Serializable {

    @NotNull
    private String pqLastDateOfSubmission;

    @NotNull
    private String tentativeDateOfAward;

    @NotNull
    private String scheduledCompletion;

    private WorkflowStep workflowStep;
}
