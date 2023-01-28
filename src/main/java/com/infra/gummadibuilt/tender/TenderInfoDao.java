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

    @Query(value = "with af as (select * from application_form af  where application_user_id= :userId)," +
            "ta as (select * from tender_applicants where application_user_id= :userId) select ti.id as tender_id," +
            "pfh.id as pq_id, au.company_name, toe.establishment_description, ti.work_description," +
            "ti.project_location, toc.type_of_contract, ti.contract_duration, ti.duration_counter, ti.project_name," +
            "CASE WHEN ti.workflow_step  ='QUALIFIED' THEN coalesce(ta.application_status, 'NOT_QUALIFIED') " +
            "WHEN ti.workflow_step = 'RECOMMENDED' and ta.is_recommended IS true then 'RECOMMENDED' "+
            "WHEN ti.workflow_step = 'RECOMMENDED' and ta.is_recommended IS false then 'NOT_RECOMMENDED' "+
            "ELSE ti.workflow_step end as workflow_step," +
            "TO_CHAR(ti.last_date_of_submission\\:\\:date, 'dd/mm/yyyy') as last_date_of_submission," +
            "CASE WHEN ta.application_status ='QUALIFIED' THEN ti.tender_document_name ELSE '' end as tender_document_name," +
            "CASE WHEN ta.application_status ='QUALIFIED' THEN ti.tender_document_size ELSE 0 end as tender_document_size," +
            "coalesce(ti.estimated_budget, 0) as estimated_budget, ti.created_by, af.id as application_form_id," +
            "af.action_taken as app_form_status from tender_info ti " +
            "left join pq_form_header pfh on ti.id = pfh.tender_info_id " +
            "left join type_of_contract toc on ti.type_of_contract_id = toc.id " +
            "left join application_user au on ti.application_user_id = au.id " +
            "left join type_of_establishment toe on ti.type_of_establishment_desc = toe.establishment_description " +
            "left join af on ti.id = af.tender_info_id left join ta on ti.id = ta.tender_info_id " +
            "where ti.type_of_establishment_desc in (:typeOfEstablishment) and " +
            "ti.workflow_step not in ('YET_TO_BE_PUBLISHED', 'DRAFT')", nativeQuery = true)
    List<TenderDashboardProjection> getContractorDashboard(List<String> typeOfEstablishment, String userId);

    @Query(value = "with af as (select * from application_form af  where application_user_id= :userId), " +
            "ta as (select * from tender_applicants where application_user_id= :userId) select ti.id as tender_id, " +
            "CASE WHEN ti.workflow_step  ='QUALIFIED' THEN coalesce(ta.application_status, 'NOT_QUALIFIED') " +
            "WHEN ti.workflow_step = 'RECOMMENDED' and ta.is_recommended IS true then 'RECOMMENDED' "+
            "WHEN ti.workflow_step = 'RECOMMENDED' and ta.is_recommended IS false then 'NOT_RECOMMENDED' "+
            "ELSE ti.workflow_step end as workflow_step, CASE WHEN ta.application_status ='QUALIFIED' " +
            "THEN ti.tender_document_name ELSE '' end as tender_document_name," +
            "CASE WHEN ta.application_status ='QUALIFIED' THEN ti.tender_document_size ELSE 0 end as tender_document_size," +
            "ti.work_description,ti.project_location,ti.contract_duration,ti.duration_counter,ti.project_name," +
            "ti.created_by,TO_CHAR(ti.last_date_of_submission\\:\\:date, 'dd/mm/yyyy') as last_date_of_submission," +
            "coalesce(ti.estimated_budget, 0) as estimated_budget,ta.application_status,ta.tender_info_id," +
            "pfh.id as pq_id,toc.type_of_contract,toe.establishment_description,au.company_name," +
            "af.id as application_form_id,af.action_taken as app_form_status,af.application_user_id " +
            "from tender_info ti left join pq_form_header pfh on ti.id = pfh.tender_info_id " +
            "left join type_of_contract toc on ti.type_of_contract_id = toc.id " +
            "left join application_user au on ti.application_user_id = au.id " +
            "left join type_of_establishment toe on ti.type_of_establishment_desc = toe.establishment_description " +
            "right join af  on ti.id = af.tender_info_id left join ta on ti.id = ta.tender_info_id", nativeQuery = true)
    List<TenderDashboardProjection> getAppliedTenders(String userId);
}
