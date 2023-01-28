package com.infra.gummadibuilt.scheduler;

import com.infra.gummadibuilt.tender.model.WorkflowStep;
import lombok.Data;

@Data
public class ActionableTenders {

    private String tenderId;

    private String workflowStep;

    private String companyName;

    private String projectName;

    private String tenderLastDateOfSubmission;
}
