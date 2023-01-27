package com.infra.gummadibuilt.tenderapplicants.model.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class TenderApplicantsDto {

    private int id;

    @NotNull
    private int applicantRank;

    @Size(max = 2500)
    private String justificationNote;

    @Enumerated(value = EnumType.STRING)
    private ApplicationStatus applicationStatus;

    @NotNull
    private String applicationUserId;

    @NotNull
    private int applicationFormId;

    private String companyName;

    private String tenderId;

    private String tenderStatus;

    private String tenderDocument;

    private int tenderDocumentSize;

    private JsonNode tenderFinanceInfo;

    public static TenderApplicantsDto valueOf(TenderApplicantsDashboardDto dashboardDto) {
        TenderApplicantsDto tenderApplicantsDto = new TenderApplicantsDto();

        tenderApplicantsDto.setId(dashboardDto.getId());
        tenderApplicantsDto.setApplicantRank(dashboardDto.getApplicant_rank());
        tenderApplicantsDto.setJustificationNote(dashboardDto.getJustification_note());
        tenderApplicantsDto.setApplicationStatus(ApplicationStatus.valueOf(dashboardDto.getApplication_status()));
        tenderApplicantsDto.setApplicationFormId(dashboardDto.getApplicant_form_id());
        tenderApplicantsDto.setApplicationUserId(dashboardDto.getApplication_user_id());
        tenderApplicantsDto.setCompanyName(dashboardDto.getCompany_name());
        tenderApplicantsDto.setTenderId(dashboardDto.getTender_info_id());
        tenderApplicantsDto.setTenderStatus(dashboardDto.getWorkflow_step());
        tenderApplicantsDto.setTenderDocument(dashboardDto.getTender_document());
        tenderApplicantsDto.setTenderDocumentSize(dashboardDto.getTender_document_size());
        ObjectMapper mapper = new ObjectMapper();
        try {
            tenderApplicantsDto.setTenderFinanceInfo(mapper.readTree(dashboardDto.getTender_finance_info()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return tenderApplicantsDto;

    }
}
