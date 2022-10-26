package com.infra.gummadibuilt.masterdata.geography.model.dto;

import com.infra.gummadibuilt.masterdata.geography.model.State;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * A DTO for the {@link State} entity
 */
@Data
public class StateDto implements Serializable {

    @Schema(name = "stateIsoCode", description = "ISO2 code of the state", example = "DL")
    @NotBlank
    @Size(min = 1, max = 3)
    private String stateIsoCode;

    @Schema(name = "stateName", description = "Name of the state", example = "Delhi")
    @NotBlank
    @Size(min = 1, max = 150)
    private String stateName;

    public static StateDto valueOf(State state) {

        StateDto result = new StateDto();
        result.setStateName(state.getStateName());
        result.setStateIsoCode(state.getStateIsoCode());

        return result;

    }
}