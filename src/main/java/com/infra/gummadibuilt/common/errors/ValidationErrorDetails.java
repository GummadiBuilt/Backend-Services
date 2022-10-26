package com.infra.gummadibuilt.common.errors;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.infra.gummadibuilt.common.errors.field.FieldErrorDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * This error type is commonly associated with HTTP status code 400 (bad request) and
 * indicates details about the validation error that resulted in the bad request error.
 */
public class ValidationErrorDetails extends ErrorDetails {

    /**
     * Contains validation error messages that cannot be easily associated
     * with a specific part of the request.
     */
    private List<String> global = new ArrayList<>();

    /**
     * The keys of this map are property paths (i.e. "name" for the name property, or
     * "changeTracking.createdDate" for the createdDate field in the changeTracking
     * sub-object.
     */
    private Multimap<String, FieldErrorDetails> fields = ArrayListMultimap.create();

    public List<String> getGlobal() {
        return global;
    }

    public void setGlobal(List<String> global) {
        this.global = global;
    }

    public Multimap<String, FieldErrorDetails> getFields() {
        return fields;
    }

    public void setFields(Multimap<String, FieldErrorDetails> fields) {
        this.fields = fields;
    }

}
