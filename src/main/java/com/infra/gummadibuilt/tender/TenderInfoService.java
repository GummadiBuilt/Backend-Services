package com.infra.gummadibuilt.tender;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infra.gummadibuilt.common.ChangeTracking;
import com.infra.gummadibuilt.common.LoggedInUser;
import com.infra.gummadibuilt.common.exception.EntityFieldNotNullException;
import com.infra.gummadibuilt.common.exception.EntityFieldSizeLimitException;
import com.infra.gummadibuilt.common.file.AmazonFileService;
import com.infra.gummadibuilt.common.file.FileDownloadDto;
import com.infra.gummadibuilt.common.util.FileUtils;
import com.infra.gummadibuilt.common.util.SaveEntityConstraintHelper;
import com.infra.gummadibuilt.tender.model.TenderInfo;
import com.infra.gummadibuilt.tender.model.TypeOfContract;
import com.infra.gummadibuilt.tender.model.WorkflowStep;
import com.infra.gummadibuilt.tender.model.dto.CreateTenderInfoDto;
import com.infra.gummadibuilt.tender.model.dto.TenderDetailsDto;
import com.infra.gummadibuilt.userandrole.ApplicationUserDao;
import com.infra.gummadibuilt.userandrole.model.ApplicationUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.infra.gummadibuilt.common.util.CommonModuleUtils.*;
import static com.infra.gummadibuilt.common.util.UserInfo.loggedInUserInfo;

@Service
public class TenderInfoService {

    private static final Logger logger = LoggerFactory.getLogger(TenderInfoService.class);
    private final ObjectMapper mapper;

    private final Validator validator;

    private final TypeOfContractDao typeOfContractDao;

    private final TenderInfoDao tenderInfoDao;

    private final AmazonFileService amazonFileService;

    private final ApplicationUserDao applicationUserDao;

    public TenderInfoService(ObjectMapper mapper,
                             Validator validator,
                             TypeOfContractDao typeOfContractDao,
                             TenderInfoDao tenderInfoDao,
                             AmazonFileService amazonFileService,
                             ApplicationUserDao applicationUserDao) {
        this.mapper = mapper;
        this.validator = validator;
        this.typeOfContractDao = typeOfContractDao;
        this.tenderInfoDao = tenderInfoDao;
        this.amazonFileService = amazonFileService;
        this.applicationUserDao = applicationUserDao;
    }

    @Transactional
    public TenderDetailsDto createTender(HttpServletRequest request, MultipartFile tenderDocument, String tenderInformation) throws JsonProcessingException {

        LoggedInUser loggedInUser = loggedInUserInfo(request);

        logger.info(String.format("User %s initiated tender creation", loggedInUser));

        FileUtils.checkFileValidOrNot(tenderDocument);
        CreateTenderInfoDto tenderInfoDto = mapper.readValue(tenderInformation, CreateTenderInfoDto.class);
        validateTenderInfo(tenderInfoDto);

        TenderInfo tenderInfo = new TenderInfo();
        TypeOfContract typeOfContract =
                getById(typeOfContractDao, tenderInfoDto.getTypeOfContract(), TYPE_OF_CONTRACT_NOT_FOUND);
        tenderInfo.setTypeOfContract(typeOfContract);
        tenderInfo.setTenderDocumentName(tenderDocument.getOriginalFilename());
        tenderInfo.setTenderDocumentSize(tenderDocument.getSize());

        createTenderInfo(tenderInfo, tenderInfoDto);
        tenderInfo.setId(this.tenderIdGenerator(tenderInfo));
        ApplicationUser applicationUser = getById(applicationUserDao, loggedInUser.getUserId(), USER_NOT_FOUND);
        tenderInfo.setApplicationUser(applicationUser);
        tenderInfo.setChangeTracking(new ChangeTracking(loggedInUser.toString()));
        SaveEntityConstraintHelper.save(tenderInfoDao, tenderInfo, null);
        logger.info(String.format("User %s created Tender %s", loggedInUser, tenderInfo.getId()));

        String response = amazonFileService.uploadFile(tenderInfo.getId(), metaData(tenderInfo), tenderDocument);
        logger.info(String.format("File upload success, generated ETAG %s", response));

        return TenderDetailsDto.valueOf(tenderInfo);
    }


    @Transactional
    public TenderDetailsDto updateTender(HttpServletRequest request,
                                         String tenderId,
                                         MultipartFile tenderDocument,
                                         String tenderInformation) throws JsonProcessingException {
        LoggedInUser loggedInUser = loggedInUserInfo(request);

        logger.info(String.format("User %s initiated tender update process", loggedInUser));
        TenderInfo tenderInfo = getById(tenderInfoDao, tenderId, TENDER_NOT_FOUND);
        CreateTenderInfoDto tenderInfoDto = mapper.readValue(tenderInformation, CreateTenderInfoDto.class);
        validateTenderInfo(tenderInfoDto);

        if (!tenderDocument.isEmpty()) {
            FileUtils.checkFileValidOrNot(tenderDocument);
            amazonFileService.deleteFile(tenderInfo.getId(), tenderInfo.getTenderDocumentName());
            String response = amazonFileService.uploadFile(tenderInfo.getId(), metaData(tenderInfo), tenderDocument);
            logger.info(String.format("File upload success, generated ETAG %s", response));
            tenderInfo.setTenderDocumentName(tenderDocument.getOriginalFilename());
            tenderInfo.setTenderDocumentSize(tenderDocument.getSize());
        }

        TypeOfContract typeOfContract =
                getById(typeOfContractDao, tenderInfoDto.getTypeOfContract(), TYPE_OF_CONTRACT_NOT_FOUND);
        tenderInfo.setTypeOfContract(typeOfContract);
        createTenderInfo(tenderInfo, tenderInfoDto);
        tenderInfo.getChangeTracking().update(loggedInUser.toString());

        SaveEntityConstraintHelper.save(tenderInfoDao, tenderInfo, null);
        logger.info(String.format("User %s updated Tender %s", loggedInUser, tenderInfo.getId()));
        return TenderDetailsDto.valueOf(tenderInfo);
    }

