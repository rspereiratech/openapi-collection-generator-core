package io.github.rspereiratech.openapi.collection.generator.core.link;

import io.swagger.v3.oas.models.links.Link;

import java.util.Map;

/**
 * Enriches an operation description by appending information about related
 * OpenAPI link objects.
 */
public interface LinkDescriptionEnricher {

    /**
     * Appends link information to the given description.
     *
     * @param description the current operation description
     * @param links       the links map from an OpenAPI response; may be {@code null}
     * @return the enriched description, or the original if no links are present
     */
    String enrich(String description, Map<String, Link> links);
}
