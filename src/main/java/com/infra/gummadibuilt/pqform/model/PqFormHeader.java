package com.infra.gummadibuilt.pqform.model;

import com.infra.gummadibuilt.common.ChangeTracking;
import com.infra.gummadibuilt.tender.model.TenderInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

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

        return new EqualsBuilder().append(getId(), that.getId()).append(getPqDocumentIssueDate(), that.getPqDocumentIssueDate()).append(getPqLastDateOfSubmission(), that.getPqLastDateOfSubmission()).append(getTentativeDateOfAward(), that.getTentativeDateOfAward()).append(getScheduledCompletion(), that.getScheduledCompletion()).append(getTenderInfo(), that.getTenderInfo()).append(getChangeTracking(), that.getChangeTracking()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getId()).append(getPqDocumentIssueDate()).append(getPqLastDateOfSubmission()).append(getTentativeDateOfAward()).append(getScheduledCompletion()).append(getTenderInfo()).append(getChangeTracking()).toHashCode();
    }
}
