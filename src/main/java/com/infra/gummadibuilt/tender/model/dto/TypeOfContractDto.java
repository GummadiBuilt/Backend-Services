package com.infra.gummadibuilt.tender.model.dto;

import com.infra.gummadibuilt.common.ChangeTracking;
import com.infra.gummadibuilt.tender.model.TypeOfContract;
import lombok.Data;

import java.io.Serializable;

@Data
public class TypeOfContractDto implements Serializable {

    private int id;

    private String contractShortCode;

    private String typeOfContract;

    private boolean isActive;

    private ChangeTracking changeTracking;

    public static TypeOfContractDto valueOf(TypeOfContract typeOfContract) {

        TypeOfContractDto result = new TypeOfContractDto();
        result.setId(typeOfContract.getId());
        result.setContractShortCode(typeOfContract.getContractShortCode());
        result.setTypeOfContract(typeOfContract.getTypeOfContract());
        result.setActive(typeOfContract.isActive());
        result.setChangeTracking(typeOfContract.getChangeTracking());

        return result;
    }
}
