package com.infra.gummadibuilt.admin.enquiries.model;

import com.infra.gummadibuilt.common.ChangeTracking;
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

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Enquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "application_role_id")
    private ApplicationRole applicationRole;

    @NotBlank
    @Size(max = 25)
    private String userName;

    @NotBlank
    @Size(max = 50)
    private String emailAddress;

    @NotBlank
    @Size(max = 10)
    private String mobileNumber;

    @NotBlank
    @Size(max = 500)
    private String enquiryDescription;

    @Embedded
    @NotNull
    @Valid
    private ChangeTracking changeTracking;


}
