package com.infra.gummadibuilt.tenderapplicants.model.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Data
public class TenderApplicantsDto {

    private int id;

    @NotNull
    private int applicantRank;

    @Size(max = 2500)
    private String justificationNote;

    @Enumerated(value = EnumType.STRING)
    private ApplicationStatus applicationStatus;

    @NotNull
    private String applicationUserId;

    @NotNull
    private int applicationFormId;

    private String companyName;

    private String tenderId;

    private String tenderStatus;

    private String tenderDocument;

    private int tenderDocumentSize;

    private JsonNode tenderFinanceInfo;

    private float contractValue;

    private boolean isRecommended;

    private float totalRevenue;

    private float financialInfoTotal;

    private String localOfficeAddress;

    public static TenderApplicantsDto valueOf(TenderApplicantsDashboardDto dashboardDto) {
        TenderApplicantsDto tenderApplicantsDto = new TenderApplicantsDto();

        tenderApplicantsDto.setId(dashboardDto.getId());
        tenderApplicantsDto.setApplicantRank(dashboardDto.getApplicant_rank());
        tenderApplicantsDto.setJustificationNote(dashboardDto.getJustification_note());
        tenderApplicantsDto.setApplicationStatus(ApplicationStatus.valueOf(dashboardDto.getApplication_status()));
        tenderApplicantsDto.setApplicationFormId(dashboardDto.getApplicant_form_id());
        tenderApplicantsDto.setApplicationUserId(dashboardDto.getApplication_user_id());
        tenderApplicantsDto.setCompanyName(dashboardDto.getCompany_name());
        tenderApplicantsDto.setTenderId(dashboardDto.getTender_info_id());
        tenderApplicantsDto.setTenderStatus(dashboardDto.getWorkflow_step());
        tenderApplicantsDto.setTenderDocument(dashboardDto.getTender_document());
        tenderApplicantsDto.setTenderDocumentSize(dashboardDto.getTender_document_size());
        ObjectMapper mapper = new ObjectMapper();
        try {
            if (dashboardDto.getTender_finance_info().length() > 0) {
                tenderApplicantsDto.setTenderFinanceInfo(mapper.readTree(dashboardDto.getTender_finance_info()));
            }
            if (dashboardDto.getClient_references().length() > 0) {
                JsonNode clientRef = mapper.readTree(dashboardDto.getClient_references());
                List<JsonNode> contractValue = StreamSupport.stream(clientRef.spliterator(), true)
                        .filter(item -> item.get("details").asText().equalsIgnoreCase("Contract Value:"))
                        .collect(Collectors.toList());
                float totalContractValue = 0;
                if (contractValue.get(0).has("Project 1")) {
                    totalContractValue = totalContractValue + contractValue.get(0).get("Project 1").asLong();
                }
                if (contractValue.get(0).has("Project 2")) {
                    totalContractValue = totalContractValue + contractValue.get(0).get("Project 2").asLong();
                }
                if (contractValue.get(0).has("Project 3")) {
                    totalContractValue = totalContractValue + contractValue.get(0).get("Project 3").asLong();
                }
                tenderApplicantsDto.setContractValue(totalContractValue);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        tenderApplicantsDto.setLocalOfficeAddress(dashboardDto.getLocal_office_address());
        tenderApplicantsDto.setTotalRevenue(dashboardDto.getTotal_revenue());
        tenderApplicantsDto.setFinancialInfoTotal(dashboardDto.getFinancial_info());
        tenderApplicantsDto.setRecommended(dashboardDto.getIs_recommended());

        return tenderApplicantsDto;

    }
}
