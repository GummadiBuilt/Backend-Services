package com.infra.gummadibuilt.masterdata.common;

import com.infra.gummadibuilt.masterdata.common.model.TypeOfEstablishment;
import com.infra.gummadibuilt.masterdata.geography.CountryDao;
import com.infra.gummadibuilt.masterdata.geography.model.Country;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RegistrationMasterDataService {

    private final CountryDao countryDao;

    private final TypeOfEstablishmentDao typeOfEstablishmentDao;

    @Autowired
    public RegistrationMasterDataService(CountryDao countryDao, TypeOfEstablishmentDao typeOfEstablishmentDao) {
        this.countryDao = countryDao;
        this.typeOfEstablishmentDao = typeOfEstablishmentDao;
    }

    private final Sort countryCode = Sort.by(Sort.Direction.ASC, "countryIsoCode");

    private final Sort typeOfEstablishment = Sort.by(Sort.Direction.ASC, "establishmentDescription");


    /**
     * Fetch master data needed for the user registration module
     *
     * @return Map with Master data needed for user registration module
     */
    public Map<String, Object> getAll() {
        List<Country> countries = countryDao.findAll(countryCode);
        List<TypeOfEstablishment> typeOfEstablishments = typeOfEstablishmentDao.findAll(typeOfEstablishment);

        Map<String, Object> mapData = new HashMap<>();
        mapData.put("countries", countries);
        mapData.put("typeOfEstablishments", typeOfEstablishments);
        return mapData;
    }

}
