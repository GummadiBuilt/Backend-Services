package com.infra.gummadibuilt.masterdata.geography;

import com.infra.gummadibuilt.masterdata.geography.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryDao extends JpaRepository<Country, String> {
}
