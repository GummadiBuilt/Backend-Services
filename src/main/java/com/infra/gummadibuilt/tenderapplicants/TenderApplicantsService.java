package com.infra.gummadibuilt.tenderapplicants;

import com.infra.gummadibuilt.common.LoggedInUser;
import com.infra.gummadibuilt.common.exception.EntityNotFoundException;
import com.infra.gummadibuilt.common.exception.InvalidActionException;
import com.infra.gummadibuilt.common.mail.MailService;
import com.infra.gummadibuilt.common.util.SaveEntityConstraintHelper;
import com.infra.gummadibuilt.tender.TenderInfoDao;
import com.infra.gummadibuilt.tender.model.TenderInfo;
import com.infra.gummadibuilt.tender.model.WorkflowStep;
import com.infra.gummadibuilt.tender.model.dto.TenderDetailsDto;
import com.infra.gummadibuilt.tenderapplicants.model.TenderApplicants;
import com.infra.gummadibuilt.tenderapplicants.model.dto.ApplicantsComparisonDto;
import com.infra.gummadibuilt.tenderapplicants.model.dto.ApplicationStatus;
import com.infra.gummadibuilt.tenderapplicants.model.dto.TenderApplicantsDashboardDto;
import com.infra.gummadibuilt.tenderapplicants.model.dto.TenderApplicantsDto;
import com.infra.gummadibuilt.tenderapplicationform.ApplicationFormDao;
import com.infra.gummadibuilt.tenderapplicationform.model.ApplicationForm;
import com.infra.gummadibuilt.tenderapplicationform.model.dto.ActionTaken;
import com.infra.gummadibuilt.tenderapplicationform.model.dto.ApplicationFormDto;
import com.infra.gummadibuilt.tenderbidinfo.TenderBidInfoDao;
import com.infra.gummadibuilt.tenderbidinfo.model.TenderBidInfo;
import com.infra.gummadibuilt.userandrole.model.ApplicationUser;
import com.infra.gummadibuilt.userregistration.UserRegistrationService;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.infra.gummadibuilt.common.util.CommonModuleUtils.*;
import static com.infra.gummadibuilt.common.util.UserInfo.loggedInUserInfo;

@Service
public class TenderApplicantsService {

    private static final Logger logger = LoggerFactory.getLogger(TenderApplicantsService.class);


    private final TenderApplicantsDao tenderApplicantsDao;
    private final TenderInfoDao tenderInfoDao;

    private final ApplicationFormDao applicationFormDao;
    private final TenderBidInfoDao tenderBidInfoDao;

    private final UserRegistrationService userRegistrationService;

    private final MailService mailService;

    public TenderApplicantsService(TenderApplicantsDao tenderApplicantsDao,
                                   TenderInfoDao tenderInfoDao,
                                   ApplicationFormDao applicationFormDao,
                                   TenderBidInfoDao tenderBidInfoDao,
                                   MailService mailService,
                                   UserRegistrationService userRegistrationService) {
        this.tenderApplicantsDao = tenderApplicantsDao;
        this.tenderInfoDao = tenderInfoDao;
        this.applicationFormDao = applicationFormDao;
        this.tenderBidInfoDao = tenderBidInfoDao;
        this.mailService = mailService;
        this.userRegistrationService = userRegistrationService;
    }

    public List<TenderApplicantsDto> get(String tenderId, HttpServletRequest request) {
        TenderInfo tenderInfo = getById(tenderInfoDao, tenderId, TENDER_NOT_FOUND);
        LoggedInUser loggedInUser = loggedInUserInfo(request);
        if (request.isUserInRole("client") && Objects.equals(tenderInfo.getApplicationUser().getId(), loggedInUser.getUserId())) {
            throw new AccessDeniedException(
                    "Cannot access tenders that are not created by you. This action will be reported"
            );
        }
        List<TenderApplicantsDashboardDto> dashboardDtos = tenderApplicantsDao.getTenderApplicants(tenderId);

        return dashboardDtos.stream().map(TenderApplicantsDto::valueOf).collect(Collectors.toList());
    }

