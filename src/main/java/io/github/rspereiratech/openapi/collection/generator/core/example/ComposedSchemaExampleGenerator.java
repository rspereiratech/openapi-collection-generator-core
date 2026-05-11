package io.github.rspereiratech.openapi.collection.generator.core.example;

import io.github.rspereiratech.openapi.collection.generator.core.schema.discriminator.DiscriminatorResolver;
import io.github.rspereiratech.openapi.collection.generator.core.schema.ref.SchemaRefResolver;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A {@link SchemaExampleGenerator} that handles composed schemas ({@code allOf}, {@code oneOf}, {@code anyOf}).
 * Delegates to the next generator in the chain for non-composed schemas.
 */
public class ComposedSchemaExampleGenerator implements SchemaExampleGenerator {

    /**
     * Next generator in the chain, used for non-composed schemas.
     */
    private final SchemaExampleGenerator next;

    /**
     * Resolver for dereferencing {@code $ref} pointers in schemas.
     */
    private final SchemaRefResolver refResolver;

    /**
     * Resolver for selecting a schema variant based on discriminator mappings.
     */
    private final DiscriminatorResolver discriminatorResolver;

    /**
     * Generator used to recursively produce examples for sub-schemas.
     */
    private final SchemaExampleGenerator recursive;

    /**
     * Creates a new composed-schema example generator.
     *
     * @param next      the next generator in the chain for non-composed schemas
     * @param ref       the resolver for {@code $ref} pointers
     * @param disc      the resolver for discriminator-based schema selection
     * @param recursive the generator used to recursively produce examples for sub-schemas
     */
    public ComposedSchemaExampleGenerator(SchemaExampleGenerator next, SchemaRefResolver ref,
                                          DiscriminatorResolver disc, SchemaExampleGenerator recursive) {
        this.next = next;
        this.refResolver = ref;
        this.discriminatorResolver = disc;
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

        if (resolved.getAllOf() != null && !resolved.getAllOf().isEmpty()) {
            return generateAllOf(resolved, openApi);
        }

        if (resolved.getOneOf() != null && !resolved.getOneOf().isEmpty()) {
            return generateOneOrAny(resolved.getOneOf(), resolved.getDiscriminator(), openApi);
        }

        if (resolved.getAnyOf() != null && !resolved.getAnyOf().isEmpty()) {
            return generateOneOrAny(resolved.getAnyOf(), null, openApi);
        }

        return next != null ? next.generate(resolved, openApi) : null;
    }

    /**
     * Merges examples from all sub-schemas in an {@code allOf} composition into a single map.
     *
     * @param schema  the composed schema containing {@code allOf} entries
     * @param openApi the OpenAPI document
     * @return a merged map of property examples, or {@code null} if the result is empty
     */
    @SuppressWarnings("unchecked")
    private Object generateAllOf(Schema<?> schema, OpenAPI openApi) {
        Map<String, Object> merged = new LinkedHashMap<>();
        for (Schema<?> sub : schema.getAllOf()) {
            Schema<?> r = refResolver.resolve(sub, openApi);
            if (r == null) {
                r = sub;
            }

            Object ex = recursive.generate(r, openApi);
            if (ex instanceof Map<?,?> m) {
                merged.putAll((Map<String, Object>) m);
            }
        }

        if (schema.getProperties() != null) {
            schema.getProperties().forEach(
                    (k, v) -> merged.put(k, recursive.generate(v, openApi)));
        }

        return merged.isEmpty() ? null : merged;
    }

    /**
     * Generates an example for a {@code oneOf} or {@code anyOf} composition by selecting one schema.
     *
     * @param schemas the candidate schemas
     * @param disc    the discriminator; may be {@code null}
     * @param openApi the OpenAPI document
     * @return an example for the chosen schema, or {@code null} if none is selected
     */
    private Object generateOneOrAny(List<Schema> schemas, io.swagger.v3.oas.models.media.Discriminator disc,
                                     OpenAPI openApi) {
        Schema<?> chosen = discriminatorResolver.resolve(schemas, disc, openApi);
        return chosen != null ? recursive.generate(chosen, openApi) : null;
    }
}
