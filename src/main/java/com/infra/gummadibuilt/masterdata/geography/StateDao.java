package com.infra.gummadibuilt.masterdata.geography;

import com.infra.gummadibuilt.masterdata.geography.model.Country;
import com.infra.gummadibuilt.masterdata.geography.model.State;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface StateDao extends JpaRepository<State, String> {
    List<State> findByCountry(Country countryId, Sort sortBy);
}
