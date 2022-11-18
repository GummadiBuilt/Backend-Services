package com.infra.gummadibuilt.tender.model.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.infra.gummadibuilt.common.ChangeTracking;
import com.infra.gummadibuilt.tender.model.TenderInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class TenderDetailsDto implements Serializable {

    @Schema(name = "Tender ID", defaultValue = "123")
    @NotBlank
    private String tenderId;

    @NotBlank
    private List<String> typeOfWork;

    @NotBlank
    @Size(max = 50)
    private String workDescription;

    @NotBlank
    @Size(max = 50)
    private String projectLocation;

    @NotNull
    private TypeOfContractDto typeOfContract;

    @NotNull
    @Max(99999)
    private int contractDuration;

    private String durationCounter;

    @NotBlank
    private LocalDate lastDateOfSubmission;

    @Digits(integer = 20, fraction = 0)
    private BigDecimal estimatedBudget;

    private String workflowStep;

    @NotNull
    private JsonNode tenderFinanceInfo;

    @NotBlank
    @Size(max = 50)
    private String tenderDocumentName;

    // Document size in MB
    @NotBlank
    private long tenderDocumentSize;

    private ChangeTracking changeTracking;

    public static TenderDetailsDto valueOf(TenderInfo tenderInfo) {
        TenderDetailsDto result = new TenderDetailsDto();
        result.setTenderId(tenderInfo.getId());
        result.setTypeOfWork(tenderInfo.getTypeOfWork());
        result.setWorkDescription(tenderInfo.getWorkDescription());
        result.setProjectLocation(tenderInfo.getProjectLocation());
        result.setTypeOfContract(TypeOfContractDto.valueOf(tenderInfo.getTypeOfContract()));
        result.setContractDuration(tenderInfo.getContractDuration());
        result.setDurationCounter(tenderInfo.getDurationCounter().getText());
        result.setLastDateOfSubmission(tenderInfo.getLastDateOfSubmission());
        result.setEstimatedBudget(tenderInfo.getEstimatedBudget());
        result.setWorkflowStep(tenderInfo.getWorkflowStep().getText());
        result.setTenderDocumentName(tenderInfo.getTenderDocumentName());
        result.setTenderDocumentSize(tenderInfo.getTenderDocumentSize());
        result.setTenderFinanceInfo(tenderInfo.getTenderFinanceInfo());
        result.setChangeTracking(tenderInfo.getChangeTracking());

        return result;
    }
}
