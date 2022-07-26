package com.infra.gummadibuilt.masterdata.geography.model.dto;

import com.infra.gummadibuilt.masterdata.geography.model.City;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * A DTO for the {@link City} entity
 */
@Data
public class CityDto implements Serializable {

    @Schema(name = "id", description = "Sequence number generated by database", example = "1")
    @NotBlank
    private int id;

    @Schema(name = "cityName", description = "Name of the city", example = "New Delhi")
    @NotBlank
    @Size(max = 100)
    private String cityName;

    public static CityDto valueOf(City city) {

        CityDto result = new CityDto();
        result.setId(city.getId());
        result.setCityName(city.getCityName());

        return result;
    }
}