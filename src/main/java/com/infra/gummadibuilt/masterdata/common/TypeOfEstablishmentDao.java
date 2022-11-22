package com.infra.gummadibuilt.masterdata.common;

import com.infra.gummadibuilt.masterdata.common.model.TypeOfEstablishment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TypeOfEstablishmentDao extends JpaRepository<TypeOfEstablishment, String> {
    List<TypeOfEstablishment> findAllByEstablishmentDescriptionIn(List<String> typeOfWorks);
}
