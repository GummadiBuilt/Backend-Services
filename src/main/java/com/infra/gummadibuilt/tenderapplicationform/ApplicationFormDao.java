package com.infra.gummadibuilt.tenderapplicationform;

import com.infra.gummadibuilt.tenderapplicationform.model.ApplicantForm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationFormDao extends JpaRepository<ApplicantForm, Integer> {
}
