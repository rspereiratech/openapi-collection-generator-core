package com.github.rspereiratech.openapi.collection.generator.core.security.resolver;

import com.github.rspereiratech.openapi.collection.generator.core.security.model.SecurityInjection;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;

/**
 * Resolves security requirements declared on an {@link Operation} (or globally on the
 * {@link OpenAPI} document) into a {@link SecurityInjection}.
 */
public interface SecuritySchemeResolver {

    /**
     * Resolves security requirements for an operation and returns the combined injection.
     *
     * @param operation the API operation whose security requirements are resolved
     * @param openApi   the full OpenAPI document containing scheme definitions
     * @return the resulting {@link SecurityInjection}
     */
    SecurityInjection resolve(Operation operation, OpenAPI openApi);
}
