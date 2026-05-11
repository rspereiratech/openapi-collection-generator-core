package io.github.rspereiratech.openapi.collection.generator.core.parser;

import java.io.File;

import io.swagger.v3.oas.models.OpenAPI;

/**
 * Contract for parsing an OpenAPI specification file into a Swagger {@link OpenAPI} model.
 */
public interface OpenApiParser {

    /**
     * Parses the given specification file and returns a fully resolved {@link OpenAPI} model.
     *
     * @param specFile the OpenAPI specification file (YAML or JSON)
     * @return the parsed {@link OpenAPI} model
     * @throws OpenApiParseException if the file cannot be parsed or contains validation errors
     */
    OpenAPI parse(File specFile) throws OpenApiParseException;
}
