package com.infra.gummadibuilt.tenderapplicants;

import com.infra.gummadibuilt.tender.TenderInfoDao;
import com.infra.gummadibuilt.tenderapplicants.model.dto.TenderApplicantsDashboardDto;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.infra.gummadibuilt.common.util.CommonModuleUtils.TENDER_NOT_FOUND;
import static com.infra.gummadibuilt.common.util.CommonModuleUtils.getById;

@Service
public class TenderApplicantsService {

    private final TenderApplicantsDao tenderApplicantsDao;
    private final TenderInfoDao tenderInfoDao;

    public TenderApplicantsService(TenderApplicantsDao tenderApplicantsDao, TenderInfoDao tenderInfoDao) {
        this.tenderApplicantsDao = tenderApplicantsDao;
        this.tenderInfoDao = tenderInfoDao;
    }

    public List<TenderApplicantsDashboardDto> get(String tenderId) {
        getById(tenderInfoDao, tenderId, TENDER_NOT_FOUND);
        return tenderApplicantsDao.getTenderApplicants(tenderId);
    }
}
