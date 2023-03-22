package com.infra.gummadibuilt.tenderapplicationform;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.infra.gummadibuilt.common.ChangeTracking;
import com.infra.gummadibuilt.common.LoggedInUser;
import com.infra.gummadibuilt.common.exception.InValidDataSubmittedException;
import com.infra.gummadibuilt.common.exception.InvalidActionException;
import com.infra.gummadibuilt.common.file.AmazonFileService;
import com.infra.gummadibuilt.common.file.FileDownloadDto;
import com.infra.gummadibuilt.common.util.FileUtils;
import com.infra.gummadibuilt.common.util.SaveEntityConstraintHelper;
import com.infra.gummadibuilt.tender.TenderInfoDao;
import com.infra.gummadibuilt.tender.model.TenderInfo;
import com.infra.gummadibuilt.tender.model.WorkflowStep;
import com.infra.gummadibuilt.tenderapplicants.TenderApplicantsDao;
import com.infra.gummadibuilt.tenderapplicants.model.TenderApplicants;
import com.infra.gummadibuilt.tenderapplicants.model.dto.ApplicationStatus;
import com.infra.gummadibuilt.tenderapplicationform.model.ApplicationForm;
import com.infra.gummadibuilt.tenderapplicationform.model.dto.ActionTaken;
import com.infra.gummadibuilt.tenderapplicationform.model.dto.ApplicationFormCreateDto;
import com.infra.gummadibuilt.tenderapplicationform.model.dto.ApplicationFormDto;
import com.infra.gummadibuilt.tenderapplicationform.model.dto.DocumentType;
import com.infra.gummadibuilt.userandrole.ApplicationRoleDao;
import com.infra.gummadibuilt.userandrole.ApplicationUserDao;
import com.infra.gummadibuilt.userandrole.model.ApplicationUser;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.infra.gummadibuilt.common.util.CommonModuleUtils.*;
import static com.infra.gummadibuilt.common.util.UserInfo.loggedInUserInfo;

@Service
public class AppFormService {
    private static final Map<String, Supplier<? extends RuntimeException>> CONSTRAINT_MAPPING = ImmutableMap
            .of(ApplicationForm.UNQ_NAME_CONSTRAINT, ApplicationFormAlreadyExistsException::new);

    private final TenderInfoDao tenderInfoDao;
    private final ApplicationFormDao applicationFormDao;

    private final ApplicationUserDao applicationUserDao;

    private final AmazonFileService amazonFileService;

    private final TenderApplicantsDao tenderApplicantsDao;
    private final ApplicationRoleDao applicationRoleDao;

