package com.infra.gummadibuilt.common.util;

import com.infra.gummadibuilt.common.exception.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CommonModuleUtils {

    public static final String DATE_PATTERN = "dd/MM/yyyy";

    /**
     * Item Discontinuous status Constants
     */
    public static final String COUNTRY_ID_NOT_FOUND = "Country with iso-code %s not found";
    public static final String STATE_ID_NOT_FOUND = "State with iso-code %s not found";
    public static final String CITY_ID_NOT_FOUND = "City with id %d not found";
    public static final String ROLE_NOT_FOUND = "Role with id %d not found";

    public static final String TYPE_OF_CONTRACT_NOT_FOUND = "Couldn't find a contract for the given id %d";
    public static final String TYPE_OF_WORK_NOT_FOUND = "Couldn't find a work that matches %s";
    public static final String TENDER_NOT_FOUND = "Couldn't find a tender with id %d";
    public static final String USER_NOT_FOUND = "Couldn't find user with id %s";

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