package com.infra.gummadibuilt.pqform.model.dto;

import com.infra.gummadibuilt.pqform.model.PqFormHeader;
import lombok.Data;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;

import static com.infra.gummadibuilt.common.util.CommonModuleUtils.DATE_PATTERN;

@Data
public class PqFormHeaderDto extends PqFormHeaderCreateDto implements Serializable {

    private int id;

    private String pqDocumentIssueDate;

    private String tenderSubmissionDate;

    private String tenderId;

    private int applicationFormId;

    public static PqFormHeaderDto valueOf(PqFormHeader pqFormHeader) {
        PqFormHeaderDto result = new PqFormHeaderDto();
        result.setId(pqFormHeader.getId());
        if (pqFormHeader.getPqDocumentIssueDate() != null) {
            result.setPqDocumentIssueDate(pqFormHeader.getPqDocumentIssueDate().format(DateTimeFormatter.ofPattern(DATE_PATTERN)));
        }
        result.setPqLastDateOfSubmission(pqFormHeader.getPqLastDateOfSubmission().format(DateTimeFormatter.ofPattern(DATE_PATTERN)));
        result.setTentativeDateOfAward(pqFormHeader.getTentativeDateOfAward().format(DateTimeFormatter.ofPattern(DATE_PATTERN)));
        result.setScheduledCompletion(pqFormHeader.getScheduledCompletion().format(DateTimeFormatter.ofPattern(DATE_PATTERN)));
        result.setTenderId(pqFormHeader.getTenderInfo().getId());
        result.setWorkflowStep(pqFormHeader.getTenderInfo().getWorkflowStep());
        return result;
    }
}
