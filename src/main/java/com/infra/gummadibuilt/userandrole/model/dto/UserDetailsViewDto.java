package com.infra.gummadibuilt.userandrole.model.dto;

import com.infra.gummadibuilt.common.ChangeTracking;
import com.infra.gummadibuilt.masterdata.geography.model.dto.CityDto;
import com.infra.gummadibuilt.masterdata.geography.model.dto.CountryDto;
import com.infra.gummadibuilt.masterdata.geography.model.dto.StateDto;
import com.infra.gummadibuilt.userandrole.model.ApplicationUser;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserDetailsViewDto extends UserDetailsDto{

    private CountryDto country;

    private StateDto state;

    private CityDto city;

    public static UserDetailsViewDto valueOf(ApplicationUser applicationUser) {

        UserDetailsViewDto result = new UserDetailsViewDto();
        result.setId(applicationUser.getId());
        result.setApplicationRoleDto(ApplicationRoleDto.valueOf(applicationUser.getApplicationRole()));
        result.setCompanyName(applicationUser.getCompanyName());
        result.setYearOfEstablishment(applicationUser.getYearOfEstablishment());
        result.setTypeOfEstablishment(applicationUser.getTypeOfEstablishment());
        result.setAddress(applicationUser.getAddress());
        result.setCountry(CountryDto.valueOf(applicationUser.getCountry()));
        result.setCity(CityDto.valueOf(applicationUser.getCity()));
        result.setState(StateDto.valueOf(applicationUser.getState()));
        result.setContactFirstName(applicationUser.getContactFirstName());
        result.setContactLastName(applicationUser.getContactLastName());
        result.setContactDesignation(applicationUser.getContactDesignation());
        result.setContactPhoneNumber(applicationUser.getContactPhoneNumber());
        result.setContactEmailAddress(applicationUser.getContactEmailAddress());
        result.setChangeTracking(applicationUser.getChangeTracking());

        return result;
    }

}
