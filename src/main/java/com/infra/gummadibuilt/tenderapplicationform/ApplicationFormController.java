package com.infra.gummadibuilt.tenderapplicationform;

import com.infra.gummadibuilt.tenderapplicationform.model.dto.ApplicationFormCreateDto;
import com.infra.gummadibuilt.tenderapplicationform.model.dto.ApplicationFormDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/tender/{tenderId}/apply")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Tender Applicant APIs",
        description = "APIs that allows contractors to apply tenders")
public class ApplicationFormController {

    private final ApplicationFormService applicationFormService;

    @Autowired
    public ApplicationFormController(ApplicationFormService applicationFormService) {
        this.applicationFormService = applicationFormService;
    }

    @RolesAllowed("contractor")
    private ApplicationFormDto applyForTender(@Valid HttpServletRequest request,
                                              @RequestBody ApplicationFormCreateDto createDto,
                                              @PathVariable("tenderId") String tenderId){
        return applicationFormService.apply(request, createDto, tenderId);
    }


}
