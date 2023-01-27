package com.infra.gummadibuilt.tenderapplicants.model.dto;

import com.fasterxml.jackson.databind.JsonNode;

public interface TenderApplicantsDashboardDto {

    int getId();

    int getApplicant_rank();

    String getApplication_status();

    String getTender_info_id();

    int getApplicant_form_id();

    String getApplication_user_id();

    String getCompany_name();

    String getJustification_note();

    String getModified_by();

    String getModified_date();

    String getWorkflow_step();

    String getTender_document();

    int getTender_document_size();

    String getTender_finance_info();
}
