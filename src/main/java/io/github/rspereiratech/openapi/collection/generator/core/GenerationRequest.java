package io.github.rspereiratech.openapi.collection.generator.core;

import io.github.rspereiratech.openapi.collection.generator.core.config.GenerationConfig;
import io.github.rspereiratech.openapi.collection.generator.core.model.CollectionFormat;

import java.io.File;
import java.util.List;

/**
 * Input configuration for collection generation, supporting multiple output formats.
 *
 * @param specFile        path to the OpenAPI specification file
 * @param outputDirectory directory where generated files are written
 * @param formats         list of target collection formats
 * @param collectionName  optional human-readable name for the generated collection
 * @param fileNamePattern pattern for the output file name, supports {@code {name}} and {@code {format}} placeholders
 */
public record GenerationRequest(File specFile, File outputDirectory, List<CollectionFormat> formats,
                                String collectionName, String fileNamePattern) {

    /**
     * Validates the request parameters.
     *
     * @throws IllegalArgumentException if no formats are specified or if the file name pattern
     *                                  is missing the {@code {format}} placeholder when multiple formats are configured
     */
    public GenerationRequest {
        if (formats == null || formats.isEmpty()) {
            throw new IllegalArgumentException("At least one format must be specified");
        }
        if (formats.size() > 1 && !fileNamePattern.contains("{format}")) {
            throw new IllegalArgumentException(
                    "fileNamePattern must contain {format} when multiple formats are configured, "
                    + "otherwise output files will overwrite each other");
        }
    }

    /**
     * Creates a new request using the default file name pattern.
     *
     * @param specFile        path to the OpenAPI specification file
     * @param outputDirectory directory where generated files are written
     * @param formats         list of target collection formats
     * @param collectionName  optional human-readable name for the generated collection
     */
    public GenerationRequest(File specFile, File outputDirectory, List<CollectionFormat> formats,
                             String collectionName) {
        this(specFile, outputDirectory, formats, collectionName, GenerationConfig.DEFAULT_FILE_NAME_PATTERN);
    }

    /**
     * Creates a {@link GenerationConfig} for a specific format from this request.
     */
    public GenerationConfig toGenerationConfig(CollectionFormat format) {
        return new GenerationConfig(outputDirectory, format, collectionName, fileNamePattern);
    }
}
