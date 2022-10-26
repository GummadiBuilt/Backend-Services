package com.infra.gummadibuilt.common.options;

import java.util.Collections;
import java.util.Map;

/**
 * Implement this interface in enumerations that should be replicated to the client.
 * See {@link CommonOptionsResource} for the mechanism to make this happen. Each enumeration also needs
 * to be registered there.
 */
public interface CommonOptionsEnum {

    /**
     * @return The text that should be used to display this option in the client.
     */
    String getText();

    /**
     * Allows the common options enum to supply additional properties that will be replicated to the client.
     */
    default Map<String, Object> getExtraProperties() {
        return Collections.emptyMap();
    }
}
