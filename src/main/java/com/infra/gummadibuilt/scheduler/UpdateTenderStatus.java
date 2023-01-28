package com.infra.gummadibuilt.scheduler;

import com.infra.gummadibuilt.common.exception.InvalidActionException;
import com.infra.gummadibuilt.common.mail.MailService;
import com.infra.gummadibuilt.common.util.SaveEntityConstraintHelper;
import com.infra.gummadibuilt.tender.TenderInfoDao;
import com.infra.gummadibuilt.tender.model.TenderInfo;
import com.infra.gummadibuilt.tender.model.WorkflowStep;
import com.infra.gummadibuilt.userregistration.UserRegistrationService;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.infra.gummadibuilt.common.util.CommonModuleUtils.DATE_PATTERN;

@Component
public class UpdateTenderStatus {

    private final TenderInfoDao tenderInfoDao;

    private final MailService mailService;

    private final UserRegistrationService userRegistrationService;

    public UpdateTenderStatus(TenderInfoDao tenderInfoDao, MailService mailService, UserRegistrationService userRegistrationService) {
        this.tenderInfoDao = tenderInfoDao;
        this.mailService = mailService;
        this.userRegistrationService = userRegistrationService;
    }

    private static final Logger logger = LoggerFactory.getLogger(UpdateTenderStatus.class);

    @Scheduled(cron = "${cron.schedule}")
    public void updateTenderStatus() throws MessagingException, TemplateException, IOException {

        logger.info("Started Update Tender Status job");

        LocalDate localDate = LocalDate.now();
        List<WorkflowStep> workflowSteps = Arrays.asList(WorkflowStep.PUBLISHED, WorkflowStep.QUALIFIED);
        List<TenderInfo> tenderInfo = tenderInfoDao.findAllByWorkflowStepIn(workflowSteps);

        logger.info(String.format("%d tenders in Published state", tenderInfo.size()));

        List<ActionableTenders> inReviewTenders = new ArrayList<>();
        List<ActionableTenders> underProcessTenders = new ArrayList<>();

        tenderInfo = tenderInfo.stream().peek(
                item -> {
                    String tenderId = item.getId();
                    logger.info(String.format("Processing tender %s", tenderId));
                    String workflowStep = item.getWorkflowStep().getText().toUpperCase();

                    switch (workflowStep) {
                        case "QUALIFIED":
                            ActionableTenders qualifiedTenders = new ActionableTenders();
                            logger.info(String.format("Processing tender %s for QUALIFIED", tenderId));
                            LocalDate tenderLastDateOfSubmission = item.getLastDateOfSubmission();
                            logger.info(String.format("Tender %s has last date of submission %s", tenderId, tenderLastDateOfSubmission.toString()));
                            if (tenderLastDateOfSubmission.isBefore(localDate)) {
                                logger.info(String.format("Updated tender %s to in review status", tenderId));
                                item.setWorkflowStep(WorkflowStep.IN_REVIEW);
                                qualifiedTenders.setTenderId(tenderId);
                                qualifiedTenders.setCompanyName(item.getApplicationUser().getCompanyName());
                                qualifiedTenders.setProjectName(item.getProjectName());
                                String lastDate = item.getLastDateOfSubmission().format(DateTimeFormatter.ofPattern(DATE_PATTERN));
                                qualifiedTenders.setTenderLastDateOfSubmission(lastDate);
                                qualifiedTenders.setWorkflowStep(item.getWorkflowStep().getText());
                                inReviewTenders.add(qualifiedTenders);
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
                                    ActionableTenders pqTenders = new ActionableTenders();
                                    logger.info(String.format("Updated tender %s to under process status", tenderId));
                                    item.setWorkflowStep(WorkflowStep.UNDER_PROCESS);

                                    pqTenders.setTenderId(tenderId);
                                    pqTenders.setCompanyName(item.getApplicationUser().getCompanyName());
                                    pqTenders.setProjectName(item.getProjectName());
                                    String lastDate = pqSubmissionDate.format(DateTimeFormatter.ofPattern(DATE_PATTERN));
                                    pqTenders.setTenderLastDateOfSubmission(lastDate);
                                    pqTenders.setWorkflowStep(item.getWorkflowStep().getText());
                                    underProcessTenders.add(pqTenders);
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

        if (inReviewTenders.size() > 0 || underProcessTenders.size() > 0) {
            logger.info("Sending email to Admins for actions to be taken");
            Map<String, Object> model = new HashMap<>();
            String[] adminUsers = userRegistrationService.getAdminUserEmailAddress();
            model.put("pq", underProcessTenders);
            model.put("inReview", inReviewTenders);
            mailService.sendMail(adminUsers, null, "actionsForToday.ftl", model);
            logger.info("Sent email to Admins for actions to be taken");
        }

        logger.info("Completed Update Tender Status job");
    }
}
