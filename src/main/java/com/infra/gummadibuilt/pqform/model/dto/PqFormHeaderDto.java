package com.infra.gummadibuilt.pqform.model.dto;

import com.infra.gummadibuilt.pqform.model.PqFormHeader;
import com.infra.gummadibuilt.tender.model.DurationCounter;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.format.DateTimeFormatter;

import static com.infra.gummadibuilt.common.util.CommonModuleUtils.DATE_PATTERN;

@Data
public class PqFormHeaderDto {

    private int id;

    @Size(max = 255)
    @NotBlank
    private String projectName;

    @NotBlank
    @Size(max = 50)
    private String workPackage;

    @Size(max = 255)
    @NotBlank
    private String typeOfStructure;

    private int contractDuration;

    @Enumerated(EnumType.STRING)
    private DurationCounter durationCounter;

    private String pqDocumentIssueDate;

    @NotNull
    private String pqLastDateOfSubmission;

    @NotNull
    private String tentativeDateOfAward;

    @NotNull
    private String scheduledCompletion;

    public static PqFormHeaderDto valueOf(PqFormHeader pqFormHeader) {
        PqFormHeaderDto result = new PqFormHeaderDto();
        result.setId(pqFormHeader.getId());
        result.setProjectName(pqFormHeader.getProjectName());
        result.setTypeOfStructure(pqFormHeader.getTypeOfStructure());
        if (pqFormHeader.getPqDocumentIssueDate() != null) {
            result.setPqDocumentIssueDate(pqFormHeader.getPqDocumentIssueDate().format(DateTimeFormatter.ofPattern(DATE_PATTERN)));
        }
        result.setPqLastDateOfSubmission(pqFormHeader.getPqLastDateOfSubmission().format(DateTimeFormatter.ofPattern(DATE_PATTERN)));
        result.setTentativeDateOfAward(pqFormHeader.getTentativeDateOfAward().format(DateTimeFormatter.ofPattern(DATE_PATTERN)));
        result.setScheduledCompletion(pqFormHeader.getScheduledCompletion().format(DateTimeFormatter.ofPattern(DATE_PATTERN)));
        result.setWorkPackage(pqFormHeader.getWorkPackage());
        result.setContractDuration(pqFormHeader.getContractDuration());
        result.setDurationCounter(pqFormHeader.getDurationCounter());
        return result;
    }
}
