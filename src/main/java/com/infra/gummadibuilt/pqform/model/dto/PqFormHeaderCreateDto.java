package com.infra.gummadibuilt.pqform.model.dto;

import com.infra.gummadibuilt.tender.model.WorkflowStep;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
public class PqFormHeaderCreateDto implements Serializable {

    @Size(max = 255)
    @NotBlank
    private String projectName;

    @Size(max = 255)
    @NotBlank
    private String typeOfStructure;

    @NotNull
    private String pqLastDateOfSubmission;

    @NotNull
    private String tentativeDateOfAward;

    @NotNull
    private String scheduledCompletion;

    private WorkflowStep workflowStep;
}
