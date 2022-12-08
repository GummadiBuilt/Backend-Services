package com.infra.gummadibuilt.pqform;

import com.infra.gummadibuilt.common.exception.InvalidActionException;
import com.infra.gummadibuilt.pqform.model.PqFormHeader;
import com.infra.gummadibuilt.pqform.model.dto.PqFormHeaderCreateDto;
import com.infra.gummadibuilt.pqform.model.dto.PqFormHeaderDto;
import com.infra.gummadibuilt.tender.TenderInfoDao;
import com.infra.gummadibuilt.tender.model.TenderInfo;
import com.infra.gummadibuilt.tender.model.WorkflowStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static com.infra.gummadibuilt.common.util.CommonModuleUtils.TENDER_NOT_FOUND;
import static com.infra.gummadibuilt.common.util.CommonModuleUtils.getById;

@Service
public class PqFormHeaderService {

    private final PqFormHeaderDao pqFormHeaderDao;
    private final TenderInfoDao tenderInfoDao;

    @Autowired
    public PqFormHeaderService(PqFormHeaderDao pqFormHeaderDao, TenderInfoDao tenderInfoDao) {
        this.pqFormHeaderDao = pqFormHeaderDao;
        this.tenderInfoDao = tenderInfoDao;
    }

    public PqFormHeaderDto get(String tenderId, HttpServletRequest request) {

        TenderInfo tenderInfo = getById(tenderInfoDao, tenderId, TENDER_NOT_FOUND);
        validateFetch(request, tenderInfo);

        Optional<PqFormHeader> pqFormHeader = pqFormHeaderDao.findByTenderInfoId(tenderInfo);

        if (pqFormHeader.isPresent()) {
            return PqFormHeaderDto.valueOf(pqFormHeader.get());
        } else {
            PqFormHeader result = new PqFormHeader();
            result.setWorkPackage(tenderInfo.getWorkDescription());
            result.setContractDuration(tenderInfo.getContractDuration());
            result.setDurationCounter(tenderInfo.getDurationCounter());
            return PqFormHeaderDto.valueOf(result);
        }
    }

    public PqFormHeaderDto createPqForm(HttpServletRequest request, String tenderId, PqFormHeaderCreateDto pqFormHeaderCreateDto) {

        TenderInfo tenderInfo = getById(tenderInfoDao, tenderId, TENDER_NOT_FOUND);

        return new PqFormHeaderDto();
    }

    private void validateFetch(HttpServletRequest request, TenderInfo tenderInfo) {
        if (request.isUserInRole("contractor") & tenderInfo.getWorkflowStep() != WorkflowStep.PUBLISHED) {
            throw new InvalidActionException(
                    String.format("Cannot access PQ form when tender is in %s", tenderInfo.getWorkflowStep().getText())
            );
        }

        if ((request.isUserInRole("client") || request.isUserInRole("admin"))
                & tenderInfo.getWorkflowStep() == WorkflowStep.SAVE) {
            throw new InvalidActionException(
                    String.format("Cannot access PQ form when tender is in %s", tenderInfo.getWorkflowStep().getText())
            );
        }
    }

}
