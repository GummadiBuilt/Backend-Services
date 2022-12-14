package com.infra.gummadibuilt.tenderapplicationform;

import com.infra.gummadibuilt.common.LoggedInUser;
import com.infra.gummadibuilt.common.exception.InvalidActionException;
import com.infra.gummadibuilt.pqform.PqFormHeaderDao;
import com.infra.gummadibuilt.pqform.model.PqFormHeader;
import com.infra.gummadibuilt.tender.TenderInfoDao;
import com.infra.gummadibuilt.tender.model.TenderInfo;
import com.infra.gummadibuilt.tender.model.WorkflowStep;
import com.infra.gummadibuilt.tenderapplicationform.model.ApplicantForm;
import com.infra.gummadibuilt.tenderapplicationform.model.dto.ApplicationFormCreateDto;
import com.infra.gummadibuilt.tenderapplicationform.model.dto.ApplicationFormDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;

import static com.infra.gummadibuilt.common.util.CommonModuleUtils.TENDER_NOT_FOUND;
import static com.infra.gummadibuilt.common.util.CommonModuleUtils.getById;
import static com.infra.gummadibuilt.common.util.UserInfo.loggedInUserInfo;
import static java.time.temporal.ChronoUnit.DAYS;

@Service
public class ApplicationFormService {

    private final ApplicationFormDao applicationFormDao;
    private final TenderInfoDao tenderInfoDao;
    private final PqFormHeaderDao pqFormHeaderDao;

    @Autowired
    public ApplicationFormService(ApplicationFormDao applicationFormDao,
                                  TenderInfoDao tenderInfoDao,
                                  PqFormHeaderDao pqFormHeaderDao) {
        this.applicationFormDao = applicationFormDao;
        this.tenderInfoDao = tenderInfoDao;
        this.pqFormHeaderDao = pqFormHeaderDao;
    }

    public ApplicationFormDto apply(HttpServletRequest request, ApplicationFormCreateDto createDto, String tenderId) {

        TenderInfo tenderInfo = getById(tenderInfoDao, tenderId, TENDER_NOT_FOUND);


        LoggedInUser loggedInUser = loggedInUserInfo(request);
        ApplicantForm form = new ApplicantForm();

        return new ApplicationFormDto();
    }


    private PqFormHeader validatePqForm(TenderInfo tenderInfo) {
        PqFormHeader pqFormHeader;
        LocalDate lastDateOfSubmission;
        if (tenderInfo.getFormHeader() != null) {
            pqFormHeader = tenderInfo.getFormHeader();
            lastDateOfSubmission = pqFormHeader.getPqLastDateOfSubmission();
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
        return pqFormHeader;
    }

    private long dayDiff(LocalDate lastDateOfSubmission) {
        LocalDate today = LocalDate.now();
        return DAYS.between(today, lastDateOfSubmission);
    }

}
