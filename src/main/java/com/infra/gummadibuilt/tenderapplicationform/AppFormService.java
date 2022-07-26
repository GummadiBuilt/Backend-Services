package com.infra.gummadibuilt.tenderapplicationform;

import com.google.common.collect.ImmutableMap;
import com.infra.gummadibuilt.common.ChangeTracking;
import com.infra.gummadibuilt.common.LoggedInUser;
import com.infra.gummadibuilt.common.exception.InvalidActionException;
import com.infra.gummadibuilt.common.util.SaveEntityConstraintHelper;
import com.infra.gummadibuilt.tender.TenderInfoDao;
import com.infra.gummadibuilt.tender.model.TenderInfo;
import com.infra.gummadibuilt.tender.model.WorkflowStep;
import com.infra.gummadibuilt.tenderapplicationform.model.ApplicationForm;
import com.infra.gummadibuilt.tenderapplicationform.model.dto.ActionTaken;
import com.infra.gummadibuilt.tenderapplicationform.model.dto.ApplicationFormCreateDto;
import com.infra.gummadibuilt.tenderapplicationform.model.dto.ApplicationFormDto;
import com.infra.gummadibuilt.userandrole.ApplicationUserDao;
import com.infra.gummadibuilt.userandrole.model.ApplicationUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import static com.infra.gummadibuilt.common.util.CommonModuleUtils.*;
import static com.infra.gummadibuilt.common.util.UserInfo.loggedInUserInfo;
import static java.time.temporal.ChronoUnit.DAYS;

@Service
public class AppFormService {
    private static final Map<String, Supplier<? extends RuntimeException>> CONSTRAINT_MAPPING = ImmutableMap
            .of(ApplicationForm.UNQ_NAME_CONSTRAINT, ApplicationFormAlreadyExistsException::new);

    private final TenderInfoDao tenderInfoDao;
    private final ApplicationFormDao applicationFormDao;

    private final ApplicationUserDao applicationUserDao;

    @Autowired
    public AppFormService(TenderInfoDao tenderInfoDao,
                          ApplicationFormDao applicationFormDao,
                          ApplicationUserDao applicationUserDao) {
        this.tenderInfoDao = tenderInfoDao;
        this.applicationFormDao = applicationFormDao;
        this.applicationUserDao = applicationUserDao;
    }

    public ApplicationFormDto get(HttpServletRequest request, String tenderId, int applicationId) {
        LoggedInUser loggedInUser = loggedInUserInfo(request);
        ApplicationUser applicationUser = getById(applicationUserDao, loggedInUser.getUserId(), USER_NOT_FOUND);
        this.validateAccess(applicationUser, loggedInUser);
        System.out.println(applicationUser.getId());
        getById(tenderInfoDao, tenderId, TENDER_NOT_FOUND);
        ApplicationForm form = getById(applicationFormDao, applicationId, APPLICATION_FORM_NOT_FOUND);
        this.validateTenderAndStatus(form, tenderId);
        return ApplicationFormDto.valueOf(form);
    }

    @Transactional
    public ApplicationFormDto applyTender(HttpServletRequest request, ApplicationFormCreateDto createDto, String tenderId) {
        LoggedInUser loggedInUser = loggedInUserInfo(request);
        ApplicationUser applicationUser = getById(applicationUserDao, loggedInUser.getUserId(), USER_NOT_FOUND);
        TenderInfo tenderInfo = getById(tenderInfoDao, tenderId, TENDER_NOT_FOUND);
        this.validateTypeOfEstablishment(applicationUser, tenderInfo);
        this.validatePqForm(tenderInfo);
        ApplicationForm form = new ApplicationForm();
        this.createApplication(form, createDto);
        form.setChangeTracking(new ChangeTracking(loggedInUser.toString()));
        form.setTenderInfo(tenderInfo);
        form.setApplicationUser(applicationUser);
        SaveEntityConstraintHelper.save(applicationFormDao, form, CONSTRAINT_MAPPING);

        return ApplicationFormDto.valueOf(form);
    }

    @Transactional
    public ApplicationFormDto update(HttpServletRequest request,
                         ApplicationFormCreateDto createDto,
                         String tenderId,
                         String applicationId) {
        LoggedInUser loggedInUser = loggedInUserInfo(request);
        ApplicationUser applicationUser = getById(applicationUserDao, loggedInUser.getUserId(), USER_NOT_FOUND);
        int appId = Integer.parseInt(applicationId);
        ApplicationForm form = getById(applicationFormDao, appId, APPLICATION_FORM_NOT_FOUND);
        TenderInfo tenderInfo = getById(tenderInfoDao, tenderId, TENDER_NOT_FOUND);
        if (form.getActionTaken().equals(ActionTaken.SUBMIT)) {
            throw new InvalidActionException("Cannot update a submitted application");
        }
        this.validateTypeOfEstablishment(applicationUser, tenderInfo);
        this.validatePqForm(tenderInfo);
        this.validateTenderAndStatus(form, tenderId);
        this.validateAccess(applicationUser, loggedInUser);
        this.createApplication(form, createDto);
        form.getChangeTracking().update(loggedInUser.toString());
        form.setTenderInfo(tenderInfo);
        form.setApplicationUser(applicationUser);
        SaveEntityConstraintHelper.save(applicationFormDao, form, null);
        return ApplicationFormDto.valueOf(form);
    }


