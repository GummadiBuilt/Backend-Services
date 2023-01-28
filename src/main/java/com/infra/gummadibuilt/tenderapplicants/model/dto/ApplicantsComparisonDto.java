package com.infra.gummadibuilt.tenderapplicants.model.dto;

import com.infra.gummadibuilt.tender.model.dto.TenderDetailsDto;
import com.infra.gummadibuilt.tenderapplicationform.model.dto.ApplicationFormDto;
import lombok.Data;

@Data
public class ApplicantsComparisonDto {
    private ApplicationFormDto applicationFormDto;

    private TenderDetailsDto tenderDetailsDto;

    private TenderApplicantsDto tenderApplicantsDto;

}
