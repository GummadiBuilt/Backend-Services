package com.infra.gummadibuilt.tenderbidinfo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.infra.gummadibuilt.tender.model.dto.TenderDetailsDto;
import com.infra.gummadibuilt.tenderapplicationform.model.dto.ActionTaken;
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
                                                @RequestPart("tenderDocument") @NotNull MultipartFile tenderDocument,
                                                @RequestPart("financialBid") @NotBlank String financialBid,
                                                @RequestPart("actionTaken") @NotNull String actionTaken) throws JsonProcessingException {
        return tenderBidInfoService.createTenderBidInfo(request, tenderId, tenderDocument, financialBid, actionTaken);
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
                                                @RequestPart("tenderDocument") @NotNull MultipartFile tenderDocument,
                                                @RequestPart("financialBid") @NotBlank String financialBid,
                                                @RequestPart @NotNull ActionTaken actionTaken) throws JsonProcessingException {
        return tenderBidInfoService.updateTenderBidInfo(request, tenderId, bidInfoId, tenderDocument, financialBid, actionTaken);
    }

}
