package com.infra.gummadibuilt.tender;

import com.infra.gummadibuilt.tender.model.TenderInfo;
import com.infra.gummadibuilt.tender.model.WorkflowStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface TenderInfoDao extends JpaRepository<TenderInfo, String> {
    @Query(value = "SELECT nextval('tender_id_seq')", nativeQuery = true)
    BigDecimal getNextVal();

    List<TenderInfo> findAllByWorkflowStepIn(List<WorkflowStep> workflowSteps);

    List<TenderInfo> findAllByWorkflowStepInAndTypeOfWorkIn(List<WorkflowStep> workflowSteps, List<String> typeOfWork);
}