    public List<TenderApplicantsDto> updateRanking(HttpServletRequest request,
                                                   String tenderId,
                                                   ActionTaken actionTaken,
                                                   List<TenderApplicantsDto> tenderApplicantsDto) {
        LoggedInUser loggedInUser = loggedInUserInfo(request);
        TenderInfo tenderInfo = getById(tenderInfoDao, tenderId, TENDER_NOT_FOUND);

        this.validate(tenderInfo);
        List<Integer> applicationFormIds = tenderApplicantsDto
                .stream()
                .map(TenderApplicantsDto::getApplicationFormId)
                .collect(Collectors.toList());
        List<ApplicationForm> applicationForms = this.validateApplicationForm(applicationFormIds, tenderInfo);
        List<TenderApplicants> tenderApplicants = this.validateTenderApplicants(tenderApplicantsDto, tenderInfo);
        List<TenderApplicants> updatedInfo = new ArrayList<>();

        tenderApplicantsDto.forEach(applicant -> {
            int applicationFormId = applicant.getApplicationFormId();
            int tenderApplicantId = applicant.getId();

            Optional<TenderApplicants> optionalTenderApplicants = tenderApplicants.stream().filter(item -> item.getId() == tenderApplicantId).findFirst();
            TenderApplicants application;
            if (optionalTenderApplicants.isPresent()) {
                application = optionalTenderApplicants.get();
            } else {
                throw new EntityNotFoundException(String.format(APPLICANT_NOT_FOUND, tenderApplicantId));
            }

            Optional<ApplicationForm> optionalApplicationForm = applicationForms.stream().filter(item -> item.getId() == applicationFormId).findFirst();
            ApplicationForm form;
            if (optionalApplicationForm.isPresent()) {
                form = optionalApplicationForm.get();
            } else {
                throw new EntityNotFoundException(String.format(APPLICATION_FORM_NOT_FOUND, applicationFormId));
            }

            this.validateApplicationAndApplicant(application, form);

            application.setApplicantRank(applicant.getApplicantRank());
            application.setJustificationNote(applicant.getJustificationNote());
            application.setApplicationStatus(applicant.getApplicationStatus());
            updatedInfo.add(application);
        });

        SaveEntityConstraintHelper.saveAll(tenderApplicantsDao, updatedInfo, null);
        if (actionTaken.equals(ActionTaken.SUBMIT)) {
            tenderInfo.setWorkflowStep(WorkflowStep.QUALIFIED);
            tenderInfo.getChangeTracking().update(loggedInUser.toString());
            SaveEntityConstraintHelper.save(tenderInfoDao, tenderInfo, null);
            String[] adminUsers = userRegistrationService.getAdminUserEmailAddress();
            sendEmail(updatedInfo, tenderInfo, ApplicationStatus.QUALIFIED, adminUsers);
            sendEmail(updatedInfo, tenderInfo, ApplicationStatus.NOT_QUALIFIED, adminUsers);
        }
        return this.get(tenderId, request);
    }


    public List<ApplicantsComparisonDto> compareApplicants(String tenderId, List<String> applicantId, HttpServletRequest request) {
        if (applicantId.size() > 10) {
            throw new InvalidActionException("A maximum of 10 applications can be compared at once");
        }
        TenderInfo tenderInfo = getById(tenderInfoDao, tenderId, TENDER_NOT_FOUND);
        LoggedInUser loggedInUser = loggedInUserInfo(request);
        if (request.isUserInRole("client") && Objects.equals(tenderInfo.getApplicationUser().getId(), loggedInUser.getUserId())) {
            throw new AccessDeniedException(
                    "Cannot access tenders that are not created by you. This action will be reported"
            );
        }
        List<Integer> applicantIds = applicantId.stream().map(Integer::parseInt).collect(Collectors.toList());
        List<ApplicationForm> applicationForms = this.validateApplicationForm(applicantIds, tenderInfo);
        List<TenderBidInfo> tenderBidInfo = new ArrayList<>();
        List<TenderApplicantsDto> tenderApplicants = new ArrayList<>();
        if (tenderInfo.getWorkflowStep().equals(WorkflowStep.IN_REVIEW)) {
            List<ApplicationUser> userID = applicationForms.stream().map(ApplicationForm::getApplicationUser).collect(Collectors.toList());
            tenderBidInfo = tenderBidInfoDao.findAllByTenderInfoAndApplicationUserIn(tenderInfo, userID);
            tenderApplicants = this.get(tenderId, request);
        }
        return this.applicantsComparisonDtos(applicationForms, tenderApplicants, tenderBidInfo);
    }

