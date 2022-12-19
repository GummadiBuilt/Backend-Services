package com.infra.gummadibuilt.tenderapplicationform.model.dto;

import com.infra.gummadibuilt.tenderapplicationform.model.ApplicationForm;
import lombok.Data;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;

import static com.infra.gummadibuilt.common.util.CommonModuleUtils.DATE_PATTERN;

@Data
public class ApplicationFormDto extends ApplicationFormCreateDto implements Serializable {

    private int applicationId;

    private String tenderId;

    private String lastDateOfSubmission;

    public static ApplicationFormDto valueOf(ApplicationForm applicationForm) {
        ApplicationFormDto result = new ApplicationFormDto();
        result.setApplicationId(applicationForm.getId());
        result.setTenderId(applicationForm.getTenderInfo().getId());
        result.setLastDateOfSubmission(
                applicationForm
                        .getTenderInfo()
                        .getFormHeader()
                        .getPqLastDateOfSubmission()
                        .format(DateTimeFormatter.ofPattern(DATE_PATTERN))
        );
        result.setCompanyName(applicationForm.getCompanyName());
        result.setYearOfEstablishment(applicationForm.getYearOfEstablishment());
        result.setTypeOfEstablishment(applicationForm.getTypeOfEstablishment());
        result.setCorpOfficeAddress(applicationForm.getCorpOfficeAddress());
        result.setLocalOfficeAddress(applicationForm.getLocalOfficeAddress());
        result.setTelephoneNum(applicationForm.getTelephoneNum());
        result.setFaxNumber(applicationForm.getFaxNumber());
        result.setContactName(applicationForm.getContactName());
        result.setContactDesignation(applicationForm.getContactDesignation());
        result.setContactPhoneNum(applicationForm.getContactPhoneNum());
        result.setContactEmailId(applicationForm.getContactEmailId());
        result.setRegionalHeadName(applicationForm.getRegionalHeadName());
        result.setRegionalHeadPhoneNum(applicationForm.getRegionalHeadPhoneNum());
        result.setSimilarProjects(applicationForm.getSimilarProjects());
        result.setClientReferences(applicationForm.getClientReferences());
        result.setSimilarProjectNature(applicationForm.getSimilarProjectNature());
        result.setEsiRegistration(applicationForm.getEsiRegistration());
        result.setEpfRegistration(applicationForm.getEpfRegistration());
        result.setGstRegistration(applicationForm.getGstRegistration());
        result.setPanNumber(applicationForm.getPanNumber());
        result.setEmployeesStrength(applicationForm.getEmployeesStrength());
        result.setCapitalEquipment(applicationForm.getCapitalEquipment());
        result.setPpeToStaff(applicationForm.getPpeToStaff());
        result.setPpeToWorkMen(applicationForm.getPpeToWorkMen());
        result.setSafetyOfficeAvailability(applicationForm.getSafetyOfficeAvailability());
        result.setFinancialInformation(applicationForm.getFinancialInformation());
        result.setCompanyBankers(applicationForm.getCompanyBankers());
        result.setCompanyAuditors(applicationForm.getCompanyAuditors());
        result.setUnderTaking(applicationForm.isUnderTaking());
        result.setActionTaken(applicationForm.getActionTaken());

        return result;
    }

}
