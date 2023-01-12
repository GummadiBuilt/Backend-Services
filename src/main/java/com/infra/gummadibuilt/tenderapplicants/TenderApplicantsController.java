package com.infra.gummadibuilt.tenderapplicants;

import com.infra.gummadibuilt.tenderapplicants.model.dto.TenderApplicantsDashboardDto;
import com.infra.gummadibuilt.tenderapplicants.model.dto.TenderApplicantsDto;
import com.infra.gummadibuilt.tenderapplicationform.model.dto.ApplicationFormDto;
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
import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
@RequestMapping("/tender-applicants/tender")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Tender Applicant Ranking APIs",
        description = "APIs that get information about the tender applicants & their rankings")
public class TenderApplicantsController {

    private final TenderApplicantsService tenderApplicantsService;

    @Autowired
    public TenderApplicantsController(TenderApplicantsService tenderApplicantsService) {
        this.tenderApplicantsService = tenderApplicantsService;
    }

    @Operation(summary = "Get applicants info by tender")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success, when list of tender applicants are retrieved",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TenderApplicantsDto.class)))),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    @GetMapping("/{tenderId}")
    @RolesAllowed({"admin", "client"})
    public List<TenderApplicantsDto> get(@PathVariable @NotBlank String tenderId) {
        return tenderApplicantsService.get(tenderId);
    }

    @Operation(summary = "Update applicants rankings for a tender")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success, when list of tender applicants are retrieved",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TenderApplicantsDashboardDto.class)))),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    @PutMapping("/{tenderId}/update")
    @RolesAllowed({"admin", "client"})
    public List<TenderApplicantsDto> updateRanking(@PathVariable @NotBlank String tenderId,
                                                   @RequestBody List<TenderApplicantsDto> tenderApplicantsDto) {
        return tenderApplicantsService.updateRanking(tenderId, tenderApplicantsDto);
    }

    @Operation(summary = "Compare tender applicants for a given tender")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success, when list of tender applicants are retrieved",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TenderApplicantsDashboardDto.class)))),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    @GetMapping("/{tenderId}/compare/{applicantId}")
    @RolesAllowed({"admin", "client"})
    public List<ApplicationFormDto> compareApplicants(@PathVariable @NotBlank String tenderId,
                                                      @PathVariable List<String> applicantId) {
        return tenderApplicantsService.compareApplicants(tenderId, applicantId);
    }

}
