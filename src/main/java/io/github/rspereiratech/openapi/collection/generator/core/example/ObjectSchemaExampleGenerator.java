package io.github.rspereiratech.openapi.collection.generator.core.example;

import io.github.rspereiratech.openapi.collection.generator.core.schema.ref.SchemaRefResolver;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A {@link SchemaExampleGenerator} that handles object-type schemas by generating a map
 * of property names to their example values. Delegates to the next generator for non-object schemas.
 */
public class ObjectSchemaExampleGenerator implements SchemaExampleGenerator {

    /**
     * Next generator in the chain, used for non-object schemas.
     */
    private final SchemaExampleGenerator next;

    /**
     * Resolver for dereferencing {@code $ref} pointers in schemas.
     */
    private final SchemaRefResolver refResolver;

    /**
     * Generator used to recursively produce examples for property schemas.
     */
    private final SchemaExampleGenerator recursive;

    /**
     * Creates a new object-schema example generator.
     *
     * @param next      the next generator in the chain for non-object schemas
     * @param ref       the resolver for {@code $ref} pointers
     * @param recursive the generator used to recursively produce examples for property schemas
     */
    public ObjectSchemaExampleGenerator(SchemaExampleGenerator next, SchemaRefResolver ref,
                                         SchemaExampleGenerator recursive) {
        this.next = next;
        this.refResolver = ref;
        this.recursive = recursive;
    }

    @Override
    public Object generate(Schema<?> schema, OpenAPI openApi) {
        if (schema == null) {
            return null;
        }

        Schema<?> resolved = refResolver.resolve(schema, openApi);
        if (resolved == null) {
            resolved = schema;
        }

        if (!"object".equals(resolved.getType()) || resolved.getProperties() == null) {
            return next != null ? next.generate(resolved, openApi) : null;
        }

        Map<String, Object> obj = new LinkedHashMap<>();
        resolved.getProperties().forEach(
                (k, v) -> obj.put(k, recursive.generate(v, openApi)));

        return obj;
    }
}
