package com.infra.gummadibuilt.pqform;

import com.infra.gummadibuilt.pqform.model.PqFormHeader;
import com.infra.gummadibuilt.tender.model.TenderInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PqFormHeaderDao extends JpaRepository<PqFormHeader, Integer> {

}
