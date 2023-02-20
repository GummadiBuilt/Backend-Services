package com.infra.gummadibuilt.admin.enquiries;

import com.infra.gummadibuilt.admin.enquiries.model.dto.EnquiryDto;
import com.infra.gummadibuilt.common.mail.MailService;
import com.infra.gummadibuilt.userandrole.ApplicationRoleDao;
import com.infra.gummadibuilt.userandrole.ApplicationUserDao;
import com.infra.gummadibuilt.userregistration.UserRegistrationService;
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
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.mail.MessagingException;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/enquiry")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "User Enquiry APIs",
        description = "APIs for contact-us module")
public class EnquiryController {

    private final EnquiryService enquiryService;

    public EnquiryController(EnquiryService enquiryService) {
        this.enquiryService = enquiryService;
    }

    @Operation(summary = "Enquiry about application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success, when query is saved to the database",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = EnquiryDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    @PostMapping
    public void enquireAboutAService(@Valid @RequestBody EnquiryDto enquiryDto) throws MessagingException, TemplateException, IOException {
        enquiryService.enquireAboutAService(enquiryDto);
    }

    @RolesAllowed("admin")
    @Operation(summary = "Get all enquiries")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success, when list of queries is returned",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = EnquiryDto.class)))
            ),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    @GetMapping
    public List<EnquiryDto> getListOfQueries() {
        return enquiryService.getListOfQueries();
    }
}
