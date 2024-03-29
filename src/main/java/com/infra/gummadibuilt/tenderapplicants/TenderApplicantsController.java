package com.infra.gummadibuilt.tenderapplicants;

import com.infra.gummadibuilt.common.file.FileDownloadDto;
import com.infra.gummadibuilt.tenderapplicants.model.dto.ApplicantsComparisonDto;
import com.infra.gummadibuilt.tenderapplicants.model.dto.TenderApplicantsDashboardDto;
import com.infra.gummadibuilt.tenderapplicants.model.dto.TenderApplicantsDto;
import com.infra.gummadibuilt.tenderapplicationform.model.dto.ActionTaken;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/tender-applicants/tender")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Tender Applicant Ranking APIs",
        description = "APIs that get information about the tender applicants & their rankings")
public class TenderApplicantsController {

    private final TenderApplicantsService tenderApplicantsService;
    private final ExportToExcel exportToExcel;

    @Autowired
    public TenderApplicantsController(TenderApplicantsService tenderApplicantsService,
                                      ExportToExcel exportToExcel) {
        this.tenderApplicantsService = tenderApplicantsService;
        this.exportToExcel = exportToExcel;
    }

    @Operation(summary = "Get applicants info by tender")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success, when list of tender applicants are retrieved",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TenderApplicantsDto.class)))),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    @GetMapping("/{tenderId}")
    @RolesAllowed({"admin"})
    public List<TenderApplicantsDto> get(@PathVariable @NotBlank String tenderId, HttpServletRequest request) {
        return tenderApplicantsService.get(tenderId, request);
    }

    @Operation(summary = "Update applicants rankings for a tender")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success, when list of tender applicants are retrieved",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TenderApplicantsDashboardDto.class)))),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    @PutMapping("/{tenderId}/update/{actionTaken}")
    @RolesAllowed({"admin"})
    public List<TenderApplicantsDto> updateRanking(HttpServletRequest request,
                                                   @PathVariable @NotBlank String tenderId,
                                                   @PathVariable @NotNull ActionTaken actionTaken,
                                                   @RequestBody List<TenderApplicantsDto> tenderApplicantsDto) {
        return tenderApplicantsService.updateRanking(request, tenderId, actionTaken, tenderApplicantsDto);
    }

    @Operation(summary = "Compare tender applicants for a given tender")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success, when list of tender applicants are retrieved",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TenderApplicantsDashboardDto.class)))),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    @GetMapping("/{tenderId}/compare/{applicantId}")
    @RolesAllowed({"admin"})
    public List<ApplicantsComparisonDto> compareApplicants(@PathVariable @NotBlank String tenderId,
                                                           @PathVariable List<String> applicantId,
                                                           HttpServletRequest request) {
        return tenderApplicantsService.compareApplicants(tenderId, applicantId, request);
    }

    @Operation(summary = "Recommend a contractor for tender")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success, when list of tender applicants are retrieved",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TenderApplicantsDashboardDto.class)))),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    @PutMapping("/{tenderId}/recommend/{applicationFormId}")
    @RolesAllowed({"admin"})
    public List<TenderApplicantsDto> recommendContractor(@PathVariable @NotBlank String tenderId,
                                                         @PathVariable String applicationFormId,
                                                         HttpServletRequest request) {
        return tenderApplicantsService.recommendContractor(request, tenderId, applicationFormId);
    }


    @GetMapping("/{tenderId}/export-to-excel/{applicantId}")
    @RolesAllowed({"admin"})
    public FileDownloadDto exportToExcel(@PathVariable @NotBlank String tenderId,
                                         @PathVariable List<String> applicantId,
                                         HttpServletRequest request) throws IOException {

        return exportToExcel.exportToExcel(tenderId, applicantId, request);
    }

}
