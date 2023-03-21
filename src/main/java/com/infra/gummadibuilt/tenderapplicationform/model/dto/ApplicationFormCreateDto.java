package com.infra.gummadibuilt.tenderapplicationform.model.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

@Data
public class ApplicationFormCreateDto implements Serializable {

    @NotBlank
    @Size(max = 255)
    private String companyName;

    @NotBlank
    @Size(max = 4)
    private String yearOfEstablishment;

    @NotBlank
    @Size(max = 255)
    private String typeOfEstablishment;

    @Size(max = 500)
    private String corpOfficeAddress;

    @Size(max = 500)
    private String localOfficeAddress;

    @Size(max = 10)
    private String telephoneNum;

    @Size(max = 10)
    private String faxNumber;

    /*Contact Person Information*/

    @Size(max = 255)
    private String contactName;

    @Size(max = 255)
    private String contactDesignation;

    @Size(max = 10)
    private String contactPhoneNum;

    @Size(max = 100)
    private String contactEmailId;

    /*Regional head/Project coordinator Information*/

    @Size(max = 255)
    private String regionalHeadName;

    @Size(max = 10)
    private String regionalHeadPhoneNum;

    private JsonNode similarProjects;

    private JsonNode clientReferences;

    private JsonNode similarProjectNature;

    /*Compliance*/

    @Size(max = 255)
    private String esiRegistration;

    @Size(max = 255)
    private String esiRegistrationFileName;
    @Size(max = 255)
    private String epfRegistration;

    @Size(max = 255)
    private String epfRegistrationFileName;

    @Size(max = 255)
    private String gstRegistration;

    @Size(max = 255)
    private String gstRegistrationFileName;

    @Size(max = 25)
    private String panNumber;

    @Size(max = 255)
    private String panFileName;

    private JsonNode employeesStrength;

    private JsonNode capitalEquipment;

    /*Compliance*/
    @Size(max = 500)
    private String safetyPolicyManual;

    @Size(max = 500)
    private String ppeToStaff;

    @Size(max = 500)
    private String ppeToWorkMen;

    @Size(max = 500)
    private String safetyOfficeAvailability;

    private JsonNode financialInformation;

    private JsonNode companyBankers;

    private JsonNode companyAuditors;

    private boolean underTaking;

    private List<JsonNode> turnOverDetails;

    private ActionTaken actionTaken;

}
