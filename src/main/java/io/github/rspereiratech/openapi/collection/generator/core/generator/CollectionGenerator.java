package io.github.rspereiratech.openapi.collection.generator.core.generator;

import java.util.List;

import io.github.rspereiratech.openapi.collection.generator.core.config.GenerationConfig;
import io.swagger.v3.oas.models.OpenAPI;

/**
 * Contract for transforming a parsed {@link OpenAPI} model into a collection JSON string
 * and optional additional files (e.g. environment files).
 */
public interface CollectionGenerator {

    /**
     * Generates the main collection JSON from the given OpenAPI model and configuration.
     *
     * @param openApi the parsed OpenAPI model
     * @param config  plugin configuration
     * @return the collection as a JSON string
     * @throws CollectionGenerationException if generation fails
     */
    String generate(OpenAPI openApi, GenerationConfig config) throws CollectionGenerationException;

    /**
     * Generates any additional files (e.g. environment files) alongside the main collection.
     *
     * <p>The default implementation returns an empty list.</p>
     *
     * @param openApi the parsed OpenAPI model
     * @param config  plugin configuration
     * @return a list of additional files to write, possibly empty
     * @throws CollectionGenerationException if generation fails
     */
    default List<AdditionalFile> generateAdditionalFiles(OpenAPI openApi, GenerationConfig config)
            throws CollectionGenerationException {
        return List.of();
    }
}
