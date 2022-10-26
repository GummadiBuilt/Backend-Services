package com.infra.gummadibuilt.userregistration;

import com.infra.gummadibuilt.masterdata.geography.model.dto.StateDto;
import com.infra.gummadibuilt.userregistration.model.UserRegistrationDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/user-registration")
public class UserRegistrationController {

    private final UserRegistrationService userRegistrationService;

    @Autowired
    public UserRegistrationController(UserRegistrationService userRegistrationService) {
        this.userRegistrationService = userRegistrationService;
    }

    @Operation(summary = "Onboard new users to the application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success, when user is onboarded to the application",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserRegistrationDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    @PostMapping
    public UserRegistrationDto registerUser(@Parameter(description = "ISO2 code of the country")
                               @Valid @RequestBody UserRegistrationDto userRegistrationDto) {
        return userRegistrationService.registerUser(userRegistrationDto);
    }

}
