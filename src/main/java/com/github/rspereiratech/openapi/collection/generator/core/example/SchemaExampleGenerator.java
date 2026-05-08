package com.github.rspereiratech.openapi.collection.generator.core.example;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;

/**
 * Generates example values for OpenAPI schemas.
 */
public interface SchemaExampleGenerator {

    /**
     * Produces an example value that conforms to the given schema.
     *
     * @param schema  the OpenAPI schema to generate an example for
     * @param openApi the OpenAPI document used for reference resolution
     * @return an example value, or {@code null} if no example can be produced
     */
    Object generate(Schema<?> schema, OpenAPI openApi);
}
