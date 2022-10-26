package com.infra.gummadibuilt.common.util;

import com.infra.gummadibuilt.common.exception.UniqueConstraintException;
import org.hibernate.exception.ConstraintViolationException;
import org.postgresql.util.PSQLException;
import org.postgresql.util.ServerErrorMessage;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class SaveEntityConstraintHelper {
    private SaveEntityConstraintHelper() {

    }

    /**
     * Saves the given entity in the given repository and flushes the insert statements. If a
     * DB constraint is violated, the name of the constraint that is violated is checked
     * against the given map of constraint names to property names.
     * If there's a matching entry in the map, a {@link UniqueConstraintException} is thrown
     * with the property name from the mapping.
     */
    public static <T> T save(JpaRepository<T, ?> repository,
                             T obj,
                             Map<String, Supplier<? extends RuntimeException>> constraintToPropertyMapping) {
        // The flush is mandatory to make Hibernate send the INSERT statement to
        // the database. This will trigger constraint checks and cause a constraint
        // violation exception which we can properly catch here
        try {
            return repository.saveAndFlush(obj);
        } catch (DataIntegrityViolationException e) {
            String constraintName = null;

            // Sometimes Hibernate finds the correct constraint name
            if (e.getCause() instanceof ConstraintViolationException) {
                ConstraintViolationException cve = (ConstraintViolationException) e.getCause();
                constraintName = cve.getConstraintName();
            }

            // In other cases, we have to go deeper
            if (constraintName == null) {
                Throwable cause = e.getMostSpecificCause();
                if (cause instanceof PSQLException) {
                    PSQLException psqlException = (PSQLException) cause;
                    ServerErrorMessage serverError = psqlException.getServerErrorMessage();
                    assert serverError != null;
                    constraintName = serverError.getConstraint();
                }
            }

            // Only if the server sent a constraint name can we check it against our
            // constraint name -> property mapping which tells us the property that
            // violated the constraint
            if (constraintName != null) {
                Supplier<? extends RuntimeException> supplier = constraintToPropertyMapping.get(constraintName);
                if (supplier != null) {
                    // Now throw our custom violation exception informing the user which field
                    // caused the duplicate entry
                    throw supplier.get();
                }
            }

            // When we reach this point, we didn't have a specific handler for the constraint violation
            // Pass it onto the generic exception handler
            throw e;
        }
    }

    public static <T> List<T> saveAll(JpaRepository<T, ?> repository, List<T> obj,
                                      Map<String, Supplier<? extends RuntimeException>> constraintToPropertyMapping) {
        // The flush is mandatory to make Hibernate send the INSERT statement to
        // the database. This will trigger constraint checks and cause a constraint
        // violation exception which we can properly catch here
        try {
            return repository.saveAll(obj);
        } catch (DataIntegrityViolationException e) {
            String constraintName = null;

            // Sometimes Hibernate finds the correct constraint name
            if (e.getCause() instanceof ConstraintViolationException) {
                ConstraintViolationException cve = (ConstraintViolationException) e.getCause();
                constraintName = cve.getConstraintName();
            }

            // In other cases, we have to go deeper
            if (constraintName == null) {
                Throwable cause = e.getMostSpecificCause();
                if (cause instanceof PSQLException) {
                    PSQLException psqlException = (PSQLException) cause;
                    ServerErrorMessage serverError = psqlException.getServerErrorMessage();
                    constraintName = serverError.getConstraint();
                }
            }

            // Only if the server sent a constraint name can we check it against our
            // constraint name -> property mapping which tells us the property that
            // violated the constraint
            if (constraintName != null) {
                Supplier<? extends RuntimeException> supplier = constraintToPropertyMapping.get(constraintName);
                if (supplier != null) {
                    // Now throw our custom violation exception informing the user which field
                    // caused the duplicate entry
                    throw supplier.get();
                }
            }

            // When we reach this point, we didn't have a specific handler for the
            // constraint violation
            // Pass it onto the generic exception handler
            throw e;
        }
    }


}
