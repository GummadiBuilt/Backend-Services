package com.infra.gummadibuilt.userandrole;

import com.infra.gummadibuilt.common.LoggedInUser;
import com.infra.gummadibuilt.common.util.SaveEntityConstraintHelper;
import com.infra.gummadibuilt.masterdata.geography.CityDao;
import com.infra.gummadibuilt.masterdata.geography.CountryDao;
import com.infra.gummadibuilt.masterdata.geography.StateDao;
import com.infra.gummadibuilt.masterdata.geography.model.City;
import com.infra.gummadibuilt.masterdata.geography.model.Country;
import com.infra.gummadibuilt.masterdata.geography.model.State;
import com.infra.gummadibuilt.userandrole.model.ApplicationUser;
import com.infra.gummadibuilt.userandrole.model.dto.UserDetailsUpdateDto;
import com.infra.gummadibuilt.userandrole.model.dto.UserDetailsViewDto;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import static com.infra.gummadibuilt.common.util.CommonModuleUtils.*;
import static com.infra.gummadibuilt.common.util.UserInfo.loggedInUserInfo;

@Service
public class UserProfileService {

    @Autowired
    public ApplicationUserDao applicationUserDao;

    @Autowired
    public CountryDao countryDao;

    @Autowired
    public CityDao cityDao;

    @Autowired
    public StateDao stateDao;


    public UserDetailsViewDto getProfile(HttpServletRequest request) {
        KeycloakAuthenticationToken principal = (KeycloakAuthenticationToken) request.getUserPrincipal();
        String userId = principal.getAccount().getKeycloakSecurityContext().getToken().getSubject();
        ApplicationUser applicationUser = getById(applicationUserDao, userId, USER_NOT_FOUND);
        return UserDetailsViewDto.valueOf(applicationUser);
    }

    public UserDetailsViewDto getUserProfile(String userId) {
        ApplicationUser applicationUser = getById(applicationUserDao, userId, USER_NOT_FOUND);
        return UserDetailsViewDto.valueOf(applicationUser);
    }

    public UserDetailsViewDto updateProfile(HttpServletRequest request, UserDetailsUpdateDto userDetails) {
        KeycloakAuthenticationToken principal = (KeycloakAuthenticationToken) request.getUserPrincipal();
        String userId = principal.getAccount().getKeycloakSecurityContext().getToken().getSubject();
        ApplicationUser applicationUser = getById(applicationUserDao, userId, USER_NOT_FOUND);

        Country country = getById(countryDao, userDetails.getCountryIsoCode(), COUNTRY_ID_NOT_FOUND);
        State state = getById(stateDao, userDetails.getStateIsoCode(), STATE_ID_NOT_FOUND);
        City city = getById(cityDao, userDetails.getCityId(), CITY_ID_NOT_FOUND);

        LoggedInUser loggedInUser = loggedInUserInfo(request);


        if (request.isUserInRole("contractor") && userDetails.getTypeOfEstablishment().size() == 0) {
            throw new RuntimeException("Contractors need to have at-least one Type Of Establishment");
        }

        applicationUser.setCity(city);
        applicationUser.setCountry(country);
        applicationUser.setState(state);
        applicationUser.setCompanyName(userDetails.getCompanyName());
        applicationUser.setYearOfEstablishment(userDetails.getYearOfEstablishment());
        applicationUser.setTypeOfEstablishment(userDetails.getTypeOfEstablishment());
        applicationUser.setAddress(userDetails.getAddress());
        applicationUser.setContactFirstName(userDetails.getContactFirstName());
        applicationUser.setContactLastName(userDetails.getContactLastName());
        applicationUser.setContactDesignation(userDetails.getContactDesignation());
        applicationUser.setContactPhoneNumber(userDetails.getContactPhoneNumber());
        applicationUser.getChangeTracking().update(loggedInUser.toString());

        SaveEntityConstraintHelper.save(applicationUserDao, applicationUser, null);

        return UserDetailsViewDto.valueOf(applicationUser);
    }

}
