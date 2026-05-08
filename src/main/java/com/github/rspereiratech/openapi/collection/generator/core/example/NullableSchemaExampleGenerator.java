package com.github.rspereiratech.openapi.collection.generator.core.example;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;

/**
 * A {@link SchemaExampleGenerator} that delegates to the next generator in the chain.
 * Acts as a pass-through handler for nullable schemas.
 */
public class NullableSchemaExampleGenerator implements SchemaExampleGenerator {

    /**
     * Next generator in the chain, invoked for non-null schemas.
     */
    private final SchemaExampleGenerator next;

    /**
     * Creates a new generator with the given next handler.
     *
     * @param next the next generator in the chain; may be {@code null}
     */
    public NullableSchemaExampleGenerator(SchemaExampleGenerator next) {
        this.next = next;
    }

    @Override
    public Object generate(Schema<?> schema, OpenAPI openApi) {
        return next != null ? next.generate(schema, openApi) : null;
    }
}
