package com.infra.gummadibuilt.notifications.model;

import com.infra.gummadibuilt.tender.model.TenderInfo;
import com.infra.gummadibuilt.tenderapplicationform.model.ApplicationForm;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Notifications {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @NotBlank
    @Size(max = 50)
    private String messageHeader;

    @NotBlank
    @Size(max = 255)
    private String messageDetails;

    @ManyToOne
    @JoinColumn(name = "tender_info_id")
    private TenderInfo tenderInfo;

    @NotNull
    private LocalDate createdDate;

    @NotBlank
    @Size(max = 255)
    private String createdBy;
}
