package com.infra.gummadibuilt.pqform;

import com.infra.gummadibuilt.pqform.model.PqFormHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PqFormHeaderDao extends JpaRepository<PqFormHeader, Integer> {
}
