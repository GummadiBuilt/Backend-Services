package com.infra.gummadibuilt.common.exception;

public class EntityFieldSizeLimitException extends RuntimeException {
	
	private static final long serialVersionUID = 4794415318129534423L;

	private final String propertyName;
	private final int minSize;
	private final int maxSize;

	public EntityFieldSizeLimitException(String propertyName, int minSize, int maxSize) {
		this.propertyName = propertyName;
		this.minSize = minSize;
		this.maxSize = maxSize;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public int getMinSize() {
		return minSize;
	}

	public int getMaxSize() {
		return maxSize;
	}
}
