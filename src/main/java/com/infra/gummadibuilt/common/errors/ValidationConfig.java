package com.infra.gummadibuilt.common.errors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.CustomValidatorBean;

import javax.validation.metadata.ConstraintDescriptor;
import java.util.Arrays;

/**
 * Custom configuration for the validator used by Spring MVC since we want the original Hibernate validation
 * constraint results because they are much more detailed than Spring's.
 */
@Configuration
public class ValidationConfig {

    @Bean
    public ExtendedValidator customValidatorBean() {
        return new ExtendedValidator();
    }

    public static class ExtendedValidator extends CustomValidatorBean {

        @Override
        protected Object[] getArgumentsForConstraint(String objectName, String field, ConstraintDescriptor<?> descriptor) {
            Object[] args = super.getArgumentsForConstraint(objectName, field, descriptor);

            args = Arrays.copyOf(args, args.length + 1);
            args[args.length - 1] = descriptor;
            return args;
        }

    }

}
