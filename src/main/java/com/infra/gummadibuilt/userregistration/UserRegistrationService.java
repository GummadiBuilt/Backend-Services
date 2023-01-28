package com.infra.gummadibuilt.userregistration;

import com.google.common.base.Joiner;
import com.infra.gummadibuilt.common.ApproveReject;
import com.infra.gummadibuilt.common.ChangeTracking;
import com.infra.gummadibuilt.common.LoggedInUser;
import com.infra.gummadibuilt.common.exception.InvalidActionException;
import com.infra.gummadibuilt.common.exception.UserExistsException;
import com.infra.gummadibuilt.common.mail.MailService;
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
import freemarker.template.TemplateException;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

import static com.infra.gummadibuilt.common.util.CommonModuleUtils.*;
import static com.infra.gummadibuilt.common.util.UserInfo.loggedInUserInfo;

@Service
public class UserRegistrationService {

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.auth-server-url}")
    private String serverUrl;

    @Value("${keycloak.resource}")
    private String clientId;
    private static final Logger logger = LoggerFactory.getLogger(UserRegistrationService.class);

    private final UserRegistrationDao userRegistrationDao;

    private final CountryDao countryDao;

    private final StateDao stateDao;

    private final CityDao cityDao;

    private final ApplicationRoleDao applicationRoleDao;

    private final PasswordGenerator passwordGenerator;

    private final ApplicationUserDao applicationUserDao;

    private final MailService mailService;

    public UserRegistrationService(UserRegistrationDao userRegistrationDao, CountryDao countryDao, StateDao stateDao, CityDao cityDao, ApplicationRoleDao applicationRoleDao, PasswordGenerator passwordGenerator, ApplicationUserDao applicationUserDao, MailService mailService) {
        this.userRegistrationDao = userRegistrationDao;
        this.countryDao = countryDao;
        this.stateDao = stateDao;
        this.cityDao = cityDao;
        this.applicationRoleDao = applicationRoleDao;
        this.passwordGenerator = passwordGenerator;
        this.applicationUserDao = applicationUserDao;
        this.mailService = mailService;
    }

    public UserRegistrationDto registerUser(UserRegistrationDto userRegistrationDto) throws MessagingException, TemplateException, IOException {

        String emailReceived = userRegistrationDto.getContactEmailAddress();

        Optional<ApplicationUser> applicationUser = applicationUserDao.findByContactEmailAddress(emailReceived);

        Optional<UserRegistration> pendingApproval = userRegistrationDao.findByContactEmailAddressAndApproveReject(emailReceived, ApproveReject.IN_REVIEW);

        if (applicationUser.isPresent()) {
            throw new UserExistsException(String.format("Email address %s is already in use. Did you forgot password?", emailReceived));
        }
        if (pendingApproval.isPresent()) {
            throw new UserExistsException(String.format("A registration with email address %s is pending for approval", emailReceived));
        }

        LoggedInUser loggedInUser = new LoggedInUser(
                userRegistrationDto.getContactFirstName(),
                userRegistrationDto.getContactLastName(),
                userRegistrationDto.getContactEmailAddress(),
                "NEW-USER"
        );

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

        Map<String, Object> model = new HashMap<>();
        model.put("companyName", userRegistrationDto.getCompanyName());
        String[] mailTo = {emailReceived};
        mailService.sendMail(mailTo, null, "registrationConfirmation.ftl", model);

        String[] adminUsers = getAdminUserEmailAddress();
        mailService.sendMail(adminUsers, null, "pendingApproval.ftl", model);

        return userRegistrationDto;
    }

    public List<RegistrationInfoDto> getPendingForApproval() {
        List<ApproveReject> approveRejects = Collections.singletonList(ApproveReject.IN_REVIEW);
        return userRegistrationDao.findAllByApproveRejectIn(approveRejects).stream().map(RegistrationInfoDto::valueOf).collect(Collectors.toList());

    }

    @Transactional
    public List<RegistrationInfoDto> approveOrRejectRequests(HttpServletRequest request, ApproveRejectDto approveRejectDto) throws UnsupportedEncodingException {

        LoggedInUser loggedInUser = loggedInUserInfo(request);

        List<Integer> requestIds = approveRejectDto.getRequestId();
        String approveReject = approveRejectDto.getActionTaken();

        List<UserRegistration> userRegistrations = userRegistrationDao.findAllById(requestIds);

        if (userRegistrations.size() != requestIds.size()) {
            List<Integer> notFoundIds = userRegistrations.stream().map(UserRegistration::getId).filter(id -> !requestIds.contains(id)).collect(Collectors.toList());
            String missingIds = Joiner.on(",").join(notFoundIds);
            throw new RuntimeException(String.format("Couldn't find following requests %s", missingIds));
        }

        if (approveReject.equals(ApproveReject.APPROVE.getText())) {
            List<ApplicationUser> applicationUsers = new ArrayList<>();
            userRegistrations.forEach(item -> {
                String password = passwordGenerator.generateSecureRandomPassword();
                try {
                    applicationUsers.add(this.createUser(item, password, request, loggedInUser));
                } catch (IOException | TemplateException | MessagingException e) {
                    throw new RuntimeException(e);
                }
                item.setApproveReject(ApproveReject.APPROVE);
                item.getChangeTracking().update(loggedInUser.toString());
            });

            SaveEntityConstraintHelper.saveAll(applicationUserDao, applicationUsers, null);
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

    public List<RegistrationInfoDto> auditApproveRejectRequests() {
        List<ApproveReject> approveRejects = Arrays.asList(ApproveReject.APPROVE, ApproveReject.REJECT);

        return userRegistrationDao.findAllByApproveRejectIn(approveRejects).stream().map(RegistrationInfoDto::valueOf).collect(Collectors.toList());
    }

    public ApplicationUser setUserInfo(ApplicationUser applicationUser, UserRegistration userRegistration, LoggedInUser loggedInUser) {
        applicationUser.setApplicationRole(userRegistration.getApplicationRole());
        applicationUser.setCompanyName(userRegistration.getCompanyName());
        applicationUser.setYearOfEstablishment(userRegistration.getYearOfEstablishment());
        applicationUser.setTypeOfEstablishment(userRegistration.getTypeOfEstablishment());
        applicationUser.setAddress(userRegistration.getAddress());
        applicationUser.setCountry(userRegistration.getCountry());
        applicationUser.setState(userRegistration.getState());
        applicationUser.setCity(userRegistration.getCity());
        applicationUser.setContactFirstName(userRegistration.getContactFirstName());
        applicationUser.setContactLastName(userRegistration.getContactLastName());
        applicationUser.setContactDesignation(userRegistration.getContactDesignation());
        applicationUser.setContactPhoneNumber(userRegistration.getContactPhoneNumber());
        applicationUser.setContactEmailAddress(userRegistration.getContactEmailAddress());
        applicationUser.setActive(true);
        applicationUser.setCredentialsExpired(true);
        applicationUser.setChangeTracking(new ChangeTracking(loggedInUser.toString()));
        return applicationUser;
    }


    public ApplicationUser createUser(UserRegistration userRegistration,
                                      String tempPassword,
                                      HttpServletRequest request,
                                      LoggedInUser loggedInUser) throws IOException, MessagingException, TemplateException {
        ApplicationUser applicationUser = new ApplicationUser();
        Keycloak keycloak = this.generateBuild(request);

        logger.info(String.format("Creating user %s", userRegistration.getContactEmailAddress()));
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(userRegistration.getContactEmailAddress());
        user.setFirstName(userRegistration.getContactFirstName());
        user.setLastName(userRegistration.getContactLastName());
        user.setEmail(userRegistration.getContactEmailAddress());

        RealmResource realmResource = keycloak.realm("Local-Realm");
        UsersResource usersResource = realmResource.users();

        // Create user (requires manage-users role)
        Response response = usersResource.create(user);
        logger.info(String.format("Response for create user: %s %s%n", response.getStatus(), response.getStatusInfo()));

        String userId = CreatedResponseUtil.getCreatedId(response);
        logger.info(String.format("Created user id: %s%n", userId));


        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(true);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(tempPassword);

        logger.info(String.format("Temporary password %s", tempPassword));

        UserResource userResource = usersResource.get(userId);
        userResource.resetPassword(passwordCred);

        String requiredRole = userRegistration.getApplicationRole().getRoleName().toLowerCase();

        logger.info(String.format("Adding role %s for user %s%n ", requiredRole, userId));
        RoleRepresentation testerRealmRole = realmResource.roles()//
                .get(requiredRole).toRepresentation();
        userResource.roles().realmLevel() //
                .add(Collections.singletonList(testerRealmRole));

        Map<String, Object> model = new HashMap<>();
        model.put("companyName", userRegistration.getCompanyName());
        model.put("tempPassword", tempPassword);

        logger.info(String.format("Sending email to user %s", userRegistration.getContactEmailAddress()));
        String[] mailTo = {userRegistration.getContactEmailAddress()};
        mailService.sendMail(mailTo, null, "temporaryPassword.ftl", model);

        applicationUser.setId(userId);

        return setUserInfo(applicationUser, userRegistration, loggedInUser);
    }

    public Keycloak generateBuild(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        return KeycloakBuilder.builder() //
                .serverUrl(serverUrl) //
                .realm(realm) //
                .authorization(token).clientId(clientId).build();
    }

    public String[] getAdminUserEmailAddress() {
        Optional<ApplicationRole> adminRole = applicationRoleDao.findByRoleNameIgnoreCase("admin");
        if (adminRole.isPresent()) {
            return applicationUserDao.findAllByApplicationRole(adminRole.get()).stream().map(ApplicationUser::getContactEmailAddress).toArray(String[]::new);
        } else {
            throw new RuntimeException("No Admins found in the database, cannot register");
        }
    }

}
