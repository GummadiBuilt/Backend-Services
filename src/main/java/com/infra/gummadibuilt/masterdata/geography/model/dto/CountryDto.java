package com.infra.gummadibuilt.masterdata.geography.model.dto;

import com.infra.gummadibuilt.common.ChangeTracking;
import com.infra.gummadibuilt.masterdata.geography.model.Country;
import lombok.Data;

@Data
public class CountryDto {

    private String countryIsoCode;

    private String countryName;

    private ChangeTracking changeTracking;

    public static CountryDto valueOf(Country country) {
        CountryDto result = new CountryDto();
        result.setCountryIsoCode(country.getCountryIsoCode());
        result.setCountryName(country.getCountryName());
        result.setChangeTracking(country.getChangeTracking());

        return result;
    }
}
