package com.infra.gummadibuilt.pqform.model;

import com.infra.gummadibuilt.common.ChangeTracking;
import com.infra.gummadibuilt.tender.model.DurationCounter;
import com.infra.gummadibuilt.tender.model.TenderInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class PqFormHeader {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Size(max = 255)
    @NotBlank
    private String projectName;

    @NotBlank
    @Size(max = 50)
    private String workPackage;

    @Size(max = 255)
    @NotBlank
    private String typeOfStructure;

    @NotNull
    @Max(99999)
    private int contractDuration;

    @Enumerated(EnumType.STRING)
    private DurationCounter durationCounter;

    private LocalDate pqDocumentIssueDate;

    @NotNull
    private LocalDate pqLastDateOfSubmission;

    @NotNull
    private LocalDate tentativeDateOfAward;

    @NotNull
    private LocalDate scheduledCompletion;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "tender_info_id")
    private TenderInfo tenderInfo;

    @Embedded
    @NotNull
    @Valid
    private ChangeTracking changeTracking;
}
