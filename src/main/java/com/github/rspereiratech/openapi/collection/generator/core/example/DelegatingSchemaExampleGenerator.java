package com.github.rspereiratech.openapi.collection.generator.core.example;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;

/**
 * A {@link SchemaExampleGenerator} that forwards all calls to a configurable delegate.
 * Useful for breaking circular dependencies when building generator chains.
 */
public class DelegatingSchemaExampleGenerator implements SchemaExampleGenerator {

    /**
     * The actual generator that handles all calls, set after construction to break circular dependencies.
     */
    private SchemaExampleGenerator delegate;

    /**
     * Sets the delegate generator that will handle all subsequent calls.
     *
     * @param d the delegate generator
     */
    public void setDelegate(SchemaExampleGenerator d) {
        this.delegate = d;
    }

    @Override
    public Object generate(Schema<?> schema, OpenAPI openApi) {
        if (delegate == null) {
            throw new IllegalStateException("Delegate not configured");
        }

        return delegate.generate(schema, openApi);
    }
}
