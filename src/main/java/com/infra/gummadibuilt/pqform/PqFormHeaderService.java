package com.infra.gummadibuilt.pqform;

import com.google.common.collect.ImmutableMap;
import com.infra.gummadibuilt.common.ChangeTracking;
import com.infra.gummadibuilt.common.LoggedInUser;
import com.infra.gummadibuilt.common.exception.InValidDataSubmittedException;
import com.infra.gummadibuilt.common.exception.InvalidActionException;
import com.infra.gummadibuilt.common.util.SaveEntityConstraintHelper;
import com.infra.gummadibuilt.pqform.model.PqFormHeader;
import com.infra.gummadibuilt.pqform.model.dto.PqFormHeaderCreateDto;
import com.infra.gummadibuilt.pqform.model.dto.PqFormHeaderDto;
import com.infra.gummadibuilt.tender.TenderInfoDao;
import com.infra.gummadibuilt.tender.model.TenderInfo;
import com.infra.gummadibuilt.tender.model.WorkflowStep;
import com.infra.gummadibuilt.tenderapplicationform.ApplicationFormDao;
import com.infra.gummadibuilt.tenderapplicationform.model.ApplicationForm;
import com.infra.gummadibuilt.userandrole.ApplicationUserDao;
import com.infra.gummadibuilt.userandrole.model.ApplicationUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static com.infra.gummadibuilt.common.util.CommonModuleUtils.*;
import static com.infra.gummadibuilt.common.util.UserInfo.loggedInUserInfo;

@Service
public class PqFormHeaderService {

    private static final Map<String, Supplier<? extends RuntimeException>> CONSTRAINT_MAPPING = ImmutableMap
            .of(PqFormHeader.UNQ_NAME_CONSTRAINT, PqFormAlreadyExistsException::new);


    private final PqFormHeaderDao pqFormHeaderDao;
    private final TenderInfoDao tenderInfoDao;

    private final ApplicationFormDao applicationFormDao;
    private final ApplicationUserDao applicationUserDao;

    @Autowired
    public PqFormHeaderService(PqFormHeaderDao pqFormHeaderDao,
                               ApplicationFormDao applicationFormDao,
                               ApplicationUserDao applicationUserDao,
                               TenderInfoDao tenderInfoDao) {
        this.pqFormHeaderDao = pqFormHeaderDao;
        this.applicationFormDao = applicationFormDao;
        this.applicationUserDao = applicationUserDao;
        this.tenderInfoDao = tenderInfoDao;
    }

    public PqFormHeaderDto get(String tenderId, int pqFormId, HttpServletRequest request) {
        TenderInfo tenderInfo = getById(tenderInfoDao, tenderId, TENDER_NOT_FOUND);
        validateFetch(request, tenderInfo);
        PqFormHeader formHeader = getById(pqFormHeaderDao, pqFormId, PQ_FORM_NOT_FOUND);
        PqFormHeaderDto pqFormHeaderDto = PqFormHeaderDto.valueOf(formHeader);
        if (request.isUserInRole("contractor")) {
            LoggedInUser loggedInUser = loggedInUserInfo(request);
            ApplicationUser applicationUser = getById(applicationUserDao, loggedInUser.getUserId(), USER_NOT_FOUND);
            Optional<ApplicationForm> applicationForm = applicationFormDao.findByTenderInfoAndApplicationUser(tenderInfo, applicationUser);
            applicationForm.ifPresent(form -> {
                pqFormHeaderDto.setApplicationFormId(form.getId());
                pqFormHeaderDto.setApplicationFormStatus(form.getActionTaken());
            });
        }
        pqFormHeaderDto.setHasFinanceInfo(validateFinanceInfo(tenderInfo));
        pqFormHeaderDto.setTenderSubmissionDate(tenderInfo.getLastDateOfSubmission().format(DateTimeFormatter.ofPattern(DATE_PATTERN)));
        return pqFormHeaderDto;
    }

    @Transactional
    public PqFormHeaderDto createPqForm(HttpServletRequest request, String tenderId, PqFormHeaderCreateDto pqFormHeaderCreateDto) {
        this.validateWorkflowStep(pqFormHeaderCreateDto);
        LoggedInUser loggedInUser = loggedInUserInfo(request);
        TenderInfo tenderInfo = getById(tenderInfoDao, tenderId, TENDER_NOT_FOUND);
        PqFormHeader formHeader = new PqFormHeader();
        this.createPqForm(pqFormHeaderCreateDto, formHeader);
        formHeader.setChangeTracking(new ChangeTracking(loggedInUser.toString()));
        this.saveInfo(tenderInfo, formHeader, pqFormHeaderCreateDto, loggedInUser);

        PqFormHeaderDto headerDto = PqFormHeaderDto.valueOf(formHeader);
        headerDto.setTenderSubmissionDate(tenderInfo.getLastDateOfSubmission().format(DateTimeFormatter.ofPattern(DATE_PATTERN)));

        headerDto.setHasFinanceInfo(validateFinanceInfo(tenderInfo));
        return headerDto;
    }

