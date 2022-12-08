package com.infra.gummadibuilt.pqform;

import com.infra.gummadibuilt.pqform.model.dto.PqFormHeaderCreateDto;
import com.infra.gummadibuilt.pqform.model.dto.PqFormHeaderDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@RestController
@RequestMapping("/tender/{tenderId}/pq-form")
public class PqFormHeaderController {

    private final PqFormHeaderService pqFormHeaderService;

    @Autowired
    public PqFormHeaderController(PqFormHeaderService pqFormHeaderService) {
        this.pqFormHeaderService = pqFormHeaderService;
    }

    @Operation(summary = "Get pq form by tender id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success, when it returns pq form information for the given tender-id",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PqFormHeaderDto.class))
                    }
            ),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    @RolesAllowed({"client", "contractor", "admin"})
    @GetMapping
    @Transactional(readOnly = true)
    public PqFormHeaderDto get(HttpServletRequest request, @PathVariable("tenderId") @NotBlank String tenderId) {
        return pqFormHeaderService.get(tenderId, request);
    }

    @Operation(summary = "Create PQ form in the application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success, when PQ form is created for given tender",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PqFormHeaderDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    @RolesAllowed("admin")
    @PostMapping
    public PqFormHeaderDto createPqForm(@Valid HttpServletRequest request,
                                        @PathVariable("tenderId") @NotBlank String tenderId,
                                        @RequestBody PqFormHeaderCreateDto pqForm) {
        return pqFormHeaderService.createPqForm(request, tenderId, pqForm);
    }
}
