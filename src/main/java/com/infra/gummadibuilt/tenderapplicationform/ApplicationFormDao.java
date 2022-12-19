package com.infra.gummadibuilt.tenderapplicationform;

import com.infra.gummadibuilt.tenderapplicationform.model.ApplicationForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationFormDao extends JpaRepository<ApplicationForm, Integer> {
}
