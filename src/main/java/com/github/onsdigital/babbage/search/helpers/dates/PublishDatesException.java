package com.github.onsdigital.babbage.search.helpers.dates;

import com.github.onsdigital.babbage.error.ValidationError;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dave on 7/1/16.
 */
public class PublishDatesException extends Exception {

    private List<ValidationError> errors;

    public PublishDatesException(List<ValidationError> errors) {
        this.errors = errors == null ? new ArrayList<>() : errors;
    }

    public List<ValidationError> getErrors() {
        return errors;
    }
}
