package com.infra.gummadibuilt.tenderapplicationform;

import com.infra.gummadibuilt.tender.model.TenderInfo;
import com.infra.gummadibuilt.tenderapplicationform.model.ApplicationForm;
import com.infra.gummadibuilt.userandrole.model.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationFormDao extends JpaRepository<ApplicationForm, Integer> {

    Optional<ApplicationForm> findByTenderInfoAndApplicationUser(TenderInfo tenderInfo, ApplicationUser applicationUser);

}
