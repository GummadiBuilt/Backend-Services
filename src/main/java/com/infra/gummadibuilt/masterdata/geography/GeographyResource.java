package com.infra.gummadibuilt.masterdata.geography;

import com.infra.gummadibuilt.masterdata.geography.model.dto.CityDto;
import com.infra.gummadibuilt.masterdata.geography.model.dto.StateDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/geography")
@Tag(name = "Geography Master Data APIs",
        description = "APIs that return master data of States & Zip Codes")
public class GeographyResource {

    private final GeographyService geographyService;

    @Autowired
    public GeographyResource(GeographyService geographyService) {
        this.geographyService = geographyService;
    }

    @Operation(summary = "Get states for a given country ISO2 code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success, when a valid response is sent with states information",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = StateDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    @GetMapping("/get-states")
    @Transactional(readOnly = true)
    public List<StateDto> getStates(@Parameter(description = "ISO2 code of the country")
                                    @Valid @RequestParam String countryCode) {
        return geographyService.getStatesByCountry(countryCode);
    }

    @Operation(summary = "Get cities for a given state ISO2 code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success, when a valid response is sent with city information",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CityDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    @GetMapping("/get-cities")
    @Transactional(readOnly = true)
    public List<CityDto> getCities(@Parameter(description = "ISO2 code of the state")
                                   @Valid @RequestParam String stateCode) {
        return geographyService.getCitiesByState(stateCode);
    }

}
