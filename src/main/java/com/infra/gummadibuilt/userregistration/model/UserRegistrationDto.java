package com.infra.gummadibuilt.userregistration.model;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * A DTO for the {@link UserRegistration} entity
 */
@Data
public class UserRegistrationDto implements Serializable {

    @NotBlank
    @Size(max = 75)
    private String firstName;

    @NotBlank
    @Size(max = 75)
    private String lastName;

    @NotBlank
    @Size(max = 75)
    private String email;

    private int applicationRoleId;

    @NotBlank
    @Size(max = 100)
    private String companyName;

    @NotNull
    @Max(9999)
    private int yearOfEstablishment;

    private List<String> typeOfEstablishment;

    @NotBlank
    @Size(max = 500)
    private String address;

    @Size(min = 1, max = 3)
    private String countryCountryIsoCode;

    @Size(min = 1, max = 3)
    private String stateStateIsoCode;

    private int cityId;

    @NotBlank
    @Size(max = 150)
    private String contactName;

    @NotBlank
    @Size(max = 150)
    private String contactDesignation;

    @NotBlank
    @Size(max = 10)
    private String contactPhoneNumber;

    @NotBlank
    @Size(max = 100)
    private String contactEmailAddress;

    @NotBlank
    @Size(max = 150)
    private String coordinatorName;

    @NotBlank
    @Size(max = 10)
    private String coordinatorMobileNumber;
}