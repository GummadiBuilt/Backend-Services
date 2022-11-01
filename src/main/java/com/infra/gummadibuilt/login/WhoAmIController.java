package com.infra.gummadibuilt.login;

import com.infra.gummadibuilt.login.dto.WhoAmIDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/who-am-i")
@Tag(name = "User Login APIs",
        description = "APIs for user login and get roles for the logged in user")
public class WhoAmIController {

    private final WhoAmIService whoAmIService;

    @Autowired
    public WhoAmIController(WhoAmIService whoAmIService) {
        this.whoAmIService = whoAmIService;
    }

    @Operation(summary = "User Login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success, when pending list of users is returned",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = WhoAmIDto.class))
                    }
            ),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    @GetMapping
    public WhoAmIDto getUserDetails(HttpServletRequest request) {
        return whoAmIService.getUserDetails(request);
    }

    @RolesAllowed("admin")
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public ResponseEntity<String> getUser() {
        return ResponseEntity.ok("Hello User");
    }

    @RolesAllowed("client")
    @RequestMapping(value = "/client", method = RequestMethod.GET)
    public ResponseEntity<String> getClient() {
        return ResponseEntity.ok("Hello Client");
    }


}
