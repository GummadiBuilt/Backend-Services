package com.infra.gummadibuilt.tenderapplicants;

import com.infra.gummadibuilt.tender.model.TenderInfo;
import com.infra.gummadibuilt.tenderapplicants.model.TenderApplicants;
import com.infra.gummadibuilt.tenderapplicants.model.dto.TenderApplicantsDashboardDto;
import com.infra.gummadibuilt.userandrole.model.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TenderApplicantsDao extends JpaRepository<TenderApplicants, Integer> {

    @Query(value = "select ta.applicant_rank, ta.id, ta.application_status, ta.tender_info_id , ta.applicant_form_id, " +
            "au.company_name, ta.justification_note, au.id as application_user_id ,ta.modified_by, ta.modified_date, " +
            "ti.workflow_step, " +
            "case when ti.workflow_step IN('IN_REVIEW', 'RECOMMENDED') then COALESCE (tbi.tender_document_name,'') else '' end as tender_document," +
            "case when ti.workflow_step IN('IN_REVIEW', 'RECOMMENDED') then COALESCE (tbi.tender_document_size,0) else 0 end as tender_document_size, " +
            "case when ti.workflow_step IN('IN_REVIEW', 'RECOMMENDED') then COALESCE (to_json(tbi.tender_finance_info\\:\\:text) #>> '{}', to_json(''\\:\\:text) #>> '{}') else to_json(''\\:\\:text) #>> '{}'  end as tender_finance_info  " +
            "from tender_applicants ta left join application_user au on ta.application_user_id = au.id " +
            "left join tender_info ti  on ta.tender_info_id = ti.id " +
            "left join tender_bid_info tbi on tbi.application_user_id = au.id " +
            "where ta.tender_info_id =:tenderId order by ta.applicant_rank", nativeQuery = true)
    List<TenderApplicantsDashboardDto> getTenderApplicants(String tenderId);

    int countByTenderInfo(TenderInfo tenderInfo);

    Optional<TenderApplicants> findByApplicationUserAndTenderInfo(ApplicationUser applicationUser, TenderInfo tenderInfo);

    List<TenderApplicants> findAllByTenderInfoAndIdIn(TenderInfo tenderInfo, List<Integer> id);
    List<TenderApplicants> findAllByTenderInfo(TenderInfo tenderInfo);
}
