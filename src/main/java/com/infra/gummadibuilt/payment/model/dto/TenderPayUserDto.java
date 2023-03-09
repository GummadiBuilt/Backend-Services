package com.infra.gummadibuilt.payment.model.dto;

import lombok.Data;

@Data
public class TenderPayUserDto {

    private String companyName;

    private String contactName;

    private String contactPhoneNumber;

    private String contactEmailAddress;
    private String contactAddress;
    private String applicationRoleName;
    private int applicationRoleId;
}
