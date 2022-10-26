package com.infra.gummadibuilt.common.util;

import com.infra.gummadibuilt.common.exception.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CommonModuleUtils {

    /**
     * Item Discontinuous status Constants
     */
    public static final String COUNTRY_ID_NOT_FOUND = "Country with iso-code %s not found";
    public static final String STATE_ID_NOT_FOUND = "State with iso-code %s not found";
    public static final String CITY_ID_NOT_FOUND = "City with id %d not found";
    public static final String ROLE_NOT_FOUND = "Role with id %d not found";
    public static final String STATUS_DISC = "Discontinue";

    public static <T> T getById(JpaRepository<T, Integer> repository, int id, String exceptionMessage) {

        Optional<T> optionalValue = repository.findById(id);
        if (optionalValue.isPresent()) {
            return optionalValue.get();
        } else {
            throw new EntityNotFoundException(String.format(exceptionMessage, id));
        }
    }

    public static <T> T getById(JpaRepository<T, String> repository, String id, String exceptionMessage) {

        Optional<T> optionalValue = repository.findById(id);
        if (optionalValue.isPresent()) {
            return optionalValue.get();
        } else {
            throw new EntityNotFoundException(String.format(exceptionMessage, id));
        }
    }

    public static <T> T optionGetById(JpaRepository<T, String> repository, String id) {

        Optional<T> optionalValue = repository.findById(id);
        return optionalValue.orElse(null);
    }

}