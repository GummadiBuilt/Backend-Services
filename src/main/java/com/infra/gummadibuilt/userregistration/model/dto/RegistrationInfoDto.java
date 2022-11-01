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

    @NotBlank
    @Size(max = 75)
    private String firstName;

    @NotBlank
    @Size(max = 75)
    private String lastName;

    @NotBlank
    @Size(max = 75)
    private String email;

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

    public static RegistrationInfoDto valueOf(UserRegistration userRegistration) {
        RegistrationInfoDto result = new RegistrationInfoDto();
        result.setId(userRegistration.getId());
        result.setApplicationRole(ApplicationRoleDto.valueOf(userRegistration.getApplicationRole()));
        result.setFirstName(userRegistration.getFirstName());
        result.setLastName(userRegistration.getLastName());
        result.setAddress(userRegistration.getAddress());
        result.setCompanyName(userRegistration.getCompanyName());
        result.setEmail(userRegistration.getEmail());
        result.setYearOfEstablishment(userRegistration.getYearOfEstablishment());
        result.setTypeOfEstablishment(userRegistration.getTypeOfEstablishment());
        result.setCountry(CountryDto.valueOf(userRegistration.getCountry()));
        result.setState(StateDto.valueOf(userRegistration.getState()));
        result.setCity(CityDto.valueOf(userRegistration.getCity()));
        result.setContactName(userRegistration.getContactName());
        result.setContactPhoneNumber(userRegistration.getContactPhoneNumber());
        result.setContactDesignation(userRegistration.getContactDesignation());
        result.setContactEmailAddress(userRegistration.getContactEmailAddress());
        result.setCoordinatorName(userRegistration.getCoordinatorName());
        result.setCoordinatorMobileNumber(userRegistration.getCoordinatorMobileNumber());


        return result;
    }

}