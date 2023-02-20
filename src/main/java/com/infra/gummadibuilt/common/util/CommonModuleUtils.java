package com.infra.gummadibuilt.common.util;

import com.infra.gummadibuilt.common.exception.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.DAYS;

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
    public static final String TENDER_NOT_FOUND = "Couldn't find a tender with id %s";
    public static final String PQ_FORM_NOT_FOUND = "Couldn't find a pq-form with id %d";
    public static final String USER_NOT_FOUND = "Couldn't find user with id %s";
    public static final String APPLICATION_FORM_NOT_FOUND = "Couldn't find application with id %d";
    public static final String APPLICANT_NOT_FOUND = "Couldn't find applicant with id %d";
    public static final String TENDER_BID_NOT_FOUND = "Couldn't find tender bid with id %d";

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

    public static long dayDiff(LocalDate lastDateOfSubmission) {
        LocalDate today = LocalDate.now();
        return DAYS.between(today, lastDateOfSubmission);
    }

    public static String[] excelHeaders() {
        return new String[]{
                "Company Name",
                "Year of establishment",
                "Type of establishment",
                "Postal Address[Corporate Office]",
                "Postal Address[Local Office]",
                "Telephone",
                "Fax",
                "Contact Person Name",
                "Designation",
                "Contact Phone Number",
                "Contact Email ID",
                "Regional Head/Project Coordinator",
                "Regional Head/Project Coordinator Mobile No",
                "Turnover Details",
                "Client Reference #1",
                "Client Reference #2",
                "Client Reference #3",
                "Similar Project #1",
                "Similar Project #2",
                "Similar Project #3",
                "ESI Registration",
                "EPF Registration",
                "GST Registration",
                "PAN Number",
                "Employee Strength",
                "Capital Equipments",
                "Safety Policy Manual",
                "PPE to Staff",
                "PPE to Work Men",
                "Safety Office Availability",
                "Financial Information",
                "Company Bankers",
                "Company Auditors"
        };
    }

    public static String[] clientReferenceHeaders() {
        return new String[]{
                "Name & Location of Project:",
                "Scope of Contract:",
                "Contract Value:",
                "Built Up Area:"
        };
    }

}