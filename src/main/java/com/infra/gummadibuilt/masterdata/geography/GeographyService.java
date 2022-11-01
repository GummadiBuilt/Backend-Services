package com.infra.gummadibuilt.masterdata.geography;

import com.infra.gummadibuilt.masterdata.geography.model.City;
import com.infra.gummadibuilt.masterdata.geography.model.dto.CityDto;
import com.infra.gummadibuilt.masterdata.geography.model.Country;
import com.infra.gummadibuilt.masterdata.geography.model.State;
import com.infra.gummadibuilt.masterdata.geography.model.dto.StateDto;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.infra.gummadibuilt.common.util.CommonModuleUtils.*;

@Service
public class GeographyService {

    private final StateDao stateDao;

    private final CountryDao countryDao;

    private final CityDao cityDao;

    public GeographyService(CountryDao countryDao, CityDao cityDao, StateDao stateDao) {
        this.countryDao = countryDao;
        this.cityDao = cityDao;
        this.stateDao = stateDao;
    }

    private final Sort idSort = Sort.by(Sort.Direction.ASC, "stateIsoCode");
    private final Sort citySort = Sort.by(Sort.Direction.ASC, "cityName");

    public List<StateDto> getStatesByCountry(String countryIsoCode) {
        Country country = getById(countryDao, countryIsoCode, COUNTRY_ID_NOT_FOUND);
        List<State> state = stateDao.findByCountry(country, idSort);
        return state.stream().map(StateDto::valueOf).collect(Collectors.toList());
    }

    public List<CityDto> getCitiesByState(String stateCode) {

        State stateInfo = getById(stateDao, stateCode, STATE_ID_NOT_FOUND);
        List<City> cities = cityDao.findCitiesByState(stateInfo, citySort);

        return cities.stream().map(CityDto::valueOf).collect(Collectors.toList());

    }

}
