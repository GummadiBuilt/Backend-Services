package com.infra.gummadibuilt.common.exception;

public class EntityFieldNotNullException extends RuntimeException {
	
	private static final long serialVersionUID = 4794432318129534423L;

    private final String propertyName;

    public EntityFieldNotNullException(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyName() {
        return propertyName;
    }

}
