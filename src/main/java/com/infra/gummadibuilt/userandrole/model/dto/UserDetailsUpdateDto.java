package com.infra.gummadibuilt.userandrole.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserDetailsUpdateDto extends UserDetailsDto {

    @NotBlank
    @Size(min = 1, max = 3)
    private String countryIsoCode;

    @NotBlank
    @Size(min = 1, max = 3)
    private String stateIsoCode;

    @NotNull
    private int cityId;
}
