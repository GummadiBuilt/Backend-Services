package com.infra.gummadibuilt.tenderapplicationform;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.infra.gummadibuilt.common.file.FileDownloadDto;
import com.infra.gummadibuilt.tenderapplicationform.model.dto.ApplicationFormCreateDto;
import com.infra.gummadibuilt.tenderapplicationform.model.dto.ApplicationFormDto;
import com.infra.gummadibuilt.tenderapplicationform.model.dto.DocumentType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/tender/{tenderId}/application")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Tender Application APIs",
        description = "APIs that allows contractors to apply tenders")
public class AppFormController {

    @Autowired
    private AppFormService appFormService;

    @Operation(summary = "Get an applicant form by tender id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Success, when application of an applicant is retrieved",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationFormDto.class))
                    }
            ),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    @GetMapping("/{applicationId}")
    @RolesAllowed({"contractor", "admin"})
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
                                          @PathVariable("tenderId") String tenderId) throws JsonProcessingException {

        return appFormService.applyTender(request, tenderId);
    }

    @Operation(summary = "Update tender application form")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success, when user is able to update their application form",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApplicationFormDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    @PutMapping("/{applicationId}/update")
    @RolesAllowed("contractor")
    public ApplicationFormDto update(HttpServletRequest request,
                                     @RequestBody ApplicationFormCreateDto createDto,
                                     @PathVariable("tenderId") String tenderId,
                                     @PathVariable("applicationId") String applicationId) {
        return appFormService.update(request, createDto, tenderId, applicationId);
    }

    @Operation(summary = "Upload files in Application form")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success, when given file is uploaded",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApplicationFormDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    @PutMapping("/{applicationId}/upload/{documentType}")
    @RolesAllowed("contractor")
    public ApplicationFormDto uploadDocument(HttpServletRequest request,
                                             @RequestPart("document") @NotNull MultipartFile fileToUpload,
                                             @PathVariable("tenderId") String tenderId,
                                             @PathVariable("documentType") DocumentType documentType,
                                             @PathVariable("applicationId") String applicationId) {
        return appFormService.uploadDocument(request, fileToUpload, tenderId, documentType, applicationId);
    }

    @Operation(summary = "Download file for the given application id & financial year")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success, when given file is downloaded",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = FileDownloadDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    @GetMapping("/{applicationId}/download/{documentType}")
    @RolesAllowed("contractor")
    public FileDownloadDto downloadDocument(HttpServletRequest request,
                                            @PathVariable("tenderId") String tenderId,
                                            @PathVariable("fileYear") DocumentType documentType,
                                            @PathVariable("applicationId") String applicationId) {
        return appFormService.downloadDocument(request, tenderId, documentType, applicationId);
    }
}
