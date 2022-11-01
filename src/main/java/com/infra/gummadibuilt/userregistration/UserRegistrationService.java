package com.infra.gummadibuilt.userregistration;

import com.google.common.base.Joiner;
import com.infra.gummadibuilt.common.ApproveReject;
import com.infra.gummadibuilt.common.ChangeTracking;
import com.infra.gummadibuilt.common.LoggedInUser;
import com.infra.gummadibuilt.common.exception.InvalidActionException;
import com.infra.gummadibuilt.common.exception.UserExistsException;
import com.infra.gummadibuilt.common.util.PasswordGenerator;
import com.infra.gummadibuilt.common.util.SaveEntityConstraintHelper;
import com.infra.gummadibuilt.masterdata.geography.CityDao;
import com.infra.gummadibuilt.masterdata.geography.CountryDao;
import com.infra.gummadibuilt.masterdata.geography.StateDao;
import com.infra.gummadibuilt.masterdata.geography.model.City;
import com.infra.gummadibuilt.masterdata.geography.model.Country;
import com.infra.gummadibuilt.masterdata.geography.model.State;
import com.infra.gummadibuilt.userandrole.ApplicationRoleDao;
import com.infra.gummadibuilt.userandrole.ApplicationUserDao;
import com.infra.gummadibuilt.userandrole.model.ApplicationRole;
import com.infra.gummadibuilt.userandrole.model.ApplicationUser;
import com.infra.gummadibuilt.userregistration.model.UserRegistration;
import com.infra.gummadibuilt.userregistration.model.dto.ApproveRejectDto;
import com.infra.gummadibuilt.userregistration.model.dto.RegistrationInfoDto;
import com.infra.gummadibuilt.userregistration.model.dto.UserRegistrationDto;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.infra.gummadibuilt.common.util.CommonModuleUtils.*;

@Service
public class UserRegistrationService {

    private final UserRegistrationDao userRegistrationDao;

    private final CountryDao countryDao;

    private final StateDao stateDao;

    private final CityDao cityDao;

    private final ApplicationRoleDao applicationRoleDao;

    private final PasswordGenerator passwordGenerator;

    private final ApplicationUserDao applicationUserDao;

    public UserRegistrationService(UserRegistrationDao userRegistrationDao,
                                   CountryDao countryDao,
                                   StateDao stateDao,
                                   CityDao cityDao,
                                   ApplicationRoleDao applicationRoleDao,
                                   PasswordGenerator passwordGenerator,
                                   ApplicationUserDao applicationUserDao) {
        this.userRegistrationDao = userRegistrationDao;
        this.countryDao = countryDao;
        this.stateDao = stateDao;
        this.cityDao = cityDao;
        this.applicationRoleDao = applicationRoleDao;
        this.passwordGenerator = passwordGenerator;
        this.applicationUserDao = applicationUserDao;
    }

    public UserRegistrationDto registerUser(UserRegistrationDto userRegistrationDto) {

        String emailReceived = userRegistrationDto.getEmail();

        Optional<ApplicationUser> applicationUser = applicationUserDao.findByEmail(emailReceived);

        Optional<UserRegistration> pendingApproval = userRegistrationDao.findByEmail(emailReceived);

        if (applicationUser.isPresent()) {
            throw new UserExistsException(
                    String.format("Email address %s is already in use. Did you forgot password?", emailReceived)
            );
        }
        if (pendingApproval.isPresent()) {
            throw new UserExistsException(
                    String.format("A registration with email address %s is pending for approval", emailReceived)
            );
        }

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

    public List<RegistrationInfoDto> getPendingForApproval() {
        return userRegistrationDao.findAllByApproveReject(ApproveReject.IN_REVIEW)
                .stream().map(RegistrationInfoDto::valueOf).collect(Collectors.toList());

    }

    public List<RegistrationInfoDto> approveOrRejectRequests(ApproveRejectDto approveRejectDto) {

        List<Integer> requestIds = approveRejectDto.getRequestId();
        String approveReject = approveRejectDto.getActionTaken();

        LoggedInUser loggedInUser = new LoggedInUser("AB", "CD");
        List<UserRegistration> userRegistrations = userRegistrationDao.findAllById(requestIds);

        if (userRegistrations.size() != requestIds.size()) {
            List<Integer> notFoundIds = userRegistrations.stream()
                    .map(UserRegistration::getId)
                    .filter(id -> !requestIds.contains(id))
                    .collect(Collectors.toList());
            String missingIds = Joiner.on(",").join(notFoundIds);
            throw new RuntimeException(String.format("Couldn't find following requests %s", missingIds));
        }

        if (approveReject.equals(ApproveReject.APPROVE.getText())) {
            List<ApplicationUser> applicationUsers = userRegistrations.stream().map(user -> setUserInfo(user, loggedInUser)).collect(Collectors.toList());
            SaveEntityConstraintHelper.saveAll(applicationUserDao, applicationUsers, null);
            userRegistrations.forEach(item -> {
                item.setApproveReject(ApproveReject.APPROVE);
                item.getChangeTracking().update(loggedInUser.toString());
            });
        } else if (approveReject.equals(ApproveReject.REJECT.getText())) {
            userRegistrations.forEach(item -> {
                item.setApproveReject(ApproveReject.REJECT);
                item.getChangeTracking().update(loggedInUser.toString());
            });

        } else {
            throw new InvalidActionException(String.format("No action matched the passed value %s", approveReject));
        }

        SaveEntityConstraintHelper.saveAll(userRegistrationDao, userRegistrations, null);

        return this.getPendingForApproval();
    }

    public ApplicationUser setUserInfo(UserRegistration userRegistration, LoggedInUser loggedInUser) {

        String password = passwordGenerator.generateSecureRandomPassword();

        ApplicationUser applicationUser = new ApplicationUser();
        applicationUser.setFirstName(userRegistration.getFirstName());
        applicationUser.setLastName(userRegistration.getLastName());
        applicationUser.setEmail(userRegistration.getEmail());
        applicationUser.setApplicationRole(userRegistration.getApplicationRole());
        applicationUser.setCompanyName(userRegistration.getCompanyName());
        applicationUser.setYearOfEstablishment(userRegistration.getYearOfEstablishment());
        applicationUser.setTypeOfEstablishment(userRegistration.getTypeOfEstablishment());
        applicationUser.setAddress(userRegistration.getAddress());
        applicationUser.setCountry(userRegistration.getCountry());
        applicationUser.setState(userRegistration.getState());
        applicationUser.setCity(userRegistration.getCity());
        applicationUser.setContactName(userRegistration.getContactName());
        applicationUser.setContactDesignation(userRegistration.getContactDesignation());
        applicationUser.setContactPhoneNumber(userRegistration.getContactPhoneNumber());
        applicationUser.setContactEmailAddress(userRegistration.getContactEmailAddress());
        applicationUser.setCoordinatorName(userRegistration.getCoordinatorName());
        applicationUser.setCoordinatorMobileNumber(userRegistration.getCoordinatorMobileNumber());
        applicationUser.setActive(true);
        applicationUser.setCredentialsExpired(true);
        applicationUser.setUserSecret(password);
        applicationUser.setChangeTracking(new ChangeTracking(loggedInUser.toString()));
        return applicationUser;
    }


}
