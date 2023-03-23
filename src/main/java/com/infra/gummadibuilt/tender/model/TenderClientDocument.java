package com.infra.gummadibuilt.tender.model;

import com.infra.gummadibuilt.common.ChangeTracking;
import com.infra.gummadibuilt.userandrole.model.ApplicationUser;
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
public class TenderClientDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Size(max = 255)
    @NotBlank
    private String fileName;

    @NotNull
    private long fileSize;

    @NotNull
    @ManyToOne
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
