package com.infra.gummadibuilt.userregistration;

import com.infra.gummadibuilt.userregistration.model.dto.ApproveRejectDto;
import com.infra.gummadibuilt.userregistration.model.dto.RegistrationInfoDto;
import com.infra.gummadibuilt.userregistration.model.dto.UserRegistrationDto;
import freemarker.template.TemplateException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@RequestMapping("/user-registration")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "User Registration APIs",
        description = "APIs for User-Registration module, includes approve/reject actions")
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
            @ApiResponse(responseCode = "400", description = "Exception when email already in use",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    @PostMapping
    public UserRegistrationDto registerUser(@Valid @RequestBody UserRegistrationDto userRegistrationDto) throws MessagingException, TemplateException, IOException {
        return userRegistrationService.registerUser(userRegistrationDto);
    }

    @RolesAllowed("admin")
    @Operation(summary = "Get applications pending for approval")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success, when pending list of users is returned",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = RegistrationInfoDto.class)))
            ),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    @GetMapping
    public List<RegistrationInfoDto> getPendingForApproval() {
        return userRegistrationService.getPendingForApproval();
    }

    @RolesAllowed("admin")
    @Operation(summary = "Approve/Reject a pending user access request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success, when action taken is succeeded",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = RegistrationInfoDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    @PostMapping("/approve-reject")
    public List<RegistrationInfoDto> approveOrRejectRequests(@Valid HttpServletRequest request, @RequestBody ApproveRejectDto approveRejectDto) throws UnsupportedEncodingException {
        return userRegistrationService.approveOrRejectRequests(request, approveRejectDto);
    }

    @RolesAllowed("admin")
    @Operation(summary = "Audit approval/reject history on user-registrations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success, when an audit list of users with actions taken is returned",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = RegistrationInfoDto.class)))
            ),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    @GetMapping("/audit-info")
    public List<RegistrationInfoDto> auditApproveRejectRequests() {
        return userRegistrationService.auditApproveRejectRequests();
    }

}
