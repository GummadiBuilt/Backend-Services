package com.infra.gummadibuilt.tender.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.infra.gummadibuilt.common.ChangeTracking;
import com.infra.gummadibuilt.masterdata.common.model.TypeOfEstablishment;
import com.infra.gummadibuilt.pqform.model.PqFormHeader;
import com.infra.gummadibuilt.userandrole.model.ApplicationUser;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@TypeDefs({
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
public class TenderInfo {

    @Id
    @Size(max = 50)
    private String id;

    @NotBlank
    @Size(max = 50)
    private String projectName;

    @NotBlank
    @Size(max = 2500)
    private String workDescription;

    @OneToOne
    @NotNull
    @JoinColumn(name = "type_of_establishment_desc")
    private TypeOfEstablishment typeOfEstablishment;

    @NotBlank
    @Size(max = 50)
    private String projectLocation;

    @OneToOne
    @NotNull
    @JoinColumn(name = "type_of_contract_id")
    private TypeOfContract typeOfContract;

    @NotNull
    @Max(99999)
    private int contractDuration;

    @Enumerated(EnumType.STRING)
    private DurationCounter durationCounter;

    @Column(columnDefinition = "DATE")
    @NotNull
    private LocalDate lastDateOfSubmission;

    @Digits(integer = 20, fraction = 0)
    private BigDecimal estimatedBudget;

    @Enumerated(EnumType.STRING)
    private WorkflowStep workflowStep;

    @NotBlank
    @Size(max = 50)
    private String tenderDocumentName;

    // Document size in MB
    @NotNull
    private long tenderDocumentSize;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private JsonNode tenderFinanceInfo;

    private boolean fileUpload;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "application_user_id")
    private ApplicationUser applicationUser;

    @OneToMany(mappedBy = "tenderInfo", cascade = CascadeType.REMOVE)
    private List<TenderClientDocument> tenderClientDocuments;

    @OneToOne(mappedBy = "tenderInfo", cascade = CascadeType.ALL)
    private PqFormHeader formHeader;

    @Embedded
    @NotNull
    @Valid
    private ChangeTracking changeTracking;
}
