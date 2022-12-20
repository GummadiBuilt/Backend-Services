package com.infra.gummadibuilt.admin.client.dto;

public interface ClientUserInfoDto {
    String getId();

    String getCompany_name();

    String getContact_email_address();

    String getContact_first_name();

    String getContact_last_name();

    String getRole_name();

    int getSave_step();

    int getPublish_step();

    int getYet_to_publish_step();

    int getUnder_process_step();

    int getRecommended_step();

    int getSuspended_step();

    int getTotal_tenders();
}
