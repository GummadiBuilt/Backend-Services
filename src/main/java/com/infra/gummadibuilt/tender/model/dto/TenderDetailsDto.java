package com.infra.gummadibuilt.tender.model.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infra.gummadibuilt.common.ChangeTracking;
import com.infra.gummadibuilt.masterdata.common.model.dto.TypeOfEstablishmentDto;
import com.infra.gummadibuilt.tender.model.TenderInfo;
import com.infra.gummadibuilt.tenderapplicationform.model.dto.ActionTaken;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

import static com.infra.gummadibuilt.common.util.CommonModuleUtils.DATE_PATTERN;

@Data
public class TenderDetailsDto implements Serializable {

    @Schema(name = "Tender ID", defaultValue = "123")
    @NotBlank
    private String tenderId;

    @NotBlank
    @Size(max = 50)
    private String projectName;

    private String clientInformation;

    @NotBlank
    @Size(max = 255)
    private TypeOfEstablishmentDto typeOfWork;

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
    private String lastDateOfSubmission;

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

    private int pqFormId;

    private int applicationFormId;

    private ActionTaken applicationFormStatus;

    private ChangeTracking changeTracking;

    private String contractorDocumentName;

    private long contactorDocumentSize;

    public static TenderDetailsDto valueOf(TenderInfo tenderInfo, boolean showBidInfo) {
        TenderDetailsDto result = new TenderDetailsDto();
        result.setTenderId(tenderInfo.getId());
        result.setProjectName(tenderInfo.getProjectName());
        result.setTypeOfWork(TypeOfEstablishmentDto.valueOf(tenderInfo.getTypeOfEstablishment()));
        result.setWorkDescription(tenderInfo.getWorkDescription());
        result.setProjectLocation(tenderInfo.getProjectLocation());
        result.setTypeOfContract(TypeOfContractDto.valueOf(tenderInfo.getTypeOfContract()));
        result.setContractDuration(tenderInfo.getContractDuration());
        result.setDurationCounter(tenderInfo.getDurationCounter().getText());
        result.setLastDateOfSubmission(
                tenderInfo.getLastDateOfSubmission().format(DateTimeFormatter.ofPattern(DATE_PATTERN))
        );
        result.setEstimatedBudget(tenderInfo.getEstimatedBudget());
        result.setWorkflowStep(tenderInfo.getWorkflowStep().getText());

        result.setClientInformation(tenderInfo.getApplicationUser().getCompanyName());
        if (showBidInfo) {
            result.setTenderDocumentName(tenderInfo.getTenderDocumentName());
            result.setTenderDocumentSize(tenderInfo.getTenderDocumentSize());
            result.setTenderFinanceInfo(tenderInfo.getTenderFinanceInfo());
        } else {
            result.setTenderDocumentName("-");
            result.setTenderDocumentSize(0);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode financeInfo = mapper.createObjectNode();
            result.setTenderFinanceInfo(financeInfo);
        }
        result.setChangeTracking(tenderInfo.getChangeTracking());

        if (tenderInfo.getFormHeader() != null) {
            result.setPqFormId(tenderInfo.getFormHeader().getId());
        }

        return result;
    }
}