    private List<ApplicantsComparisonDto> applicantsComparisonDtos(List<ApplicationForm> applicationForms,
                                                                   List<TenderApplicantsDto> tenderApplicants,
                                                                   List<TenderBidInfo> tenderBidInfo) {
        List<ApplicantsComparisonDto> result = new ArrayList<>();

        applicationForms.forEach(item -> {
            ApplicantsComparisonDto comparisonDto = new ApplicantsComparisonDto();
            String userId = item.getApplicationUser().getId();

            Optional<TenderApplicantsDto> applicant = tenderApplicants.stream().filter(ta -> ta.getApplicationUserId().equalsIgnoreCase(userId)).findFirst();
            applicant.ifPresent(comparisonDto::setTenderApplicantsDto);

            Optional<TenderBidInfo> bidInfo = tenderBidInfo.stream().filter(bid -> bid.getApplicationUser().getId().equalsIgnoreCase(userId)).findFirst();
            if (bidInfo.isPresent()) {
                TenderBidInfo bid = bidInfo.get();
                TenderDetailsDto detailsDto = TenderDetailsDto.valueOf(bid.getTenderInfo(), false);
                detailsDto.setContractorBidId(bid.getId());
                detailsDto.setContractorDocumentName(bid.getTenderDocumentName());
                detailsDto.setTenderFinanceInfo(bid.getTenderFinanceInfo());

                comparisonDto.setTenderDetailsDto(detailsDto);
            }

            ApplicationFormDto formDto = ApplicationFormDto.valueOf(item);
            comparisonDto.setApplicationFormDto(formDto);

            result.add(comparisonDto);
        });


        return result;
    }

    public void validateApplicationAndApplicant(TenderApplicants applicants, ApplicationForm applicationForm) {

        if (!Objects.equals(applicants.getApplicationUser().getId(), applicationForm.getApplicationUser().getId())) {
            throw new InvalidActionException(
                    String.format("For tender %s, applicants didn't match", applicants.getTenderInfo().getId())
            );
        }

    }

    public void validate(TenderInfo tenderInfo) {
        String tenderId = tenderInfo.getId();
        if (tenderInfo.getWorkflowStep() != WorkflowStep.UNDER_PROCESS) {
            throw new InvalidActionException(
                    String.format("Tender %s should be in step under process to get/update rankings", tenderId)
            );
        }

    }

    public List<TenderApplicants> validateTenderApplicants(List<TenderApplicantsDto> tenderApplicantsDto, TenderInfo tenderInfo) {
        String tenderId = tenderInfo.getId();
        List<Integer> tenderApplicantIds = tenderApplicantsDto
                .stream()
                .map(TenderApplicantsDto::getId)
                .collect(Collectors.toList());


        List<TenderApplicants> tenderApplicants = tenderApplicantsDao.findAllByTenderInfoAndIdIn(tenderInfo, tenderApplicantIds);

        if (tenderApplicants.size() != tenderApplicantIds.size()) {
            String tenderAppIds = tenderApplicants.stream()
                    .map(TenderApplicants::getId)
                    .filter(id -> !tenderApplicantIds.contains(id))
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));
            throw new InvalidActionException(String.format("Tender applicants %s were not mapped to tender %s", tenderAppIds, tenderId));
        }

        return tenderApplicants;
    }

    public List<ApplicationForm> validateApplicationForm(List<Integer> applicationFormIds, TenderInfo tenderInfo) {
        String tenderId = tenderInfo.getId();
        List<ApplicationForm> applicationForms = applicationFormDao.findAllByTenderInfoAndIdIn(tenderInfo, applicationFormIds);
        if (applicationForms.size() != applicationFormIds.size()) {
            String appId = applicationForms.stream()
                    .map(ApplicationForm::getId)
                    .filter(id -> !applicationFormIds.contains(id))
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));
            throw new InvalidActionException(String.format("Application forms %s were not mapped to tender %s", appId, tenderId));
        }

        return applicationForms;
    }


    public void sendEmail(List<TenderApplicants> updatedInfo,
                          TenderInfo tenderInfo,
                          ApplicationStatus applicationStatus,
                          String[] adminUser) {
        logger.info(String.format("Sending mail communication sent to %s contractors", applicationStatus.getText()));
        Map<String, Object> model = new HashMap<>();
        model.put("tenderId", tenderInfo.getId());
        model.put("projectName", tenderInfo.getProjectName());
        model.put("applicationStatus", applicationStatus.getText().toUpperCase());

        updatedInfo.stream()
                .filter(item -> item.getApplicationStatus().equals(applicationStatus))
                .forEach(user -> {
                    String[] mailTo = {user.getApplicationUser().getContactEmailAddress()};
                    model.put("contractorCompanyName", user.getApplicationUser().getCompanyName());
                    model.put("tenderCompanyName", tenderInfo.getApplicationUser().getCompanyName());
                    String lastDate = tenderInfo.getLastDateOfSubmission().format(DateTimeFormatter.ofPattern(DATE_PATTERN));
                    model.put("lastDate", lastDate);
                    try {
                        mailService.sendMail(mailTo, adminUser, "preQualification.ftl", model);
                    } catch (IOException | MessagingException | TemplateException e) {
                        throw new RuntimeException(e);
                    }
                    logger.info(String.format("Mail communication sent to %s contractors", applicationStatus.getText()));
                });

    }
}
