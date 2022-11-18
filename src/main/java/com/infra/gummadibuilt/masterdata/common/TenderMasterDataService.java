package com.infra.gummadibuilt.masterdata.common;

import com.infra.gummadibuilt.masterdata.common.model.TypeOfEstablishment;
import com.infra.gummadibuilt.tender.TypeOfContractDao;
import com.infra.gummadibuilt.tender.model.TypeOfContract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TenderMasterDataService {

    private final TypeOfEstablishmentDao typeOfEstablishmentDao;
    private final TypeOfContractDao typeOfContractDao;

    @Autowired
    public TenderMasterDataService(TypeOfEstablishmentDao typeOfEstablishmentDao, TypeOfContractDao typeOfContractDao) {
        this.typeOfEstablishmentDao = typeOfEstablishmentDao;
        this.typeOfContractDao = typeOfContractDao;
    }

    private final Sort typeOfEstablishment = Sort.by(Sort.Direction.ASC, "establishmentDescription");
    private final Sort typeOfContract = Sort.by(Sort.Direction.ASC, "typeOfContract");


    public Map<String, Object> getAll() {
        List<TypeOfEstablishment> typeOfEstablishments = typeOfEstablishmentDao.findAll(typeOfEstablishment);
        List<TypeOfContract> typeOfContracts = typeOfContractDao.findAll(typeOfContract);
        Map<String, Object> mapData = new HashMap<>();
        mapData.put("typeOfContracts", typeOfContracts);
        mapData.put("typeOfEstablishments", typeOfEstablishments);
        return mapData;
    }
}
