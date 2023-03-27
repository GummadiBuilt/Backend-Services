package com.infra.gummadibuilt.tender;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.infra.gummadibuilt.common.file.FileDownloadDto;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/tender")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Tender Info APIs",
        description = "APIs that can CREATE, EDIT & VIEW tenders that are created")
public class TenderInfoController {

    private final TenderInfoService tenderInfoService;

    @Autowired
    public TenderInfoController(TenderInfoService tenderInfoService) {
        this.tenderInfoService = tenderInfoService;
    }

    @Operation(summary = "Create a new tender in the application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success, when user is onboarded to the application",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TenderDetailsDto.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Exception when email already in use",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    @RolesAllowed("client")
    @PostMapping
    public TenderDetailsDto createTender(@Valid HttpServletRequest request,
                                         @RequestPart("tenderDocument") @NotNull MultipartFile tenderDocument,
                                         @RequestPart("clientDocument") @NotNull List<MultipartFile> clientDocument,
                                         @RequestPart("tenderInfo") @NotBlank String tenderInfo) throws JsonProcessingException {
        return tenderInfoService.createTender(request, tenderDocument, clientDocument, tenderInfo);
    }

    @Operation(summary = "Create a new tender in the application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success, when user is onboarded to the application",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TenderDetailsDto.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Exception when email already in use",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    @RolesAllowed({"client", "admin"})
    @PutMapping("/update/{tenderId}")
    public TenderDetailsDto updateTender(@Valid HttpServletRequest request,
                                         @PathVariable("tenderId") @NotBlank String tenderId,
                                         @RequestPart("tenderDocument") MultipartFile tenderDocument,
                                         @RequestPart("clientDocument") @NotNull List<MultipartFile> clientDocument,
                                         @RequestPart("tenderInfo") @NotBlank String tenderInfo) throws JsonProcessingException {
        return tenderInfoService.updateTender(request, tenderId, tenderDocument, clientDocument, tenderInfo);
    }

    @Operation(summary = "Delete an uploaded document")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success, when user is onboarded to the application",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TenderDetailsDto.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Exception when email already in use",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    @RolesAllowed({"client"})
    @PutMapping("{tenderId}/delete-document/{documentId}")
    public TenderDetailsDto deleteDocument(@Valid @RequestPart("documentId") @NotBlank String documentId,
                                           @RequestPart("tenderId") @NotBlank String tenderId) {
        return tenderInfoService.deleteDocument(documentId, tenderId);
    }

    @Operation(summary = "Get all tenders")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success, when it retrieves a list of Tenders based on user-role",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TenderDashboardProjection.class)))),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    @RolesAllowed({"client", "contractor", "admin"})
    @GetMapping("/getAll")
    @Transactional(readOnly = true)
    public List<TenderDashboardProjection> getAll(HttpServletRequest request) {
        return tenderInfoService.getAllTenders(request);
    }

    @Operation(summary = "Get applied tenders")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success, when applied tenders are retrieved",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TenderDashboardProjection.class)))),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    @RolesAllowed("contractor")
    @GetMapping("/applied-tenders")
    @Transactional(readOnly = true)
    public List<TenderDashboardProjection> getMyTenders(HttpServletRequest request) {
        return tenderInfoService.getMyTenders(request);
    }


    @Operation(summary = "Get tender by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success, when it returns tender information for the given tender-id",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = TenderDetailsDto.class))
                    }
            ),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    @RolesAllowed({"client", "contractor", "admin"})
    @GetMapping("/get/{tenderId}")
    @Transactional(readOnly = true)
    public TenderDetailsDto get(HttpServletRequest request, @PathVariable("tenderId") @NotBlank String tenderId) {
        return tenderInfoService.getTenderInfo(request, tenderId);
    }

    @Operation(summary = "Download technical tender document")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success, when user is able to download tender document",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = FileDownloadDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    @RolesAllowed({"client", "contractor", "admin"})
    @GetMapping("/download/{tenderId}/id/{documentId}/documentType/{documentType}")
    @Transactional(readOnly = true)
    public FileDownloadDto downloadTender(HttpServletRequest request,
                                          @PathVariable("tenderId") String tenderId,
                                          @PathVariable("documentId") String documentId,
                                          @PathVariable("documentType") String documentType) {
        return tenderInfoService.downloadTender(request, tenderId, documentId, documentType);
    }
}
