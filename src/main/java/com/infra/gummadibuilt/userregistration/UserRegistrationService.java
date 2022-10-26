package com.infra.gummadibuilt.userregistration;

import com.infra.gummadibuilt.common.ApproveReject;
import com.infra.gummadibuilt.common.ChangeTracking;
import com.infra.gummadibuilt.common.LoggedInUser;
import com.infra.gummadibuilt.common.util.SaveEntityConstraintHelper;
import com.infra.gummadibuilt.masterdata.geography.CityDao;
import com.infra.gummadibuilt.masterdata.geography.CountryDao;
import com.infra.gummadibuilt.masterdata.geography.StateDao;
import com.infra.gummadibuilt.masterdata.geography.model.City;
import com.infra.gummadibuilt.masterdata.geography.model.Country;
import com.infra.gummadibuilt.masterdata.geography.model.State;
import com.infra.gummadibuilt.userandrole.ApplicationRoleDao;
import com.infra.gummadibuilt.userandrole.model.ApplicationRole;
import com.infra.gummadibuilt.userregistration.model.UserRegistration;
import com.infra.gummadibuilt.userregistration.model.UserRegistrationDto;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import static com.infra.gummadibuilt.common.util.CommonModuleUtils.*;

@Service
public class UserRegistrationService {

    private final UserRegistrationDao userRegistrationDao;

    private final CountryDao countryDao;

    private final StateDao stateDao;

    private final CityDao cityDao;

    private final ApplicationRoleDao applicationRoleDao;

    public UserRegistrationService(UserRegistrationDao userRegistrationDao,
                                   CountryDao countryDao,
                                   StateDao stateDao,
                                   CityDao cityDao,
                                   ApplicationRoleDao applicationRoleDao) {
        this.userRegistrationDao = userRegistrationDao;
        this.countryDao = countryDao;
        this.stateDao = stateDao;
        this.cityDao = cityDao;
        this.applicationRoleDao = applicationRoleDao;
    }

    public UserRegistrationDto registerUser(UserRegistrationDto userRegistrationDto) {

        LoggedInUser loggedInUser = new LoggedInUser(userRegistrationDto.getFirstName(), userRegistrationDto.getLastName());

        Country country = getById(countryDao, userRegistrationDto.getCountryCountryIsoCode(), COUNTRY_ID_NOT_FOUND);
        State state = getById(stateDao, userRegistrationDto.getStateStateIsoCode(), STATE_ID_NOT_FOUND);
        City city = getById(cityDao, userRegistrationDto.getCityId(), CITY_ID_NOT_FOUND);
        ApplicationRole applicationRole = getById(applicationRoleDao, userRegistrationDto.getApplicationRoleId(), ROLE_NOT_FOUND);

        UserRegistration userRegistration = new UserRegistration();
        BeanUtils.copyProperties(userRegistrationDto, userRegistration);
        userRegistration.setApproveReject(ApproveReject.IN_REVIEW);
        userRegistration.setCountry(country);
        userRegistration.setState(state);
        userRegistration.setCity(city);
        userRegistration.setApplicationRole(applicationRole);
        userRegistration.setChangeTracking(new ChangeTracking(loggedInUser.toString()));

        SaveEntityConstraintHelper.save(userRegistrationDao, userRegistration, null);

        return userRegistrationDto;
    }
}
