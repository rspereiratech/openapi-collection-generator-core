package com.github.rspereiratech.openapi.collection.generator.core.extension;

import io.swagger.v3.oas.models.Operation;

/**
 * Immutable context passed to extension processors, carrying information about
 * the current OpenAPI operation being processed.
 *
 * @param path               the API path (e.g. {@code /pets/{id}})
 * @param httpMethod         the HTTP method (e.g. GET, POST)
 * @param currentName        the current display name of the operation
 * @param currentDescription the current description of the operation
 * @param operation          the OpenAPI {@link io.swagger.v3.oas.models.Operation} being processed
 */
public record ExtensionContext(String path, String httpMethod, String currentName, String currentDescription, Operation operation) {
}
