package com.infra.gummadibuilt.tenderapplicationform.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.infra.gummadibuilt.common.ChangeTracking;
import com.infra.gummadibuilt.tender.model.TenderInfo;
import com.infra.gummadibuilt.tenderapplicationform.model.dto.ActionTaken;
import com.infra.gummadibuilt.userandrole.model.ApplicationUser;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@TypeDefs({
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
public class ApplicationForm {

    public static final String UNQ_NAME_CONSTRAINT = "application_form_unq_name";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /*Company Information*/
    @NotBlank
    @Size(max = 255)
    private String companyName;

    @NotBlank
    @Size(max = 4)
    private String yearOfEstablishment;

    @NotBlank
    @Size(max = 2500)
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

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private JsonNode similarProjects;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private JsonNode clientReferences;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
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

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private JsonNode employeesStrength;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
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

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private JsonNode financialInformation;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private JsonNode companyBankers;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private JsonNode companyAuditors;

    private boolean underTaking;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private List<JsonNode> turnOverDetails;

    @Enumerated(EnumType.STRING)
    private ActionTaken actionTaken;

    @OneToOne
    @NotNull
    @JoinColumn(name = "tender_info_id")
    private TenderInfo tenderInfo;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "application_user_id")
    private ApplicationUser applicationUser;

    @Embedded
    @NotNull
    @Valid
    private ChangeTracking changeTracking;

}
