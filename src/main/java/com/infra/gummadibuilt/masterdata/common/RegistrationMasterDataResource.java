package com.infra.gummadibuilt.masterdata.common;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/registration-master-data")
@Tag(name = "User Registration Master Data APIs",
        description = "APIs that return master data needed for user registration")

public class RegistrationMasterDataResource {


    private final RegistrationMasterDataService registrationMasterDataService;

    @Autowired
    public RegistrationMasterDataResource(RegistrationMasterDataService registrationMasterDataService) {
        this.registrationMasterDataService = registrationMasterDataService;
    }

    @Operation(summary = "Get master data for user registration module")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success, when a valid response is sent with master data"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    @GetMapping
    @Transactional(readOnly = true)
    public Map<String, Object> getAll() {
        return registrationMasterDataService.getAll();
    }

}
