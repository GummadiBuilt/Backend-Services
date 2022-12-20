package com.infra.gummadibuilt.admin.contractor;

import com.infra.gummadibuilt.admin.client.dto.ClientUserInfoDto;
import com.infra.gummadibuilt.admin.contractor.dto.ContractorUserInfoDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@RestController
@RequestMapping("/contractor-users")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Contractor User Administration APIs",
        description = "APIs that get information about contractors & their metrics")
public class ContractorUserInfoController {

    private final ContractorUserInfoService contractorUserInfoService;

    public ContractorUserInfoController(ContractorUserInfoService contractorUserInfoService) {
        this.contractorUserInfoService = contractorUserInfoService;
    }

    @Operation(summary = "Get all contractor users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success, when it retrieves a list of contractors",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ContractorUserInfoDto.class)))),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    @RolesAllowed({"admin"})
    @GetMapping("/getAll")
    @Transactional(readOnly = true)
    public List<ContractorUserInfoDto> getAll() {
        return contractorUserInfoService.getAll();
    }
}