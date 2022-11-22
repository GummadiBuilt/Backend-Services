package com.infra.gummadibuilt.masterdata.common.model.dto;

import com.infra.gummadibuilt.common.ChangeTracking;
import com.infra.gummadibuilt.masterdata.common.model.TypeOfEstablishment;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class TypeOfEstablishmentDto {

    @NotBlank
    @Size(max = 25)
    private String establishmentDescription;

    private boolean isActive;

    private ChangeTracking changeTracking;

    public static TypeOfEstablishmentDto valueOf(TypeOfEstablishment typeOfEstablishment) {
        TypeOfEstablishmentDto result = new TypeOfEstablishmentDto();
        result.setEstablishmentDescription(typeOfEstablishment.getEstablishmentDescription());
        result.setActive(typeOfEstablishment.isActive());
        result.setChangeTracking(typeOfEstablishment.getChangeTracking());

        return result;
    }

}
