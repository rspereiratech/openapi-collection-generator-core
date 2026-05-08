package com.github.rspereiratech.openapi.collection.generator.core.schema.discriminator;

import java.util.List;
import java.util.Optional;

import com.github.rspereiratech.openapi.collection.generator.core.schema.ref.SchemaRefResolver;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Discriminator;
import io.swagger.v3.oas.models.media.Schema;

/**
 * Default implementation of {@link DiscriminatorResolver}. When a discriminator mapping is present,
 * the first mapped reference is resolved; otherwise the first candidate schema is returned.
 */
public class DefaultDiscriminatorResolver implements DiscriminatorResolver {

    /**
     * Resolver used to dereference {@code $ref} pointers in schemas.
     */
    private final SchemaRefResolver refResolver;

    /**
     * Creates a new resolver backed by the given {@link SchemaRefResolver}.
     *
     * @param refResolver the resolver used to dereference {@code $ref} pointers
     */
    public DefaultDiscriminatorResolver(SchemaRefResolver refResolver) {
        this.refResolver = refResolver;
    }

    @Override
    public Schema<?> resolve(List<Schema> schemas, Discriminator disc, OpenAPI openApi) {
        if (disc != null && disc.getMapping() != null && !disc.getMapping().isEmpty()) {
            String ref = disc.getMapping().values().iterator().next();

            Schema<?> r = refResolver.resolveByName(ref.replace("#/components/schemas/", ""), openApi);
            if (r != null) {
                return r;
            }
        }

        return Optional.ofNullable(schemas).filter(s -> !s.isEmpty())
                .map(s -> refResolver.resolve(s.get(0), openApi)).orElse(null);
    }
}
