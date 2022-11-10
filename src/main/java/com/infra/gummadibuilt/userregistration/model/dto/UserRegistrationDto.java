package com.infra.gummadibuilt.userregistration.model.dto;

import com.infra.gummadibuilt.userregistration.model.UserRegistration;
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

    private int id;

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

    public static UserRegistrationDto valueOf(UserRegistration userRegistration) {
        UserRegistrationDto result = new UserRegistrationDto();
        result.setId(userRegistration.getId());
        result.setAddress(userRegistration.getAddress());
        result.setCompanyName(userRegistration.getCompanyName());
        result.setYearOfEstablishment(userRegistration.getYearOfEstablishment());
        result.setTypeOfEstablishment(userRegistration.getTypeOfEstablishment());
        result.setCountryCountryIsoCode(userRegistration.getCountry().getCountryIsoCode());
        result.setStateStateIsoCode(userRegistration.getState().getStateIsoCode());
        result.setCityId(userRegistration.getCity().getId());
        result.setContactFirstName(userRegistration.getContactFirstName());
        result.setContactLastName(userRegistration.getContactLastName());
        result.setContactPhoneNumber(userRegistration.getContactPhoneNumber());
        result.setContactDesignation(userRegistration.getContactDesignation());
        result.setContactEmailAddress(userRegistration.getContactEmailAddress());

        return result;
    }

}