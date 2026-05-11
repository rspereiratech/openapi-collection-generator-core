package io.github.rspereiratech.openapi.collection.generator.core.security.model;

/**
 * Represents an environment variable used as a placeholder in a generated collection.
 *
 * @param name        the variable name
 * @param placeholder the default placeholder value shown to the user
 */
public record EnvironmentVariable(String name, String placeholder) {}
