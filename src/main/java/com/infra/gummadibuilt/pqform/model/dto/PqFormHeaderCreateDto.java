package com.infra.gummadibuilt.pqform.model.dto;

import com.infra.gummadibuilt.common.ChangeTracking;
import com.infra.gummadibuilt.tender.model.DurationCounter;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class PqFormHeaderCreateDto {

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
    private LocalDate pqLastDateOfSubmission;

    @NotNull
    private LocalDate tentativeDateOfAward;

    @NotNull
    private LocalDate scheduledCompletion;
}
