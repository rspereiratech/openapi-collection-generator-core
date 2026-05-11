package io.github.rspereiratech.openapi.collection.generator.core.security.model;

/**
 * Represents an HTTP query parameter as a name-value pair to be appended to a request URL.
 *
 * @param name  the query parameter name
 * @param value the query parameter value
 */
public record HttpQueryParam(String name, String value) {}
