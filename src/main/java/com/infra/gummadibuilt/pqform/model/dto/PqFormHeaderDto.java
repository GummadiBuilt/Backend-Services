package com.infra.gummadibuilt.pqform.model.dto;

import com.infra.gummadibuilt.pqform.model.PqFormHeader;
import com.infra.gummadibuilt.tender.model.DurationCounter;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

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

    private LocalDate pqDocumentIssueDate;

    @NotNull
    private LocalDate pqLastDateOfSubmission;

    @NotNull
    private LocalDate tentativeDateOfAward;

    @NotNull
    private LocalDate scheduledCompletion;

    public static PqFormHeaderDto valueOf(PqFormHeader pqFormHeader) {
        PqFormHeaderDto result = new PqFormHeaderDto();
        result.setId(pqFormHeader.getId());
        result.setProjectName(pqFormHeader.getProjectName());
        result.setWorkPackage(pqFormHeader.getWorkPackage());
        result.setTypeOfStructure(pqFormHeader.getTypeOfStructure());
        result.setContractDuration(pqFormHeader.getContractDuration());
        result.setDurationCounter(pqFormHeader.getDurationCounter());
        result.setPqDocumentIssueDate(pqFormHeader.getPqDocumentIssueDate());
        result.setPqLastDateOfSubmission(pqFormHeader.getPqLastDateOfSubmission());
        result.setTentativeDateOfAward(pqFormHeader.getTentativeDateOfAward());
        result.setScheduledCompletion(pqFormHeader.getScheduledCompletion());

        return result;
    }
}