    public List<TenderDetailsDto> getAllTenders(HttpServletRequest request) {
        List<TenderDetailsDto> tenderDetailsDtos;
        LoggedInUser loggedInUser = loggedInUserInfo(request);
        ApplicationUser applicationUser = getById(applicationUserDao, loggedInUser.getUserId(), USER_NOT_FOUND);

        if (request.isUserInRole("admin")) {
            List<WorkflowStep> workflowSteps = Arrays.asList(
                    WorkflowStep.ARCHIVED,
                    WorkflowStep.UNDER_PROCESS,
                    WorkflowStep.SUSPENDED,
                    WorkflowStep.PUBLISHED,
                    WorkflowStep.YET_TO_BE_PUBLISHED
            );
            tenderDetailsDtos = tenderInfoDao.findAllByWorkflowStepIn(workflowSteps)
                    .stream()
                    .map(TenderDetailsDto::valueOf)
                    .collect(Collectors.toList());

        } else if (request.isUserInRole("client")) {

            tenderDetailsDtos = applicationUser.getTenderInfo().stream().map(TenderDetailsDto::valueOf).collect(Collectors.toList());

        } else if (request.isUserInRole("contractor")) {
            List<String> typeOfWorks = applicationUser.getTypeOfEstablishment();
            List<WorkflowStep> workflowSteps = List.of(WorkflowStep.PUBLISHED);
            tenderDetailsDtos = tenderInfoDao.findAllByWorkflowStepInAndTypeOfWorkIn(workflowSteps, typeOfWorks)
                    .stream()
                    .map(TenderDetailsDto::valueOf)
                    .collect(Collectors.toList());
        } else {
            throw new RuntimeException("Token didnt match to any roles");
        }

        return tenderDetailsDtos;
    }


    public TenderDetailsDto getTenderInfo(HttpServletRequest request, String tenderId) {
        LoggedInUser loggedInUser = loggedInUserInfo(request);

        TenderInfo tenderInfo = getById(tenderInfoDao, tenderId, TENDER_NOT_FOUND);

        TenderDetailsDto tenderDetailsDto = new TenderDetailsDto();

        if (request.isUserInRole("contractor") && (tenderInfo.getWorkflowStep() == WorkflowStep.PUBLISHED)) {
            return TenderDetailsDto.valueOf(tenderInfo);
        } else if (request.isUserInRole("client") && Objects.equals(tenderInfo.getApplicationUser().getId(), loggedInUser.getUserId())) {
            return TenderDetailsDto.valueOf(tenderInfo);
        } else if (request.isUserInRole("admin") && (tenderInfo.getWorkflowStep() != WorkflowStep.SAVE)) {
            return TenderDetailsDto.valueOf(tenderInfo);
        }

        return tenderDetailsDto;
    }

    public FileDownloadDto downloadTender(String tenderId) {
        TenderInfo tenderInfo = getById(tenderInfoDao, tenderId, TENDER_NOT_FOUND);
        return amazonFileService.downloadFile(tenderInfo.getId(), tenderInfo.getTenderDocumentName());
    }

    private void createTenderInfo(TenderInfo tenderInfo, CreateTenderInfoDto createTenderInfoDto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
        LocalDate localDate = LocalDate.parse(createTenderInfoDto.getLastDateOfSubmission(), formatter);
        tenderInfo.setTypeOfWork(createTenderInfoDto.getTypeOfWork());
        tenderInfo.setWorkDescription(createTenderInfoDto.getWorkDescription());
        tenderInfo.setProjectLocation(createTenderInfoDto.getProjectLocation());
        tenderInfo.setContractDuration(createTenderInfoDto.getContractDuration());
        tenderInfo.setDurationCounter(createTenderInfoDto.getDurationCounter());
        tenderInfo.setEstimatedBudget(createTenderInfoDto.getEstimatedBudget());
        tenderInfo.setLastDateOfSubmission(localDate);
        tenderInfo.setWorkflowStep(createTenderInfoDto.getWorkflowStep());
        tenderInfo.setTenderFinanceInfo(createTenderInfoDto.getTenderFinanceInfo());
    }

    private String tenderIdGenerator(TenderInfo tenderInfo) {
        String shortCode = tenderInfo.getTypeOfContract().getContractShortCode();
        String subMonth = Integer.toString(tenderInfo.getLastDateOfSubmission().getMonthValue());
        String subYear = Integer.toString(tenderInfo.getLastDateOfSubmission().getYear()).substring(2);

        String nextVal = String.valueOf(tenderInfoDao.getNextVal());

        return String.format("%s%s%s-%s", shortCode, subMonth, subYear, nextVal);
    }

    private void validateTenderInfo(CreateTenderInfoDto tenderInfoDto) {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(tenderInfoDto);
        constraintViolations.forEach(violation -> {
            if (violation.getMessage().contains("null")) {
                throw new EntityFieldNotNullException(violation.getPropertyPath().toString());
            } else if (violation.getMessage().contains("size")) {
                throw new EntityFieldSizeLimitException(violation.getPropertyPath().toString(), 1, 50);
            }
        });
    }

    private Map<String, String> metaData(TenderInfo tenderInfo) {
        Map<String, String> metaData = new HashMap<>();
        metaData.put("TenderID", tenderInfo.getId());
        metaData.put("TypeOfContract", tenderInfo.getTypeOfContract().getTypeOfContract());

        return metaData;
    }
}