    private void createApplication(ApplicationForm form, ApplicationFormCreateDto createDto) {
        form.setCompanyName(createDto.getCompanyName());
        form.setYearOfEstablishment(createDto.getYearOfEstablishment());
        form.setTypeOfEstablishment(createDto.getTypeOfEstablishment());
        form.setCorpOfficeAddress(createDto.getCorpOfficeAddress());
        form.setLocalOfficeAddress(createDto.getLocalOfficeAddress());
        form.setTelephoneNum(createDto.getTelephoneNum());
        form.setFaxNumber(createDto.getFaxNumber());
        form.setContactName(createDto.getContactName());
        form.setContactDesignation(createDto.getContactDesignation());
        form.setContactPhoneNum(createDto.getContactPhoneNum());
        form.setContactEmailId(createDto.getContactEmailId());
        form.setRegionalHeadName(createDto.getRegionalHeadName());
        form.setRegionalHeadPhoneNum(createDto.getRegionalHeadPhoneNum());
        form.setSimilarProjects(createDto.getSimilarProjects());
        form.setClientReferences(createDto.getClientReferences());
        form.setSimilarProjectNature(createDto.getSimilarProjectNature());
        form.setEsiRegistration(createDto.getEsiRegistration());
        form.setEpfRegistration(createDto.getEpfRegistration());
        form.setGstRegistration(createDto.getGstRegistration());
        form.setPanNumber(createDto.getPanNumber());
        form.setEmployeesStrength(createDto.getEmployeesStrength());
        form.setCapitalEquipment(createDto.getCapitalEquipment());
        form.setSafetyPolicyManual(createDto.getSafetyPolicyManual());
        form.setPpeToStaff(createDto.getPpeToStaff());
        form.setPpeToWorkMen(createDto.getPpeToWorkMen());
        form.setSafetyOfficeAvailability(createDto.getSafetyOfficeAvailability());
        form.setFinancialInformation(createDto.getFinancialInformation());
        form.setCompanyBankers(createDto.getCompanyBankers());
        form.setCompanyAuditors(createDto.getCompanyAuditors());
        form.setUnderTaking(createDto.isUnderTaking());
        form.setActionTaken(createDto.getActionTaken());
    }

    private void validateAccess(ApplicationUser applicationUser, LoggedInUser loggedInUser) {
        if (!Objects.equals(applicationUser.getId(), loggedInUser.getUserId())) {
            throw new AccessDeniedException(
                    "Cannot access applications that are not created by you. This actions will be reported"
            );
        }
    }

    private void validateTenderAndStatus(ApplicationForm form, String tenderId) {
        if (!Objects.equals(form.getTenderInfo().getId(), tenderId)) {
            throw new InvalidActionException(String.format("Submitted application doesn't map to tender %s", tenderId));
        }
    }

    private void validateTypeOfEstablishment(ApplicationUser applicationUser, TenderInfo tenderInfo) {
        if (!applicationUser.getTypeOfEstablishment().contains(tenderInfo.getTypeOfEstablishment().getEstablishmentDescription())) {
            String typeOfEstablishments = String.join(",", applicationUser.getTypeOfEstablishment());
            throw new InvalidActionException(
                    String.format("You cannot apply this tender, you can apply only the establishments %s you are in specialized in", typeOfEstablishments)
            );
        }
    }

    private void validatePqForm(TenderInfo tenderInfo) {
        LocalDate lastDateOfSubmission;
        if (tenderInfo.getFormHeader() != null) {
            lastDateOfSubmission = tenderInfo.getFormHeader().getPqLastDateOfSubmission();
        } else {
            throw new InvalidActionException(
                    String.format("Cannot apply to tender %s as there is no PQ form created", tenderInfo.getId())
            );
        }
        if (tenderInfo.getWorkflowStep() != WorkflowStep.PUBLISHED) {
            throw new InvalidActionException(
                    String.format("Cannot apply to tender %s as its not in published state", tenderInfo.getId())
            );
        }
        if (dayDiff(lastDateOfSubmission) < 0) {
            throw new InvalidActionException(
                    String.format("Cannot apply to tender %s as the last date of submission is in past", tenderInfo.getId())
            );
        }
    }

    private long dayDiff(LocalDate lastDateOfSubmission) {
        LocalDate today = LocalDate.now();
        return DAYS.between(today, lastDateOfSubmission);
    }
}
