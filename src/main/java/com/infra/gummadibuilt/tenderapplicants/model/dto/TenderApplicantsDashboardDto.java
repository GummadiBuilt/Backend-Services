package com.infra.gummadibuilt.tenderapplicants.model.dto;

import software.amazon.awssdk.services.s3.endpoints.internal.Value;

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
}
