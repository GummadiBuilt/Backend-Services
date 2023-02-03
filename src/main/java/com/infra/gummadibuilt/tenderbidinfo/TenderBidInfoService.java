package com.infra.gummadibuilt.tenderbidinfo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.infra.gummadibuilt.common.ChangeTracking;
import com.infra.gummadibuilt.common.LoggedInUser;
import com.infra.gummadibuilt.common.exception.InvalidActionException;
import com.infra.gummadibuilt.common.file.AmazonFileService;
import com.infra.gummadibuilt.common.file.FileDownloadDto;
import com.infra.gummadibuilt.common.util.FileUtils;
import com.infra.gummadibuilt.common.util.SaveEntityConstraintHelper;
import com.infra.gummadibuilt.tender.TenderInfoDao;
import com.infra.gummadibuilt.tender.TypeOfContractDao;
import com.infra.gummadibuilt.tender.model.TenderInfo;
import com.infra.gummadibuilt.tender.model.dto.TenderDetailsDto;
import com.infra.gummadibuilt.tenderapplicants.TenderApplicantsDao;
import com.infra.gummadibuilt.tenderapplicants.model.TenderApplicants;
import com.infra.gummadibuilt.tenderapplicants.model.dto.ApplicationStatus;
import com.infra.gummadibuilt.tenderapplicationform.ApplicationFormDao;
import com.infra.gummadibuilt.tenderapplicationform.model.dto.ActionTaken;
import com.infra.gummadibuilt.tenderbidinfo.model.TenderBidInfo;
import com.infra.gummadibuilt.userandrole.ApplicationUserDao;
import com.infra.gummadibuilt.userandrole.model.ApplicationUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import static com.infra.gummadibuilt.common.util.CommonModuleUtils.*;
import static com.infra.gummadibuilt.common.util.UserInfo.loggedInUserInfo;

@Service
public class TenderBidInfoService {
    private static final Logger logger = LoggerFactory.getLogger(TenderBidInfoService.class);

    private static final Map<String, Supplier<? extends RuntimeException>> CONSTRAINT_MAPPING = ImmutableMap
            .of(TenderBidInfo.UNQ_NAME_CONSTRAINT, TenderBidInfoAlreadyExists::new);


    private final ObjectMapper mapper;

    private final TenderApplicantsDao tenderApplicantsDao;

    private final TypeOfContractDao typeOfContractDao;


    private final TenderInfoDao tenderInfoDao;

    private final AmazonFileService amazonFileService;

    private final ApplicationUserDao applicationUserDao;

    private final ApplicationFormDao applicationFormDao;
    private final TenderBidInfoDao tenderBidInfoDao;

    public TenderBidInfoService(ObjectMapper mapper,
                                TenderBidInfoDao tenderBidInfoDao,
                                TypeOfContractDao typeOfContractDao,
                                TenderInfoDao tenderInfoDao,
                                AmazonFileService amazonFileService,
                                ApplicationUserDao applicationUserDao,
                                ApplicationFormDao applicationFormDao,
                                TenderApplicantsDao tenderApplicantsDao) {
        this.mapper = mapper;
        this.tenderBidInfoDao = tenderBidInfoDao;
        this.typeOfContractDao = typeOfContractDao;
        this.tenderInfoDao = tenderInfoDao;
        this.amazonFileService = amazonFileService;
        this.applicationUserDao = applicationUserDao;
        this.applicationFormDao = applicationFormDao;
        this.tenderApplicantsDao = tenderApplicantsDao;
    }

