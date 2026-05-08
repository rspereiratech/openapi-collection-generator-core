package com.github.rspereiratech.openapi.collection.generator.core.example;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;

import java.util.Map;

/**
 * A {@link SchemaExampleGenerator} that produces example values for primitive-type schemas
 * (string, integer, number, boolean). Checks extensions, explicit examples, enums, and defaults
 * before falling back to sensible defaults. Delegates to the next generator for unrecognised types.
 */
public class PrimitiveSchemaExampleGenerator implements SchemaExampleGenerator {

    /**
     * Next generator in the chain, used for unrecognised schema types.
     */
    private final SchemaExampleGenerator next;

    /**
     * Creates a new primitive-schema example generator.
     *
     * @param next the next generator in the chain for non-primitive schemas; may be {@code null}
     */
    public PrimitiveSchemaExampleGenerator(SchemaExampleGenerator next) {
        this.next = next;
    }

    @Override
    public Object generate(Schema<?> schema, OpenAPI openApi) {
        if (schema == null) {
            return null;
        }

        Object fromExtension = extractFromExtensions(schema);
        if (fromExtension != null) {
            return fromExtension;
        }

        Object explicit = extractExplicitExample(schema);
        if (explicit != null) {
            return explicit;
        }

        return generateByType(schema, openApi);
    }

    /**
     * Attempts to extract an example from the {@code x-examples} vendor extension.
     *
     * @param schema the schema to inspect
     * @return the extracted example value, or {@code null} if not available
     */
    private Object extractFromExtensions(Schema<?> schema) {
        if (schema.getExtensions() == null) {
            return null;
        }

        Object x = schema.getExtensions().get("x-examples");
        if (!(x instanceof Map<?, ?> m) || m.isEmpty()) {
            return null;
        }

        Object first = m.values().iterator().next();
        if (first instanceof Map<?, ?> em) {
            Object v = em.get("value");
            if (v != null) {
                return v;
            }
        }
        return first;
    }

    /**
     * Returns the first available explicit example from the schema's example, enum, or default value.
     *
     * @param schema the schema to inspect
     * @return the explicit example, or {@code null} if none is defined
     */
    private Object extractExplicitExample(Schema<?> schema) {
        if (schema.getExample() != null) {
            return schema.getExample();
        }

        if (schema.getEnum() != null && !schema.getEnum().isEmpty()) {
            return schema.getEnum().get(0);
        }

        return schema.getDefault();
    }

    /**
     * Generates a default example based on the schema type.
     *
     * @param schema  the schema to generate an example for
     * @param openApi the OpenAPI specification
     * @return a sensible default value, or delegates to the next generator for unknown types
     */
    private Object generateByType(Schema<?> schema, OpenAPI openApi) {
        return switch (String.valueOf(schema.getType())) {
            case "string"  -> schema.getFormat() != null ? exampleForFormat(schema.getFormat()) : "string";
            case "integer" -> schema.getMinimum() != null ? schema.getMinimum().intValue() : 0;
            case "number"  -> schema.getMinimum() != null ? schema.getMinimum().doubleValue() : 0.0;
            case "boolean" -> true;
            default -> next != null ? next.generate(schema, openApi) : null;
        };
    }

    /**
     * Returns a representative example string for the given format specifier.
     *
     * @param fmt the format (e.g. {@code "date"}, {@code "uuid"}, {@code "email"})
     * @return an example string matching the format
     */
    private Object exampleForFormat(String fmt) {
        return switch (fmt) {
            case "date"      -> "2024-01-01";
            case "date-time" -> "2024-01-01T00:00:00Z";
            case "uuid"      -> "00000000-0000-0000-0000-000000000000";
            case "email"     -> "user@example.com";
            case "uri"       -> "https://example.com";
            case "byte"      -> "dGVzdA==";
            case "binary"    -> "<binary>";
            case "password"  -> "********";
            default          -> "string";
        };
    }
}
