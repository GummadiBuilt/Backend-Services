package com.infra.gummadibuilt.tenderapplicants.model.dto;

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

        return tenderApplicantsDto;

    }
}
