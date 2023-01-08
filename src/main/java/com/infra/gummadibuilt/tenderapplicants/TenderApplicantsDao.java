package com.infra.gummadibuilt.tenderapplicants;

import com.infra.gummadibuilt.tender.model.TenderInfo;
import com.infra.gummadibuilt.tenderapplicants.model.TenderApplicants;
import com.infra.gummadibuilt.tenderapplicants.model.dto.TenderApplicantsDashboardDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TenderApplicantsDao extends JpaRepository<TenderApplicants, Integer> {

    @Query(value = "select ta.applicant_rank, ta.id, ta.application_status, ta.tender_info_id , ta.applicant_form_id, " +
            "au.company_name, ta.justification_note, au.id as application_user_id ,ta.modified_by, ta.modified_date " +
            "from tender_applicants ta left join application_user au on ta.application_user_id = au.id " +
            "where ta.tender_info_id =:tenderId order by ta.applicant_rank",nativeQuery = true)
    List<TenderApplicantsDashboardDto> getTenderApplicants(String tenderId);

    int countByTenderInfo(TenderInfo tenderInfo);

}
