package com.infra.gummadibuilt.userandrole;

import com.infra.gummadibuilt.userandrole.model.dto.UserDetailsUpdateDto;
import com.infra.gummadibuilt.userandrole.model.dto.UserDetailsViewDto;
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
import javax.validation.Valid;

@RestController
@RequestMapping("/user-profile")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "User Profile API",
        description = "APIs that allows logged in user to view and update their profiles")
public class UserProfileController {

    @Autowired
    public UserProfileService userProfileService;

    @Operation(summary = "Get logged in user profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success, when user profile information is retrieved",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDetailsViewDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    @GetMapping
    @RolesAllowed({"client", "contractor", "admin"})
    public UserDetailsViewDto getProfile(HttpServletRequest request) {
        return userProfileService.getProfile(request);
    }

    @Operation(summary = "Get user-profile by an id. Admin only function")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success, when user profile information is retrieved",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDetailsViewDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    @GetMapping("/{userId}")
    @RolesAllowed("admin")
    public UserDetailsViewDto getProfile(@PathVariable String userId) {
        return userProfileService.getUserProfile(userId);
    }

    @Operation(summary = "Update profile of logged in user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success, when user profile information is updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDetailsViewDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    @PutMapping("/update")
    @RolesAllowed({"client", "contractor", "admin"})
    public UserDetailsViewDto updateProfile(HttpServletRequest request,
                                            @Valid @RequestBody UserDetailsUpdateDto userDetails) {
        return userProfileService.updateProfile(request, userDetails);
    }
}
