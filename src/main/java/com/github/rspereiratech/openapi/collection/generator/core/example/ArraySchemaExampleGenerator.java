package com.github.rspereiratech.openapi.collection.generator.core.example;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;

import java.util.List;

/**
 * A {@link SchemaExampleGenerator} that handles array-type schemas by producing a single-element list
 * with an example generated from the array's item schema. Delegates to the next generator for non-array schemas.
 */
public class ArraySchemaExampleGenerator implements SchemaExampleGenerator {

    /**
     * Next generator in the chain, used for non-array schemas.
     */
    private final SchemaExampleGenerator next;

    /**
     * Generator used to produce an example for the array's item schema.
     */
    private final SchemaExampleGenerator recursive;

    /**
     * Creates a new array-schema example generator.
     *
     * @param next      the next generator in the chain for non-array schemas
     * @param recursive the generator used to produce an example for the array's item schema
     */
    public ArraySchemaExampleGenerator(SchemaExampleGenerator next, SchemaExampleGenerator recursive) {
        this.next = next;
        this.recursive = recursive;
    }

    @Override
    public Object generate(Schema<?> schema, OpenAPI openApi) {
        if (!(schema instanceof ArraySchema arr)) {
            return next != null ? next.generate(schema, openApi) : null;
        }

        Object item = arr.getItems() != null ? recursive.generate(arr.getItems(), openApi) : new Object();
        return List.of(item);
    }
}
