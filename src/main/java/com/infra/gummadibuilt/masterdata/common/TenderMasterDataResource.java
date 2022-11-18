package com.infra.gummadibuilt.masterdata.common;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import java.util.Map;

@RestController
@RequestMapping("/tender-master-data")
@Tag(name = "Tender Creation Master Data APIs",
        description = "APIs that return master data needed for tender creation")
@SecurityRequirement(name = "bearerAuth")

public class TenderMasterDataResource {

    private final TenderMasterDataService tenderMasterDataService;

    @Autowired
    public TenderMasterDataResource(TenderMasterDataService tenderMasterDataService) {
        this.tenderMasterDataService = tenderMasterDataService;
    }

    @Operation(summary = "Get master data for tender-creation module")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success, when a valid response is sent with master data"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    @GetMapping
    @Transactional(readOnly = true)
    @RolesAllowed({"client", "contractor", "admin"})
    public Map<String, Object> getAll() {
        return tenderMasterDataService.getAll();
    }

}
