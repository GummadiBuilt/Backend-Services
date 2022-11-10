package com.infra.gummadibuilt.userregistration.model.dto;

import com.infra.gummadibuilt.masterdata.geography.model.dto.CityDto;
import com.infra.gummadibuilt.masterdata.geography.model.dto.CountryDto;
import com.infra.gummadibuilt.masterdata.geography.model.dto.StateDto;
import com.infra.gummadibuilt.userandrole.model.dto.ApplicationRoleDto;
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
public class RegistrationInfoDto implements Serializable {

    private int id;

    private ApplicationRoleDto applicationRole;

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
    private CountryDto country;

    @Size(min = 1, max = 3)
    private StateDto state;

    private CityDto city;

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

    public static RegistrationInfoDto valueOf(UserRegistration userRegistration) {
        RegistrationInfoDto result = new RegistrationInfoDto();
        result.setId(userRegistration.getId());
        result.setApplicationRole(ApplicationRoleDto.valueOf(userRegistration.getApplicationRole()));
        result.setAddress(userRegistration.getAddress());
        result.setCompanyName(userRegistration.getCompanyName());
        result.setYearOfEstablishment(userRegistration.getYearOfEstablishment());
        result.setTypeOfEstablishment(userRegistration.getTypeOfEstablishment());
        result.setCountry(CountryDto.valueOf(userRegistration.getCountry()));
        result.setState(StateDto.valueOf(userRegistration.getState()));
        result.setCity(CityDto.valueOf(userRegistration.getCity()));
        result.setContactFirstName(userRegistration.getContactFirstName());
        result.setContactLastName(userRegistration.getContactLastName());
        result.setContactPhoneNumber(userRegistration.getContactPhoneNumber());
        result.setContactDesignation(userRegistration.getContactDesignation());
        result.setContactEmailAddress(userRegistration.getContactEmailAddress());

        return result;
    }

}