    @Transactional
    public TenderDetailsDto createTenderBidInfo(HttpServletRequest request,
                                                String tenderId,
                                                MultipartFile contractorDocument,
                                                String financialBidInfo,
                                                String actionTaken) throws JsonProcessingException {
        LoggedInUser loggedInUser = loggedInUserInfo(request);
        String userId = loggedInUser.getUserId();
        ApplicationUser applicationUser = getById(applicationUserDao, userId, USER_NOT_FOUND);
        TenderInfo tenderInfo = getById(tenderInfoDao, tenderId, TENDER_NOT_FOUND);
        validateApplicantUserQualification(applicationUser, tenderInfo);
        validateTenderLastDate(tenderInfo);

        TenderBidInfo tenderBidInfo = new TenderBidInfo();
        JsonNode financialInfo = mapper.readTree(financialBidInfo);

        FileUtils.checkFileValidOrNot(contractorDocument);
        String filePath = getFilePath(tenderInfo, loggedInUser.getUserId());
        String response = amazonFileService.uploadFile(filePath, metaData(tenderInfo), contractorDocument);
        logger.info(String.format("File upload success, generated ETAG %s", response));

        ActionTaken taken = actionTaken.equalsIgnoreCase(ActionTaken.DRAFT.getText()) ? ActionTaken.DRAFT : ActionTaken.SUBMIT;

        tenderBidInfo.setApplicationUser(applicationUser);
        tenderBidInfo.setTenderInfo(tenderInfo);
        tenderBidInfo.setTenderFinanceInfo(financialInfo);
        tenderBidInfo.setTenderDocumentName(contractorDocument.getOriginalFilename());
        tenderBidInfo.setTenderDocumentSize(contractorDocument.getSize());
        tenderBidInfo.setActionTaken(taken);
        tenderBidInfo.setChangeTracking(new ChangeTracking(loggedInUser.toString()));

        SaveEntityConstraintHelper.save(tenderBidInfoDao, tenderBidInfo, CONSTRAINT_MAPPING);

        TenderDetailsDto dto = TenderDetailsDto.valueOf(tenderInfo, true);
        dto.setContractorDocumentName(tenderBidInfo.getTenderDocumentName());
        dto.setTenderFinanceInfo(financialInfo);
        dto.setContractorDocumentSize(tenderBidInfo.getTenderDocumentSize());
        dto.setContractorBidId(tenderBidInfo.getId());
        dto.setContractorActionTaken(tenderBidInfo.getActionTaken());
        return dto;

    }

    @Transactional
    public TenderDetailsDto updateTenderBidInfo(HttpServletRequest request,
                                                String tenderId,
                                                String bidInfoId,
                                                MultipartFile contractorDocument,
                                                String financialBidInfo,
                                                String actionTaken) throws JsonProcessingException {
        LoggedInUser loggedInUser = loggedInUserInfo(request);
        int bidId = Integer.parseInt(bidInfoId);
        String userId = loggedInUser.getUserId();
        ApplicationUser applicationUser = getById(applicationUserDao, userId, USER_NOT_FOUND);
        TenderInfo tenderInfo = getById(tenderInfoDao, tenderId, TENDER_NOT_FOUND);
        validateApplicantUserQualification(applicationUser, tenderInfo);
        TenderBidInfo bidInfo = getById(tenderBidInfoDao, bidId, TENDER_BID_NOT_FOUND);
        if (bidInfo.getActionTaken() == ActionTaken.SUBMIT) {
            throw new InvalidActionException(
                    String.format("Your bid for tender %s is already submitted", bidInfo.getTenderInfo().getId())
            );
        }
        validateTenderLastDate(tenderInfo);
        validateBidTender(tenderInfo, bidInfo);
        validateBidUserAndLoggedInUser(bidInfo, loggedInUser);

        JsonNode financialInfo = mapper.readTree(financialBidInfo);
        bidInfo.setTenderFinanceInfo(financialInfo);
        bidInfo.getChangeTracking().update(loggedInUser.toString());

        if (!contractorDocument.getOriginalFilename().equalsIgnoreCase("blob")) {
            FileUtils.checkFileValidOrNot(contractorDocument);
            String filePath = getFilePath(tenderInfo, loggedInUser.getUserId());
            amazonFileService.deleteFile(filePath, bidInfo.getTenderDocumentName());
            String response = amazonFileService.uploadFile(filePath, metaData(tenderInfo), contractorDocument);
            logger.info(String.format("File upload success, generated ETAG %s", response));
            bidInfo.setTenderDocumentName(contractorDocument.getOriginalFilename());
            bidInfo.setTenderDocumentSize(contractorDocument.getSize());
        }

        ActionTaken taken = actionTaken.equalsIgnoreCase(ActionTaken.DRAFT.getText()) ? ActionTaken.DRAFT : ActionTaken.SUBMIT;
        bidInfo.setActionTaken(taken);

        SaveEntityConstraintHelper.save(tenderBidInfoDao, bidInfo, CONSTRAINT_MAPPING);

        TenderDetailsDto dto = TenderDetailsDto.valueOf(tenderInfo, true);
        dto.setContractorDocumentName(bidInfo.getTenderDocumentName());
        dto.setTenderFinanceInfo(financialInfo);
        dto.setContractorDocumentSize(bidInfo.getTenderDocumentSize());
        dto.setContractorBidId(bidInfo.getId());
        dto.setContractorActionTaken(bidInfo.getActionTaken());
        return dto;
    }

