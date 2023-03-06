package com.infra.gummadibuilt.payment.model.dto;

import com.infra.gummadibuilt.userandrole.model.dto.ApplicationRoleDto;
import lombok.Data;

@Data
public class TenderPayUserDto {

    private String companyName;

    private String contactName;

    private String contactPhoneNumber;

    private String contactEmailAddress;
    private String contactAddress;
    private ApplicationRoleDto applicationRole;
}
