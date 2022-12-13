package com.infra.gummadibuilt.tenderapplicants.model;

import com.infra.gummadibuilt.common.ChangeTracking;
import com.infra.gummadibuilt.tender.model.TenderInfo;
import com.infra.gummadibuilt.userandrole.model.ApplicationUser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class TenderApplicantInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NotNull
    private int rank;

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
    @JoinColumn(name = "pq_application_id")
    private PqApplicantInformation pqApplicationInformation;

    @Embedded
    @NotNull
    @Valid
    private ChangeTracking changeTracking;
}
