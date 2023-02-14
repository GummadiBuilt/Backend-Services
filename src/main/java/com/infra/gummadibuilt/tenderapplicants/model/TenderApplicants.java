package com.infra.gummadibuilt.tenderapplicants.model;

import com.infra.gummadibuilt.common.ChangeTracking;
import com.infra.gummadibuilt.tender.model.TenderInfo;
import com.infra.gummadibuilt.tenderapplicants.model.dto.ApplicationStatus;
import com.infra.gummadibuilt.tenderapplicationform.model.ApplicationForm;
import com.infra.gummadibuilt.userandrole.model.ApplicationUser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class TenderApplicants {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @Digits(integer = 2, fraction = 3)
    private BigDecimal applicantRank;

    @Size(max = 2500)
    private String justificationNote;

    @Enumerated(value = EnumType.STRING)
    private ApplicationStatus applicationStatus;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "application_user_id")
    private ApplicationUser applicationUser;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "tender_info_id")
    private TenderInfo tenderInfo;

    @OneToOne
    @NotNull
    @JoinColumn(name = "applicant_form_id")
    private ApplicationForm applicationForm;

    private boolean isRecommended;

    @Embedded
    @NotNull
    @Valid
    private ChangeTracking changeTracking;
}
