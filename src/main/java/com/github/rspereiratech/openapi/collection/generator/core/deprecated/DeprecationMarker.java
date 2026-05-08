package com.github.rspereiratech.openapi.collection.generator.core.deprecated;

/**
 * Strategy for marking operation names and descriptions as deprecated.
 */
public interface DeprecationMarker {

    /**
     * Marks the given name to indicate deprecation status.
     *
     * @param name       the original operation name
     * @param deprecated whether the operation is deprecated
     * @return the name, possibly decorated with a deprecation indicator
     */
    String markName(String name, boolean deprecated);

    /**
     * Marks the given description to indicate deprecation status.
     *
     * @param description the original operation description
     * @param deprecated  whether the operation is deprecated
     * @return the description, possibly prefixed with a deprecation warning
     */
    String markDescription(String description, boolean deprecated);
}
