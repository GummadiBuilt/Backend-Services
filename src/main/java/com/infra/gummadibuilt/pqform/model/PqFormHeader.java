package com.infra.gummadibuilt.pqform.model;

import com.infra.gummadibuilt.common.ChangeTracking;
import com.infra.gummadibuilt.tender.model.DurationCounter;
import com.infra.gummadibuilt.tender.model.TenderInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class PqFormHeader {

    // Name of the db constraint for uniqueness of the state
    public static final String UNQ_NAME_CONSTRAINT = "pq_form_header_unq_name";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PqFormHeader)) return false;
        PqFormHeader that = (PqFormHeader) o;
        return getId() == that.getId() && getContractDuration() == that.getContractDuration() && Objects.equals(getProjectName(), that.getProjectName()) && Objects.equals(getWorkPackage(), that.getWorkPackage()) && Objects.equals(getTypeOfStructure(), that.getTypeOfStructure()) && getDurationCounter() == that.getDurationCounter() && Objects.equals(getPqDocumentIssueDate(), that.getPqDocumentIssueDate()) && Objects.equals(getPqLastDateOfSubmission(), that.getPqLastDateOfSubmission()) && Objects.equals(getTentativeDateOfAward(), that.getTentativeDateOfAward()) && Objects.equals(getScheduledCompletion(), that.getScheduledCompletion()) && Objects.equals(getTenderInfo(), that.getTenderInfo()) && Objects.equals(getChangeTracking(), that.getChangeTracking());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getProjectName(), getWorkPackage(), getTypeOfStructure(), getContractDuration(), getDurationCounter(), getPqDocumentIssueDate(), getPqLastDateOfSubmission(), getTentativeDateOfAward(), getScheduledCompletion(), getTenderInfo(), getChangeTracking());
    }
}
