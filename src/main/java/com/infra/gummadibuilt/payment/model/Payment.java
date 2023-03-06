package com.infra.gummadibuilt.payment.model;

import com.infra.gummadibuilt.common.ChangeTracking;
import com.infra.gummadibuilt.tender.model.TenderInfo;
import com.infra.gummadibuilt.userandrole.model.ApplicationRole;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class Payment {

    @Id
    private String referenceId;

    @NotNull
    private long paymentAmount;

    @ManyToOne
    @JoinColumn(name = "application_role_id")
    private ApplicationRole applicationRole;

    @ManyToOne
    @JoinColumn(name = "tender_info_id")
    private TenderInfo tenderInfo;

    @NotBlank
    private String shortUrl;

    @NotBlank
    private String paymentId;

    @NotNull
    private long expireBy;

    @NotBlank
    @Size(max = 50)
    private String paymentDescription;

    @NotBlank
    @Size(max = 50)
    private String contactName;

    @Size(max = 13)
    private String contactPhoneNumber;

    @Size(max = 50)
    private String contactEmailAddress;

    private boolean notifyViaSms;

    private boolean notifyViaEmail;

    @Embedded
    @NotNull
    @Valid
    private ChangeTracking changeTracking;

}
