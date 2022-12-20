package com.infra.gummadibuilt.admin.client;

import com.infra.gummadibuilt.admin.client.dto.ClientUserInfoDto;
import com.infra.gummadibuilt.tender.model.dto.TenderDashboardProjection;
import com.infra.gummadibuilt.tender.model.dto.TenderDetailsDto;
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
@RequestMapping("/client-users")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Client User Administration APIs",
        description = "APIs that get information about clients & their metrics")
public class ClientUserInfoController {

    private final ClientUserInfoService clientUserInfoService;

    public ClientUserInfoController(ClientUserInfoService clientUserInfoService) {
        this.clientUserInfoService = clientUserInfoService;
    }

    @Operation(summary = "Get all client users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success, when it retrieves a list of client",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ClientUserInfoDto.class)))),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    @RolesAllowed({"admin"})
    @GetMapping("/getAll")
    @Transactional(readOnly = true)
    public List<ClientUserInfoDto> getAll() {
        return clientUserInfoService.getAll();
    }
}