    @Transactional
    public PqFormHeaderDto updatePqForm(HttpServletRequest request, String tenderId, int pqFormId, PqFormHeaderCreateDto pqFormHeaderCreateDto) {
        this.validateWorkflowStep(pqFormHeaderCreateDto);
        LoggedInUser loggedInUser = loggedInUserInfo(request);
        TenderInfo tenderInfo = getById(tenderInfoDao, tenderId, TENDER_NOT_FOUND);
        PqFormHeader formHeader = getById(pqFormHeaderDao, pqFormId, PQ_FORM_NOT_FOUND);
        this.createPqForm(pqFormHeaderCreateDto, formHeader);
        formHeader.getChangeTracking().update(loggedInUser.toString());
        this.saveInfo(tenderInfo, formHeader, pqFormHeaderCreateDto, loggedInUser);

        PqFormHeaderDto headerDto = PqFormHeaderDto.valueOf(formHeader);
        headerDto.setTenderSubmissionDate(tenderInfo.getLastDateOfSubmission().format(DateTimeFormatter.ofPattern(DATE_PATTERN)));
        headerDto.setHasFinanceInfo(validateFinanceInfo(tenderInfo));
        return headerDto;
    }

    private void saveInfo(TenderInfo tenderInfo, PqFormHeader formHeader, PqFormHeaderCreateDto pqFormHeaderCreateDto, LoggedInUser loggedInUser) {
        if (pqFormHeaderCreateDto.getWorkflowStep().getText().equals(WorkflowStep.PUBLISHED.getText())) {
            formHeader.setPqDocumentIssueDate(LocalDate.now());
            tenderInfo.setWorkflowStep(pqFormHeaderCreateDto.getWorkflowStep());
            if (!validateFinanceInfo(tenderInfo)) {
                throw new InValidDataSubmittedException("Cannot submit PQ form, please fill Finance information in Tender");
            }
        } else {
            formHeader.setPqDocumentIssueDate(null);
        }
        formHeader.setTenderInfo(tenderInfo);
        tenderInfo.getChangeTracking().update(loggedInUser.toString());
        SaveEntityConstraintHelper.save(pqFormHeaderDao, formHeader, CONSTRAINT_MAPPING);
        SaveEntityConstraintHelper.save(tenderInfoDao, tenderInfo, null);
    }

    private void createPqForm(PqFormHeaderCreateDto pqFormHeaderCreateDto, PqFormHeader formHeader) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
        LocalDate tentativeAwardDate = LocalDate.parse(pqFormHeaderCreateDto.getTentativeDateOfAward(), formatter);
        LocalDate lastDateOfSubmission = LocalDate.parse(pqFormHeaderCreateDto.getPqLastDateOfSubmission(), formatter);
        formHeader.setTentativeDateOfAward(tentativeAwardDate);
        formHeader.setPqLastDateOfSubmission(lastDateOfSubmission);
    }

    private boolean validateFinanceInfo(TenderInfo tenderInfo) {
        return tenderInfo.getTenderFinanceInfo() != null;
    }

    private void validateFetch(HttpServletRequest request, TenderInfo tenderInfo) {
        if (request.isUserInRole("contractor") & (tenderInfo.getWorkflowStep() == WorkflowStep.DRAFT || tenderInfo.getWorkflowStep() == WorkflowStep.YET_TO_BE_PUBLISHED)) {
            throw new InvalidActionException(
                    String.format("Cannot access PQ form when tender is in %s", tenderInfo.getWorkflowStep().getText())
            );
        }

        if ((request.isUserInRole("client") || request.isUserInRole("admin"))
                & tenderInfo.getWorkflowStep() == WorkflowStep.DRAFT) {
            throw new InvalidActionException(
                    String.format("Cannot access PQ form when tender is in %s", tenderInfo.getWorkflowStep().getText())
            );
        }
    }

    private void validateWorkflowStep(PqFormHeaderCreateDto pqFormHeaderCreateDto) {
        if ((pqFormHeaderCreateDto.getWorkflowStep() != WorkflowStep.YET_TO_BE_PUBLISHED)
                & (pqFormHeaderCreateDto.getWorkflowStep() != WorkflowStep.PUBLISHED)) {
            throw new InvalidActionException("PQ Form action can only be Yet to be published or Published");
        }
    }

}
