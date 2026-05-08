package com.github.rspereiratech.openapi.collection.generator.core.schema.discriminator;

import java.util.List;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Discriminator;
import io.swagger.v3.oas.models.media.Schema;

/**
 * Selects a concrete schema from a list of candidates using an optional OpenAPI {@link Discriminator}.
 */
public interface DiscriminatorResolver {

    /**
     * Resolves a single schema from the given candidates, optionally guided by a discriminator mapping.
     *
     * @param schemas       the candidate schemas (e.g. from {@code oneOf} or {@code anyOf})
     * @param discriminator the discriminator to guide selection; may be {@code null}
     * @param openApi       the OpenAPI document for reference resolution
     * @return the selected schema, or {@code null} if no suitable schema is found
     */
    Schema<?> resolve(List<Schema> schemas, Discriminator discriminator, OpenAPI openApi);
}
