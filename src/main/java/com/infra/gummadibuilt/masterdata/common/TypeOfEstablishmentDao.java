package com.infra.gummadibuilt.masterdata.common;

import com.infra.gummadibuilt.masterdata.common.model.TypeOfEstablishment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TypeOfEstablishmentDao extends JpaRepository<TypeOfEstablishment, String> {
}
