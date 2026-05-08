package com.github.rspereiratech.openapi.collection.generator.core.schema.ref;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;

/**
 * Default implementation of {@link SchemaRefResolver} that resolves {@code $ref} pointers
 * by stripping the {@code #/components/schemas/} prefix and looking up the schema by name.
 */
public class DefaultSchemaRefResolver implements SchemaRefResolver {

    /**
     * JSON Pointer prefix used to locate schemas in the OpenAPI components section.
     */
    private static final String REF_PREFIX = "#/components/schemas/";

    @Override
    public Schema<?> resolve(Schema<?> schema, OpenAPI openApi) {
        if (schema == null || schema.get$ref() == null) {
            return schema;
        }

        Schema<?> r = resolveByName(schema.get$ref().replace(REF_PREFIX, ""), openApi);
        return r != null ? r : schema;
    }

    @Override
    public Schema<?> resolveByName(String name, OpenAPI openApi) {
        if (openApi.getComponents() == null || openApi.getComponents().getSchemas() == null) {
            return null;
        }

        return openApi.getComponents().getSchemas().get(name);
    }
}
