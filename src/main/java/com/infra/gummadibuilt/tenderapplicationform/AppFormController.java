package com.infra.gummadibuilt.tenderapplicationform;

import com.infra.gummadibuilt.tenderapplicationform.model.dto.ApplicationFormCreateDto;
import com.infra.gummadibuilt.tenderapplicationform.model.dto.ApplicationFormDto;
import io.swagger.v3.oas.annotations.Operation;
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

@RestController
@RequestMapping("/tender/{tenderId}/application")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Tender Application APIs",
        description = "APIs that allows contractors to apply tenders")
public class AppFormController {

    @Autowired
    private AppFormService appFormService;

    @GetMapping("/{applicationId}")
    @RolesAllowed({"contractor", "client", "admin"})
    public ApplicationFormDto get(HttpServletRequest request,
                                  @PathVariable("tenderId") String tenderId,
                                  @PathVariable("applicationId") String applicationId) {
        return appFormService.get(request, tenderId, Integer.parseInt(applicationId));
    }

    @Operation(summary = "Apply for a tender")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success, when user is able to apply for a tender",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApplicationFormDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    @PostMapping
    @RolesAllowed("contractor")
    public ApplicationFormDto applyTender(HttpServletRequest request,
                                          @RequestBody ApplicationFormCreateDto createDto,
                                          @PathVariable("tenderId") String tenderId) {

        return appFormService.applyTender(request, createDto, tenderId);
    }

    @PutMapping("/{applicationId}/update")
    @RolesAllowed("contractor")
    public ApplicationFormDto update(HttpServletRequest request,
                         @RequestBody ApplicationFormCreateDto createDto,
                         @PathVariable("tenderId") String tenderId,
                         @PathVariable("applicationId") String applicationId) {
        return appFormService.update(request,createDto,tenderId,applicationId);
    }
}
