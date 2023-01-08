package com.infra.gummadibuilt.scheduler;

import com.infra.gummadibuilt.common.util.SaveEntityConstraintHelper;
import com.infra.gummadibuilt.tender.TenderInfoDao;
import com.infra.gummadibuilt.tender.model.TenderInfo;
import com.infra.gummadibuilt.tender.model.WorkflowStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UpdateTenderStatus {

    private final TenderInfoDao tenderInfoDao;

    public UpdateTenderStatus(TenderInfoDao tenderInfoDao) {
        this.tenderInfoDao = tenderInfoDao;
    }

    private static final Logger logger = LoggerFactory.getLogger(UpdateTenderStatus.class);

    @Scheduled(cron = "${cron.schedule}")
    public void updateTenderStatus() {

        logger.info("Started Update Tender Status job");

        LocalDate localDate = LocalDate.now();
        List<TenderInfo> tenderInfo = tenderInfoDao.findAllByWorkflowStepIn(Collections.singletonList(WorkflowStep.PUBLISHED));

        logger.info(String.format("%d tenders in Published state", tenderInfo.size()));

        tenderInfo = tenderInfo.stream().peek(
                item -> {
                    String tenderId = item.getId();
                    logger.info(String.format("Processing tender %s", tenderId));
                    if (item.getFormHeader() != null) {
                        LocalDate pqSubmissionDate = item.getFormHeader().getPqLastDateOfSubmission();
                        logger.info(String.format("Tender %s has PQ submission data as %s", tenderId, pqSubmissionDate.toString()));
                        if (pqSubmissionDate.isBefore(localDate)) {
                            logger.info(String.format("Updated tender %s to under process status", tenderId));
                            item.setWorkflowStep(WorkflowStep.UNDER_PROCESS);
                        } else {
                            logger.info(String.format("Skipping Tender %s ", tenderId, pqSubmissionDate.toString()));
                        }
                    } else {
                        logger.error(String.format("Tender %s has no PQ Form but with status published", tenderId));
                    }
                }
        ).collect(Collectors.toList());

        logger.info("Updated tender status & saving the updates to database");
        SaveEntityConstraintHelper.saveAll(tenderInfoDao, tenderInfo, null);

        logger.info("Completed Update Tender Status job");
    }
}
