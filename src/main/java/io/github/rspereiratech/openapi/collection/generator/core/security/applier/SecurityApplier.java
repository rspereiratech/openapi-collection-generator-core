package io.github.rspereiratech.openapi.collection.generator.core.security.applier;

import io.github.rspereiratech.openapi.collection.generator.core.security.model.SecurityInjection;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;

/**
 * Applies security scheme resolution to individual operations or globally across
 * an entire {@link OpenAPI} document.
 */
public interface SecurityApplier {

    /**
     * Applies security for a single operation.
     *
     * @param operation the API operation
     * @param openApi   the full OpenAPI document
     * @return the {@link SecurityInjection} for the operation
     */
    SecurityInjection apply(Operation operation, OpenAPI openApi);

    /**
     * Collects deduplicated environment variables needed by all operations in the document.
     *
     * @param openApi the full OpenAPI document
     * @return a {@link SecurityInjection} containing only the unique environment variables
     */
    SecurityInjection applyGlobal(OpenAPI openApi);
}
