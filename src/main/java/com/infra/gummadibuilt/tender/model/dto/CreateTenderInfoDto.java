package com.infra.gummadibuilt.tender.model.dto;


import com.fasterxml.jackson.databind.JsonNode;
import com.infra.gummadibuilt.tender.model.DurationCounter;
import com.infra.gummadibuilt.tender.model.TenderInfo;
import com.infra.gummadibuilt.tender.model.WorkflowStep;
import lombok.Data;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * A DTO for the {@link TenderInfo} entity
 */

@Data
public class CreateTenderInfoDto implements Serializable {

    @NotBlank
    @Size(max = 255)
    private String typeOfWork;

    @NotBlank
    @Size(max = 50)
    private String projectName;

    @NotBlank
    @Size(max = 2500)
    private String workDescription;

    @NotBlank
    @Size(max = 50)
    private String projectLocation;

    @NotNull
    private int typeOfContract;

    @NotNull
    @Max(99999)
    private int contractDuration;

    private DurationCounter durationCounter;

    @NotBlank
    private String lastDateOfSubmission;

    @Digits(integer = 20, fraction = 0)
    private BigDecimal estimatedBudget;

    private WorkflowStep workflowStep;

    @NotNull
    private JsonNode tenderFinanceInfo;

}
