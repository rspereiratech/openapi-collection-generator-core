package io.github.rspereiratech.openapi.collection.generator.core.schema.ref;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;

/**
 * Resolves {@code $ref} references within OpenAPI schemas to their target schema definitions.
 */
public interface SchemaRefResolver {

    /**
     * Resolves a schema that may contain a {@code $ref} pointer to its referenced schema definition.
     *
     * @param schema  the schema to resolve; may be {@code null} or contain a {@code $ref}
     * @param openApi the OpenAPI document used to look up component schemas
     * @return the resolved schema, or the original schema if no reference is present or resolution fails
     */
    Schema<?> resolve(Schema<?> schema, OpenAPI openApi);

    /**
     * Looks up a schema by its component name in the OpenAPI document.
     *
     * @param name    the schema component name
     * @param openApi the OpenAPI document containing the component definitions
     * @return the matching schema, or {@code null} if not found
     */
    Schema<?> resolveByName(String name, OpenAPI openApi);
}
