package io.github.rspereiratech.openapi.collection.generator.core.security.model;

/**
 * Represents an HTTP header as a name-value pair to be injected into a request.
 *
 * @param name  the header name
 * @param value the header value
 */
public record HttpHeader(String name, String value) {}
