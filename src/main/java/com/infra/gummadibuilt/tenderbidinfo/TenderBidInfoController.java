package com.infra.gummadibuilt.tenderbidinfo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.infra.gummadibuilt.common.file.FileDownloadDto;
import com.infra.gummadibuilt.tender.model.dto.TenderDetailsDto;
import io.swagger.v3.oas.annotations.Operation;
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

@RestController
@RequestMapping("/{tenderId}/tender-bid-info")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Tender Bid Info APIs",
        description = "APIs that allow qualified contractors to submit Financial & Technical bid info")
public class TenderBidInfoController {

    private final TenderBidInfoService tenderBidInfoService;

    @Autowired
    public TenderBidInfoController(TenderBidInfoService tenderBidInfoService) {
        this.tenderBidInfoService = tenderBidInfoService;
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success, when contractor is able to submit bid info",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TenderDetailsDto.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Error on validation",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    @RolesAllowed("contractor")
    @PostMapping
    public TenderDetailsDto createTenderBidInfo(@Valid HttpServletRequest request,
                                                @PathVariable("tenderId") @NotBlank String tenderId,
                                                @RequestPart("contractorDocument") @NotNull MultipartFile contractorDocument,
                                                @RequestPart("financialBid") @NotBlank String financialBid,
                                                @RequestPart("actionTaken") @NotBlank String actionTaken) throws JsonProcessingException {
        return tenderBidInfoService.createTenderBidInfo(request, tenderId, contractorDocument, financialBid, actionTaken);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success, when contractor is able to submit bid info",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TenderDetailsDto.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Error on validation",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    @RolesAllowed("contractor")
    @PutMapping("/update/{bidInfoId}")
    public TenderDetailsDto updateTenderBidInfo(@Valid HttpServletRequest request,
                                                @PathVariable("tenderId") @NotBlank String tenderId,
                                                @PathVariable("bidInfoId") @NotBlank String bidInfoId,
                                                @RequestPart("contractorDocument") MultipartFile contractorDocument,
                                                @RequestPart("financialBid") @NotBlank String financialBid,
                                                @RequestPart @NotBlank String actionTaken) throws JsonProcessingException {
        return tenderBidInfoService.updateTenderBidInfo(request, tenderId, bidInfoId, contractorDocument, financialBid, actionTaken);
    }

    @Operation(summary = "Download contractor technical tender response document")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success, when user is able to download tender response document",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = FileDownloadDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    @RolesAllowed({"client", "contractor", "admin"})
    @GetMapping("/download/{tenderId}/tender-response/{userId}")
    @Transactional(readOnly = true)
    public FileDownloadDto downloadTender(@PathVariable("tenderId") String tenderId,
                                          @PathVariable("userId") String userId,
                                          HttpServletRequest request) {
        return tenderBidInfoService.downloadTender(tenderId, userId, request);
    }

}
