package com.infra.gummadibuilt.scheduler;

import com.infra.gummadibuilt.common.exception.InvalidActionException;
import com.infra.gummadibuilt.common.util.SaveEntityConstraintHelper;
import com.infra.gummadibuilt.tender.TenderInfoDao;
import com.infra.gummadibuilt.tender.model.TenderInfo;
import com.infra.gummadibuilt.tender.model.WorkflowStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
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
        List<WorkflowStep> workflowSteps = Arrays.asList(WorkflowStep.PUBLISHED, WorkflowStep.QUALIFIED);
        List<TenderInfo> tenderInfo = tenderInfoDao.findAllByWorkflowStepIn(workflowSteps);

        logger.info(String.format("%d tenders in Published state", tenderInfo.size()));

        tenderInfo = tenderInfo.stream().peek(
                item -> {
                    String tenderId = item.getId();
                    logger.info(String.format("Processing tender %s", tenderId));

                    String workflowStep = item.getWorkflowStep().getText().toUpperCase();

                    switch (workflowStep) {
                        case "QUALIFIED":
                            logger.info(String.format("Processing tender %s for QUALIFIED", tenderId));
                            LocalDate tenderLastDateOfSubmission = item.getLastDateOfSubmission();
                            logger.info(String.format("Tender %s has last date of submission %s", tenderId, tenderLastDateOfSubmission.toString()));
                            if (tenderLastDateOfSubmission.isBefore(localDate)) {
                                logger.info(String.format("Updated tender %s to in review status", tenderId));
                                item.setWorkflowStep(WorkflowStep.IN_REVIEW);
                            } else {
                                logger.info(String.format("Skipping Tender %s", tenderId));
                            }
                            break;
                        case "PUBLISHED":
                            if (item.getFormHeader() != null) {
                                logger.info(String.format("Processing tender %s for PQ", tenderId));
                                LocalDate pqSubmissionDate = item.getFormHeader().getPqLastDateOfSubmission();
                                logger.info(String.format("Tender %s has PQ submission data as %s", tenderId, pqSubmissionDate.toString()));
                                if (pqSubmissionDate.isBefore(localDate)) {
                                    logger.info(String.format("Updated tender %s to under process status", tenderId));
                                    item.setWorkflowStep(WorkflowStep.UNDER_PROCESS);
                                } else {
                                    logger.info(String.format("Skipping Tender %s", tenderId));
                                }
                            } else {
                                logger.error(String.format("Tender %s has no PQ Form but with status published", tenderId));
                            }
                            break;
                        default:
                            throw new InvalidActionException(String.format("Cannot process tender %s", tenderId));
                    }

                }
        ).collect(Collectors.toList());

        logger.info("Updated tender status & saving the updates to database");
        SaveEntityConstraintHelper.saveAll(tenderInfoDao, tenderInfo, null);

        logger.info("Completed Update Tender Status job");
    }
}