    @Autowired
    public AppFormService(TenderInfoDao tenderInfoDao,
                          ApplicationFormDao applicationFormDao,
                          ApplicationUserDao applicationUserDao,
                          AmazonFileService amazonFileService,
                          TenderApplicantsDao tenderApplicantsDao,
                          ApplicationRoleDao applicationRoleDao) {
        this.tenderInfoDao = tenderInfoDao;
        this.applicationFormDao = applicationFormDao;
        this.applicationUserDao = applicationUserDao;
        this.amazonFileService = amazonFileService;
        this.tenderApplicantsDao = tenderApplicantsDao;
        this.applicationRoleDao = applicationRoleDao;
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
    public ApplicationFormDto applyTender(HttpServletRequest request, String tenderId) {
        LoggedInUser loggedInUser = loggedInUserInfo(request);
        ApplicationUser applicationUser = getById(applicationUserDao, loggedInUser.getUserId(), USER_NOT_FOUND);
        TenderInfo tenderInfo = getById(tenderInfoDao, tenderId, TENDER_NOT_FOUND);
        this.validateTypeOfEstablishment(applicationUser, tenderInfo);
        this.validatePqForm(tenderInfo);
        ApplicationForm form = new ApplicationForm();
        this.setUserKnownInfo(form, applicationUser);
        form.setActionTaken(ActionTaken.DRAFT);
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
        if (createDto.getActionTaken().equals(ActionTaken.SUBMIT)) {
            this.validateFileUpload(form);
            TenderApplicants applicants = new TenderApplicants();
            int currentApplicant = tenderApplicantsDao.countByTenderInfo(tenderInfo);
            applicants.setApplicantRank(BigDecimal.valueOf(currentApplicant + 1));
            applicants.setApplicationForm(form);
            applicants.setApplicationUser(applicationUser);
            applicants.setTenderInfo(tenderInfo);
            applicants.setApplicationStatus(ApplicationStatus.UNDER_PROCESS);
            applicants.setChangeTracking(new ChangeTracking(loggedInUser.toString()));

            SaveEntityConstraintHelper.save(tenderApplicantsDao, applicants, null);
        }
        return ApplicationFormDto.valueOf(form);
    }

    @Transactional
    public ApplicationFormDto uploadDocument(HttpServletRequest request,
                                             MultipartFile fileToUpload,
                                             String tenderId,
                                             DocumentType documentType,
                                             String applicationId) {
        LoggedInUser loggedInUser = loggedInUserInfo(request);
        ApplicationUser applicationUser = getById(applicationUserDao, loggedInUser.getUserId(), USER_NOT_FOUND);
        int appId = Integer.parseInt(applicationId);
        ApplicationForm applicationForm = getById(applicationFormDao, appId, APPLICATION_FORM_NOT_FOUND);
        TenderInfo tenderInfo = getById(tenderInfoDao, tenderId, TENDER_NOT_FOUND);
        this.validateTypeOfEstablishment(applicationUser, tenderInfo);
        this.validatePqForm(tenderInfo);
        this.validateTenderAndStatus(applicationForm, tenderId);
        this.validateAccess(applicationUser, loggedInUser);
        FileUtils.checkFileValidOrNot(fileToUpload);
        String filePath = String.format("%s/%s", tenderId, loggedInUser.getUserId());
        List<JsonNode> turnOverInfoNodes = null;
        List<JsonNode> turnOverDetails = applicationForm.getTurnOverDetails();
        boolean modifyTax = false;
        String fileName = fileToUpload.getOriginalFilename();
        switch (documentType.getText()) {
            case "GST":
                filePath = String.format("%s/%s", filePath, DocumentType.GST.getText());
                applicationForm.setGstRegistrationFileName(fileName);
                break;
            case "ESI":
                filePath = String.format("%s/%s", filePath, DocumentType.ESI.getText());
                applicationForm.setEsiRegistrationFileName(fileName);
                break;
            case "EPF":
                filePath = String.format("%s/%s", filePath, DocumentType.EPF.getText());
                applicationForm.setEpfRegistrationFileName(fileName);
                break;
            case "PAN":
                filePath = String.format("%s/%s", filePath, DocumentType.PAN.getText());
                applicationForm.setPanFileName(fileName);
                break;
            case "YEAR_ONE":
                modifyTax = true;
                turnOverInfoNodes = this.modifyTurnOverInfo(turnOverDetails, fileToUpload, DocumentType.YEAR_ONE.getText());
                filePath = String.format("%s/%s", filePath, DocumentType.YEAR_ONE.getText());
                break;
            case "YEAR_TWO":
                modifyTax = true;
                turnOverInfoNodes = this.modifyTurnOverInfo(turnOverDetails, fileToUpload, DocumentType.YEAR_TWO.getText());
                filePath = String.format("%s/%s", filePath, DocumentType.YEAR_TWO.getText());
                break;
            case "YEAR_THREE":
                modifyTax = true;
                turnOverInfoNodes = this.modifyTurnOverInfo(turnOverDetails, fileToUpload, DocumentType.YEAR_THREE.getText());
                filePath = String.format("%s/%s", filePath, DocumentType.YEAR_THREE.getText());
                break;
            default:
                throw new RuntimeException(String.format("Given year format %s is invalid", documentType));
        }

        amazonFileService.uploadFile(filePath, metaData(applicationForm), fileToUpload);
        if (modifyTax) {
            applicationForm.setTurnOverDetails(turnOverInfoNodes);
        }
        SaveEntityConstraintHelper.save(applicationFormDao, applicationForm, null);
        return ApplicationFormDto.valueOf(applicationForm);
    }

    public FileDownloadDto downloadDocument(HttpServletRequest request,
                                            String tenderId,
                                            DocumentType documentType,
                                            String applicationId) {

        LoggedInUser loggedInUser = loggedInUserInfo(request);
        ApplicationUser applicationUser = getById(applicationUserDao, loggedInUser.getUserId(), USER_NOT_FOUND);
        int appId = Integer.parseInt(applicationId);
        ApplicationForm applicationForm = getById(applicationFormDao, appId, APPLICATION_FORM_NOT_FOUND);
        getById(tenderInfoDao, tenderId, TENDER_NOT_FOUND);
        this.validateTenderAndStatus(applicationForm, tenderId);
        if (request.isUserInRole("contractor")) {
            this.validateAccess(applicationUser, loggedInUser);
        }

        String filePath = String.format("%s/%s", tenderId, loggedInUser.getUserId());
        String fileName = "";
        if (documentType.getText().equals("YEAR_ONE") ||
                documentType.getText().equals("YEAR_TWO") ||
                documentType.getText().equals("YEAR_THREE")
        ) {
            fileName = applicationForm.getTurnOverDetails().stream()
                    .filter(item -> item.get("row").asText().equals(documentType.getText()))
                    .map(item -> item.get("fileName").asText())
                    .collect(Collectors.joining(","));
        }

        switch (documentType.getText()) {
            case "GST":
                fileName = applicationForm.getGstRegistrationFileName();
                filePath = String.format("%s/%s", filePath, DocumentType.GST.getText());
                break;
            case "ESI":
                fileName = applicationForm.getEsiRegistrationFileName();
                filePath = String.format("%s/%s", filePath, DocumentType.ESI.getText());
                break;
            case "EPF":
                fileName = applicationForm.getEpfRegistrationFileName();
                filePath = String.format("%s/%s", filePath, DocumentType.EPF.getText());
                break;
            case "PAN":
                fileName = applicationForm.getPanFileName();
                filePath = String.format("%s/%s", filePath, DocumentType.PAN.getText());
                break;
            case "YEAR_ONE":
                filePath = String.format("%s/%s", filePath, DocumentType.YEAR_ONE.getText());
                break;
            case "YEAR_TWO":
                filePath = String.format("%s/%s", filePath, DocumentType.YEAR_TWO.getText());
                break;
            case "YEAR_THREE":
                filePath = String.format("%s/%s", filePath, DocumentType.YEAR_THREE.getText());
                break;
            default:
                throw new RuntimeException(String.format("Given document type %s is invalid", documentType));
        }
        if (fileName.isEmpty()) {
            throw new InValidDataSubmittedException(String.format("Request document type %s doesn't exist", documentType.getText()));
        }
        return amazonFileService.downloadFile(filePath, fileName);
    }

    private Map<String, String> metaData(ApplicationForm applicationForm) {
        Map<String, String> metaData = new HashMap<>();
        metaData.put("ApplicationFormId", String.valueOf(applicationForm.getId()));
        metaData.put("TenderId", applicationForm.getTenderInfo().getId());
        metaData.put("ApplicationUserId", applicationForm.getApplicationUser().getId());
        return metaData;
    }

    private List<JsonNode> modifyTurnOverInfo(List<JsonNode> turnOverInfo,
                                              MultipartFile yearDocument,
                                              String yearInfo) {
        return turnOverInfo.stream()
                .map(item -> {
                    if (item.has("row")) {
                        if (item.get("row").asText().equals(yearInfo)) {
                            ((ObjectNode) item).put("fileName", yearDocument.getOriginalFilename());
                            ((ObjectNode) item).put("fileSize", yearDocument.getSize());
                            return item;
                        }
                    }
                    return item;
                })
                .collect(Collectors.toList());
    }

    private void setUserKnownInfo(ApplicationForm form, ApplicationUser user) {
        form.setCompanyName(user.getCompanyName());
        form.setYearOfEstablishment(String.valueOf(user.getYearOfEstablishment()));
        form.setTypeOfEstablishment(String.join(",", user.getTypeOfEstablishment()));
        form.setCorpOfficeAddress(user.getAddress());
        form.setContactName(String.format("%s, %s", user.getContactFirstName(), user.getContactLastName()));
        form.setContactEmailId(user.getContactEmailAddress());
        form.setContactDesignation(user.getContactDesignation());
        form.setContactPhoneNum(user.getContactPhoneNumber());
    }

    private void createApplication(ApplicationForm form, ApplicationFormCreateDto createDto) {
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
        form.setTurnOverDetails(createDto.getTurnOverDetails());
    }

    private void validateAccess(ApplicationUser applicationUser, LoggedInUser loggedInUser) {
        if (!Objects.equals(applicationUser.getId(), loggedInUser.getUserId())) {
            throw new AccessDeniedException(
                    "Cannot access applications that are not created by you. This action will be reported"
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

    public void validateFileUpload(ApplicationForm applicationForm) {
        if (applicationForm.getGstRegistrationFileName().isEmpty()) {
            throw new InValidDataSubmittedException("GST registration is missing file upload");
        }

        if (applicationForm.getEpfRegistrationFileName().isEmpty()) {
            throw new InValidDataSubmittedException("EPF registration is missing file upload");
        }

        if (applicationForm.getEsiRegistrationFileName().isEmpty()) {
            throw new InValidDataSubmittedException("ESI registration is missing file upload");
        }

        if (applicationForm.getPanFileName().isEmpty()) {
            throw new InValidDataSubmittedException("PAN is missing file upload");
        }
    }

}
