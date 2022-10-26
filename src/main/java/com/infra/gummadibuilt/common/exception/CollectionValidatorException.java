package com.infra.gummadibuilt.common.exception;

import javax.validation.ConstraintViolation;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class CollectionValidatorException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private List<Set<? extends ConstraintViolation<?>>> constraintViolationsList = new LinkedList<>();

	public CollectionValidatorException(List<Set<? extends ConstraintViolation<?>>> constraintViolationsList) {
		this.constraintViolationsList=constraintViolationsList;
		}

	public List<Set<? extends ConstraintViolation<?>>> getConstraintViolationsList() {
		return constraintViolationsList;
	}
}
