package com.infra.gummadibuilt.tender;

import com.infra.gummadibuilt.masterdata.common.model.TypeOfEstablishment;
import com.infra.gummadibuilt.tender.model.TenderInfo;
import com.infra.gummadibuilt.tender.model.WorkflowStep;
import com.infra.gummadibuilt.tender.model.dto.TenderDashboardProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

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
            "TO_CHAR(ti.last_date_of_submission\\:\\:date, 'dd/mm/yyyy') as last_date_of_submission, ti.estimated_budget, " +
            "ti.workflow_step, ti.tender_document_name, ti.tender_document_size, ti.created_by " +
            "from tender_info ti left join pq_form_header pfh on ti.id = pfh.tender_info_id left join type_of_contract " +
            "toc on ti.type_of_contract_id  = toc.id left join application_user au  on ti.application_user_id = au.id " +
            "left join type_of_establishment toe on ti.type_of_establishment_desc = toe.establishment_description " +
            "where workflow_step not in ('SAVE')", nativeQuery = true)
    List<TenderDashboardProjection> getAdminDashboard();

    @Query(value = "select ti.id as tender_id, pfh.id as pq_id, au.company_name, toe.establishment_description, " +
            "ti.work_description, ti.project_location, toc.type_of_contract, ti.contract_duration, ti.duration_counter," +
            "TO_CHAR(ti.last_date_of_submission\\:\\:date, 'dd/mm/yyyy') as last_date_of_submission, ti.estimated_budget, " +
            "ti.workflow_step, ti.tender_document_name, ti.tender_document_size, ti.created_by " +
            "from tender_info ti left join pq_form_header pfh on ti.id = pfh.tender_info_id left join type_of_contract " +
            "toc on ti.type_of_contract_id  = toc.id left join application_user au  on ti.application_user_id = au.id " +
            "left join type_of_establishment toe on ti.type_of_establishment_desc = toe.establishment_description " +
            "where ti.application_user_id = ?1", nativeQuery = true)
    List<TenderDashboardProjection> getClientDashboard(String userId);

    @Query(value = "select ti.id as tender_id, pfh.id as pq_id, au.company_name, toe.establishment_description, " +
            "ti.work_description, ti.project_location, toc.type_of_contract, ti.contract_duration, ti.duration_counter," +
            "TO_CHAR(ti.last_date_of_submission\\:\\:date, 'dd/mm/yyyy') as last_date_of_submission, ti.estimated_budget, " +
            "ti.workflow_step, '' as tender_document_name, 0 as tender_document_size, ti.created_by " +
            "from tender_info ti left join pq_form_header pfh on ti.id = pfh.tender_info_id left join type_of_contract " +
            "toc on ti.type_of_contract_id  = toc.id left join application_user au  on ti.application_user_id = au.id " +
            "left join type_of_establishment toe on ti.type_of_establishment_desc = toe.establishment_description " +
            "where ti.type_of_establishment_desc in (?1) and ti.workflow_step in ('PUBLISHED')", nativeQuery = true)
    List<TenderDashboardProjection> getContractorDashboard(String typeOfEstablishment);
}
