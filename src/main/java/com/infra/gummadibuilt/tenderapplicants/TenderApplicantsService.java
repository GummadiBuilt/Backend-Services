package com.infra.gummadibuilt.tenderapplicants;

import com.infra.gummadibuilt.common.LoggedInUser;
import com.infra.gummadibuilt.common.exception.InvalidActionException;
import com.infra.gummadibuilt.common.util.SaveEntityConstraintHelper;
import com.infra.gummadibuilt.tender.TenderInfoDao;
import com.infra.gummadibuilt.tender.model.TenderInfo;
import com.infra.gummadibuilt.tender.model.WorkflowStep;
import com.infra.gummadibuilt.tenderapplicants.model.TenderApplicants;
import com.infra.gummadibuilt.tenderapplicants.model.dto.TenderApplicantsDashboardDto;
import com.infra.gummadibuilt.tenderapplicants.model.dto.TenderApplicantsDto;
import com.infra.gummadibuilt.tenderapplicationform.ApplicationFormDao;
import com.infra.gummadibuilt.tenderapplicationform.model.ApplicationForm;
import com.infra.gummadibuilt.tenderapplicationform.model.dto.ActionTaken;
import com.infra.gummadibuilt.tenderapplicationform.model.dto.ApplicationFormDto;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.infra.gummadibuilt.common.util.CommonModuleUtils.*;
import static com.infra.gummadibuilt.common.util.UserInfo.loggedInUserInfo;

@Service
public class TenderApplicantsService {

    private final TenderApplicantsDao tenderApplicantsDao;
    private final TenderInfoDao tenderInfoDao;

    private final ApplicationFormDao applicationFormDao;

    public TenderApplicantsService(TenderApplicantsDao tenderApplicantsDao,
                                   TenderInfoDao tenderInfoDao,
                                   ApplicationFormDao applicationFormDao) {
        this.tenderApplicantsDao = tenderApplicantsDao;
        this.tenderInfoDao = tenderInfoDao;
        this.applicationFormDao = applicationFormDao;
    }

    public List<TenderApplicantsDto> get(String tenderId) {
        getById(tenderInfoDao, tenderId, TENDER_NOT_FOUND);
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
        List<TenderApplicants> updatedInfo = new ArrayList<>();
        tenderApplicantsDto.forEach(applicant -> {
            int appFormId = applicant.getApplicationFormId();
            int applicantId = applicant.getId();
            TenderApplicants application = getById(tenderApplicantsDao, applicantId, APPLICANT_NOT_FOUND);
            ApplicationForm form = getById(applicationFormDao, appFormId, APPLICATION_FORM_NOT_FOUND);
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
        }
        return this.get(tenderId);
    }

    public List<ApplicationFormDto> compareApplicants(String tenderId, List<String> applicantId) {
        getById(tenderInfoDao, tenderId, TENDER_NOT_FOUND);
        List<Integer> applicantIds = applicantId.stream().map(Integer::parseInt).collect(Collectors.toList());
        List<ApplicationForm> applicationForms = applicationFormDao.findAllById(applicantIds);

        return applicationForms.stream().map(ApplicationFormDto::valueOf).collect(Collectors.toList());
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
}
