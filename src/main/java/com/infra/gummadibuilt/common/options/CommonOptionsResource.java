package com.infra.gummadibuilt.common.options;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.infra.gummadibuilt.common.ApproveReject;
import com.infra.gummadibuilt.tender.model.DurationCounter;
import com.infra.gummadibuilt.tender.model.WorkflowStep;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This REST controller returns a list of all common enumerations throughout the
 * application, which would normally be replicated into the client source code.
 */
@RestController
@RequestMapping("/common-options")
public class CommonOptionsResource {
    // Pre-cached JSON document that contains all common enums
    private final String commonOptionsJson;

    @Autowired
    public CommonOptionsResource(ObjectMapper mapper) throws JsonProcessingException {
        this.commonOptionsJson = buildCommonOptions(mapper);
    }

    @Operation(summary = "Returns a list of all common enumerations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success & returns a list of all common enumerations",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "Invalid operation",
                    content = @Content)
    })
    @GetMapping(produces = "application/json; charset=UTF-8")
    public String getOptions() {
        return commonOptionsJson;
    }

    // Pre-build a string representation of the list of all common options
    private static String buildCommonOptions(ObjectMapper mapper) throws JsonProcessingException {

        ObjectNode result = mapper.createObjectNode();
        addOptions(ApproveReject.class, result.putArray("approveReject"));
        addOptions(DurationCounter.class, result.putArray("durationCounter"));
        addOptions(WorkflowStep.class, result.putArray("workflowStep"));
        return mapper.writeValueAsString(result);
    }

    // Adds all enumeration literals from the given enum class to the output array
    private static <T extends Enum<T> & CommonOptionsEnum> void addOptions(Class<T> enumClass, ArrayNode arrayOut) {

        for (T enumConstant : enumClass.getEnumConstants()) {
            ObjectNode obj = arrayOut.addObject();
            obj.put("id", enumConstant.name());
            obj.put("text", enumConstant.getText());
            enumConstant.getExtraProperties().forEach(obj::putPOJO);
        }

    }

}
