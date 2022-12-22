package com.infra.gummadibuilt.userandrole.model.dto;

import com.infra.gummadibuilt.common.ChangeTracking;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class UserDetailsDto {

    @Size(max = 50)
    private String id;

    private ApplicationRoleDto applicationRoleDto;

    private String companyName;

    @NotNull
    @Max(9999)
    private int yearOfEstablishment;

    private List<String> typeOfEstablishment;

    @NotBlank
    @Size(max = 500)
    private String address;

    @NotBlank
    @Size(max = 75)
    private String contactFirstName;

    @NotBlank
    @Size(max = 75)
    private String contactLastName;

    @NotBlank
    @Size(max = 150)
    private String contactDesignation;

    @NotBlank
    @Size(max = 10)
    private String contactPhoneNumber;

    @NotBlank
    @Size(max = 100)
    private String contactEmailAddress;

    private ChangeTracking changeTracking;
}