    public FileDownloadDto downloadTender(String tenderId, String userId, HttpServletRequest request) {
        TenderInfo tenderInfo = getById(tenderInfoDao, tenderId, TENDER_NOT_FOUND);
        LoggedInUser loggedInUser = loggedInUserInfo(request);
        String logInUser = userId;
        if (request.isUserInRole("contractor")) {
            logInUser = loggedInUser.getUserId();
        }
        if (request.isUserInRole("client") && Objects.equals(tenderInfo.getApplicationUser().getId(), loggedInUser.getUserId())) {
            throw new AccessDeniedException(
                    "Cannot access tenders that are not created by you. This action will be reported"
            );
        }

        ApplicationUser applicationUser = getById(applicationUserDao, logInUser, USER_NOT_FOUND);
        Optional<TenderBidInfo> bidInfo = tenderBidInfoDao.findByTenderInfoAndApplicationUser(tenderInfo, applicationUser);
        if (bidInfo.isPresent()) {
            String filePath = getFilePath(tenderInfo, logInUser);
            return amazonFileService.downloadFile(filePath, bidInfo.get().getTenderDocumentName());
        } else {
            throw new InvalidActionException(String.format("No contractor response found for tender %s", tenderId));
        }
    }

    private Map<String, String> metaData(TenderInfo tenderInfo) {
        Map<String, String> metaData = new HashMap<>();
        metaData.put("TenderID", tenderInfo.getId());
        metaData.put("TypeOfContract", tenderInfo.getTypeOfContract().getTypeOfContract());

        return metaData;
    }

    private void validateTenderLastDate(TenderInfo tenderInfo) {
        if (dayDiff(tenderInfo.getLastDateOfSubmission()) < 0) {
            throw new InvalidActionException(
                    String.format("Cannot apply to tender %s as the last date of submission is in past", tenderInfo.getId())
            );
        }
    }

    private void validateApplicantUserQualification(ApplicationUser applicationUser, TenderInfo tenderInfo) {
        String tenderId = tenderInfo.getId();
        Optional<TenderApplicants> tenderApplicants = tenderApplicantsDao.findByApplicationUserAndTenderInfo(applicationUser, tenderInfo);
        if (tenderApplicants.isPresent()) {
            if (tenderApplicants.get().getApplicationStatus() != ApplicationStatus.QUALIFIED) {
                throw new InvalidActionException(String.format("You are not qualified to bid for this tender %s", tenderId));
            }
        } else {
            throw new InvalidActionException(String.format("You cannot create tender bid for tender %s", tenderId));
        }
    }

    private void validateBidTender(TenderInfo tenderInfo, TenderBidInfo tenderBidInfo) {
        String tenderBidId = tenderBidInfo.getTenderInfo().getId();
        String tenderId = tenderInfo.getId();
        if (!Objects.equals(tenderBidId, tenderId)) {
            throw new InvalidActionException(
                    String.format("Tender & Bid Doesnt have the same tender-id %s %s", tenderId, tenderBidId)
            );
        }
    }

    private void validateBidUserAndLoggedInUser(TenderBidInfo tenderBidInfo, LoggedInUser loggedInUser) {
        String tenderBidUser = tenderBidInfo.getApplicationUser().getId();
        String userId = loggedInUser.getUserId();
        if (!Objects.equals(tenderBidUser, userId)) {
            throw new InvalidActionException(
                    String.format("You cannot access tender %s, as this doesnt belong to you", tenderBidInfo.getTenderInfo().getId())
            );
        }
    }

    private String getFilePath(TenderInfo tenderInfo, String loggedInUser) {
        return String.format("%s/%s/%s", tenderInfo.getId(), loggedInUser, "FINANCIAL_BID");
    }
}
