package com.infra.gummadibuilt.pqform;

import com.google.common.collect.ImmutableMap;
import com.infra.gummadibuilt.common.ChangeTracking;
import com.infra.gummadibuilt.common.LoggedInUser;
import com.infra.gummadibuilt.common.exception.InvalidActionException;
import com.infra.gummadibuilt.common.util.SaveEntityConstraintHelper;
import com.infra.gummadibuilt.pqform.model.PqFormHeader;
import com.infra.gummadibuilt.pqform.model.dto.PqFormHeaderCreateDto;
import com.infra.gummadibuilt.pqform.model.dto.PqFormHeaderDto;
import com.infra.gummadibuilt.tender.TenderInfoDao;
import com.infra.gummadibuilt.tender.model.TenderInfo;
import com.infra.gummadibuilt.tender.model.WorkflowStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
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

    @Autowired
    public PqFormHeaderService(PqFormHeaderDao pqFormHeaderDao, TenderInfoDao tenderInfoDao) {
        this.pqFormHeaderDao = pqFormHeaderDao;
        this.tenderInfoDao = tenderInfoDao;
    }

    public PqFormHeaderDto get(String tenderId, HttpServletRequest request) {

        TenderInfo tenderInfo = getById(tenderInfoDao, tenderId, TENDER_NOT_FOUND);
        validateFetch(request, tenderInfo);

        Optional<PqFormHeader> pqFormHeader = pqFormHeaderDao.findByTenderInfo(tenderInfo);

        if (pqFormHeader.isPresent()) {
            return PqFormHeaderDto.valueOf(pqFormHeader.get(), true);
        } else {
            PqFormHeader result = new PqFormHeader();
            result.setWorkPackage(tenderInfo.getWorkDescription());
            result.setContractDuration(tenderInfo.getContractDuration());
            result.setDurationCounter(tenderInfo.getDurationCounter());
            return PqFormHeaderDto.valueOf(result, false);
        }
    }

    public PqFormHeaderDto createPqForm(HttpServletRequest request, String tenderId, PqFormHeaderCreateDto pqFormHeaderCreateDto) {
        LoggedInUser loggedInUser = loggedInUserInfo(request);
        TenderInfo tenderInfo = getById(tenderInfoDao, tenderId, TENDER_NOT_FOUND);

        PqFormHeader formHeader = new PqFormHeader();
        this.createPqForm(pqFormHeaderCreateDto, formHeader);
        formHeader.setTenderInfo(tenderInfo);
        formHeader.setChangeTracking(new ChangeTracking(loggedInUser.toString()));
        formHeader.setPqDocumentIssueDate(null);
        this.saveInfo(tenderInfo, formHeader, pqFormHeaderCreateDto, loggedInUser);

        return PqFormHeaderDto.valueOf(formHeader, true);
    }

    public PqFormHeaderDto updatePqForm(HttpServletRequest request, String tenderId, int pqFormId, PqFormHeaderCreateDto pqFormHeaderCreateDto) {
        LoggedInUser loggedInUser = loggedInUserInfo(request);
        TenderInfo tenderInfo = getById(tenderInfoDao, tenderId, TENDER_NOT_FOUND);
        PqFormHeader formHeader = getById(pqFormHeaderDao, pqFormId, PQ_FORM_NOT_FOUND);
        this.createPqForm(pqFormHeaderCreateDto, formHeader);
        formHeader.setTenderInfo(tenderInfo);
        formHeader.setChangeTracking(new ChangeTracking(loggedInUser.toString()));
        if (pqFormHeaderCreateDto.getWorkflowStep().getText().equals(WorkflowStep.PUBLISHED.getText())) {
            formHeader.setPqDocumentIssueDate(LocalDate.now());
        } else {
            formHeader.setPqDocumentIssueDate(null);
        }
        formHeader.getChangeTracking().update(loggedInUser.toString());
        this.saveInfo(tenderInfo, formHeader, pqFormHeaderCreateDto, loggedInUser);

        return PqFormHeaderDto.valueOf(formHeader, true);
    }

    private void saveInfo(TenderInfo tenderInfo, PqFormHeader formHeader, PqFormHeaderCreateDto pqFormHeaderCreateDto, LoggedInUser loggedInUser) {
        tenderInfo.setWorkflowStep(pqFormHeaderCreateDto.getWorkflowStep());
        tenderInfo.getChangeTracking().update(loggedInUser.toString());

        formHeader.setWorkPackage(tenderInfo.getWorkDescription());
        formHeader.setDurationCounter(tenderInfo.getDurationCounter());
        formHeader.setContractDuration(tenderInfo.getContractDuration());

        SaveEntityConstraintHelper.save(pqFormHeaderDao, formHeader, CONSTRAINT_MAPPING);
        SaveEntityConstraintHelper.save(tenderInfoDao, tenderInfo, null);
    }

    private void createPqForm(PqFormHeaderCreateDto pqFormHeaderCreateDto, PqFormHeader formHeader) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
        LocalDate tentativeAwardDate = LocalDate.parse(pqFormHeaderCreateDto.getTentativeDateOfAward(), formatter);
        LocalDate scheduledCompletion = LocalDate.parse(pqFormHeaderCreateDto.getScheduledCompletion(), formatter);
        LocalDate lastDateOfSubmission = LocalDate.parse(pqFormHeaderCreateDto.getPqLastDateOfSubmission(), formatter);

        formHeader.setProjectName(pqFormHeaderCreateDto.getProjectName());
        formHeader.setTypeOfStructure(pqFormHeaderCreateDto.getTypeOfStructure());
        formHeader.setTentativeDateOfAward(tentativeAwardDate);
        formHeader.setPqLastDateOfSubmission(lastDateOfSubmission);
        formHeader.setScheduledCompletion(scheduledCompletion);
    }

    private void validateFetch(HttpServletRequest request, TenderInfo tenderInfo) {
        if (request.isUserInRole("contractor") & tenderInfo.getWorkflowStep() != WorkflowStep.PUBLISHED) {
            throw new InvalidActionException(
                    String.format("Cannot access PQ form when tender is in %s", tenderInfo.getWorkflowStep().getText())
            );
        }

        if ((request.isUserInRole("client") || request.isUserInRole("admin"))
                & tenderInfo.getWorkflowStep() == WorkflowStep.SAVE) {
            throw new InvalidActionException(
                    String.format("Cannot access PQ form when tender is in %s", tenderInfo.getWorkflowStep().getText())
            );
        }
    }

}
