package com.infra.gummadibuilt.tender;

import com.infra.gummadibuilt.masterdata.common.model.TypeOfEstablishment;
import com.infra.gummadibuilt.tender.model.TenderInfo;
import com.infra.gummadibuilt.tender.model.WorkflowStep;
import com.infra.gummadibuilt.tender.model.dto.TenderDashboardProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TenderInfoDao extends JpaRepository<TenderInfo, String> {
    @Query(value = "SELECT nextval('tender_id_seq')", nativeQuery = true)
    BigDecimal getNextVal();

    List<TenderInfo> findAllByWorkflowStepIn(List<WorkflowStep> workflowSteps);

    List<TenderInfo> findAllByWorkflowStepInAndTypeOfEstablishmentIn(
            List<WorkflowStep> workflowSteps,
            List<TypeOfEstablishment> typeOfWork
    );

    @Query(value = "select ti.id as tender_id, pfh.id as pq_id, au.company_name, toe.establishment_description, " +
            "ti.work_description, ti.project_location, toc.type_of_contract, ti.contract_duration, ti.duration_counter," +
            "ti.project_name, TO_CHAR(ti.last_date_of_submission\\:\\:date, 'dd/mm/yyyy') as last_date_of_submission," +
            "coalesce(ti.estimated_budget,0) as estimated_budget, ti.workflow_step, ti.tender_document_name, ti.tender_document_size, ti.created_by " +
            "from tender_info ti left join pq_form_header pfh on ti.id = pfh.tender_info_id left join type_of_contract " +
            "toc on ti.type_of_contract_id  = toc.id left join application_user au  on ti.application_user_id = au.id " +
            "left join type_of_establishment toe on ti.type_of_establishment_desc = toe.establishment_description " +
            "where workflow_step not in ('DRAFT')", nativeQuery = true)
    List<TenderDashboardProjection> getAdminDashboard();

    @Query(value = "select ti.id as tender_id, pfh.id as pq_id, au.company_name, toe.establishment_description, " +
            "ti.work_description, ti.project_location, toc.type_of_contract, ti.contract_duration, ti.duration_counter," +
            "ti.project_name, TO_CHAR(ti.last_date_of_submission\\:\\:date, 'dd/mm/yyyy') as last_date_of_submission," +
            "coalesce(ti.estimated_budget,0) as estimated_budget, ti.workflow_step, ti.tender_document_name, ti.tender_document_size, ti.created_by " +
            "from tender_info ti left join pq_form_header pfh on ti.id = pfh.tender_info_id left join type_of_contract " +
            "toc on ti.type_of_contract_id  = toc.id left join application_user au  on ti.application_user_id = au.id " +
            "left join type_of_establishment toe on ti.type_of_establishment_desc = toe.establishment_description " +
            "where ti.application_user_id = ?1", nativeQuery = true)
    List<TenderDashboardProjection> getClientDashboard(String userId);

    @Query(value = "select ti.id as tender_id, pfh.id as pq_id, au.company_name, toe.establishment_description, " +
            "ti.work_description, ti.project_location, toc.type_of_contract, ti.contract_duration, ti.duration_counter," +
            "ti.project_name, TO_CHAR(ti.last_date_of_submission\\:\\:date, 'dd/mm/yyyy') as last_date_of_submission, " +
            "coalesce(ti.estimated_budget,0) as estimated_budget, " +
            "(select case when ti.workflow_step in('QUALIFIED') then case when ta.application_status = 'QUALIFIED' then 'QUALIFIED' else 'NOT_QUALIFIED' end else ti.workflow_step end from application_form af left join tender_applicants ta on ta.applicant_form_id = af.id left join tender_info ti on ti.id = af.tender_info_id where ta.application_user_id = :userId and ta.tender_info_id =ti.id) as workflow_step,"+
            "(select case when ta.application_status = 'QUALIFIED' then ti.tender_document_name else '' end from tender_info ti left join tender_applicants ta on ta.tender_info_id  = ti.id where ta.application_user_id = :userId) as tender_document_name,"+
            "0 as tender_document_size, ti.created_by, " +
            "(select af.id from application_form af  where af.application_user_id = :userId and af.tender_info_id =ti.id ) as application_form_id, "+
            "(select af.action_taken  from application_form af  where af.application_user_id = :userId and af.tender_info_id =ti.id ) as app_form_status "+
            "from tender_info ti left join pq_form_header pfh on ti.id = pfh.tender_info_id left join type_of_contract " +
            "toc on ti.type_of_contract_id  = toc.id left join application_user au  on ti.application_user_id = au.id " +
            "left join type_of_establishment toe on ti.type_of_establishment_desc = toe.establishment_description " +
            "where ti.type_of_establishment_desc in (:typeOfEstablishment)" +
            " and ti.workflow_step not in ('YET_TO_BE_PUBLISHED', 'DRAFT')", nativeQuery = true)
    List<TenderDashboardProjection> getContractorDashboard(List<String> typeOfEstablishment, String userId);

    @Query(value = "select ti.id as tender_id, pfh.id as pq_id, af.application_user_id , " +
            "af.id as application_form_id, au.company_name, af.action_taken as app_form_status, " +
            "toe.establishment_description, ti.work_description, ti.project_location, toc.type_of_contract, " +
            "ti.contract_duration, ti.duration_counter, ti.project_name, " +
            "TO_CHAR(ti.last_date_of_submission\\:\\:date, 'dd/mm/yyyy') as last_date_of_submission, " +
            "coalesce(ti.estimated_budget,0) as estimated_budget," +
            "(select case when ti.workflow_step in('QUALIFIED') then case when ta.application_status = 'QUALIFIED' then 'QUALIFIED' else 'NOT_QUALIFIED' end else ti.workflow_step end from application_form af left join tender_applicants ta on ta.applicant_form_id = af.id left join tender_info ti on ti.id = af.tender_info_id where ta.application_user_id = :userId and ta.tender_info_id =ti.id) as workflow_step,"+
            "(select case when ta.application_status = 'QUALIFIED' then ti.tender_document_name else '' end from tender_info ti left join tender_applicants ta on ta.tender_info_id  = ti.id where ta.application_user_id = :userId) as tender_document_name,"+
            " ti.tender_document_size, ti.created_by from tender_info ti " +
            "left join pq_form_header pfh on ti.id = pfh.tender_info_id " +
            "left join type_of_contract toc on ti.type_of_contract_id = toc.id " +
            "left join application_user au on ti.application_user_id = au.id " +
            "left join type_of_establishment toe on ti.type_of_establishment_desc = toe.establishment_description " +
            "left join application_form af on ti.id = af.tender_info_id " +
            "where af.application_user_id = :userId",nativeQuery = true)
    List<TenderDashboardProjection> getAppliedTenders(String userId);
}
