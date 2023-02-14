package com.infra.gummadibuilt.tenderapplicants.model.dto;

import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;

public interface TenderApplicantsDashboardDto {

    int getId();

    BigDecimal getApplicant_rank();

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

    float getFinancial_info();

    String getLocal_office_address();

    String getTurn_over_details();

    String getTender_finance_info();

    String getClient_references();

    Boolean getIs_recommended();
}
