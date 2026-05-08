package com.github.rspereiratech.openapi.collection.generator.core.server;

import io.swagger.v3.oas.models.OpenAPI;

import java.util.List;

/**
 * Generates {@link ServerEnvironment} instances from the servers defined in an OpenAPI specification.
 */
public interface ServerEnvironmentGenerator {

    /**
     * Generates server environments from the given OpenAPI definition.
     *
     * @param openApi        the parsed OpenAPI specification
     * @param collectionName the name of the collection (used for file naming)
     * @return a list of server environments; never {@code null}
     */
    List<ServerEnvironment> generate(OpenAPI openApi, String collectionName);
}
