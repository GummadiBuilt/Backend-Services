package com.infra.gummadibuilt.common.errors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

/**
 * Configures all controllers to make use of the extended validator that stores the original Hibernate
 * constraint descriptor in the message arguments (see {@link ValidationConfig}.
 */
@ControllerAdvice
public class ValidationAdvice {

    private final ValidationConfig.ExtendedValidator extendedValidator;

    @Autowired
    public ValidationAdvice(ValidationConfig.ExtendedValidator extendedValidator) {
        this.extendedValidator = extendedValidator;
    }

    @InitBinder
    public void customizeBinder(WebDataBinder binder) {
        binder.replaceValidators(extendedValidator);
    }

}
