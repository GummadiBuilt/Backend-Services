package com.infra.gummadibuilt.masterdata.geography;

import com.infra.gummadibuilt.masterdata.geography.model.City;
import com.infra.gummadibuilt.masterdata.geography.model.State;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CityDao extends JpaRepository<City, Integer> {
    public List<City> findCitiesByState(State stateId, Sort sort);
}
