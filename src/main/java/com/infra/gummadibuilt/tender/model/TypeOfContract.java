package com.infra.gummadibuilt.tender.model;

import com.infra.gummadibuilt.common.ChangeTracking;
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
public class TypeOfContract {

    // Name of the db constraint for uniqueness of the country
    public static final String UNQ_NAME_CONSTRAINT = "type_of_contract_const";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank
    @Size(max = 3)
    private String contractShortCode;

    @NotBlank
    @Size(max = 100)
    private String typeOfContract;

    private boolean isActive;

    @Embedded
    @Valid
    @NotNull
    private ChangeTracking changeTracking;
}
