package com.infra.gummadibuilt.tender.model.dto;

public interface TenderDashboardProjection {

    String getProject_name();

    String getTender_id();

    String getApplication_form_id();

    String getPq_id();

    String getCompany_name();

    String getEstablishment_description();

    String getWork_description();

    String getProject_location();

    String getType_of_contract();

    int getContract_duration();

    String getDuration_counter();

    String getLast_date_of_submission();

    long getEstimated_budget();

    String getWorkflow_step();

    String getTender_document_name();

    long getTender_document_size();

    String getCreated_by();


}
