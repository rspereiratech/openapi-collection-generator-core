package io.github.rspereiratech.openapi.collection.generator.core.config;

import io.github.rspereiratech.openapi.collection.generator.core.model.CollectionFormat;

import java.io.File;

/**
 * Core configuration parameters used by generators and writers.
 *
 * @param outputDirectory directory where generated files are written
 * @param format          target collection format (e.g. Postman, Insomnia)
 * @param collectionName  optional human-readable name for the generated collection
 * @param fileNamePattern pattern for the output file name, supports {@code {name}} and {@code {format}} placeholders
 */
public record GenerationConfig(File outputDirectory, CollectionFormat format, String collectionName,
                               String fileNamePattern) {

    public static final String DEFAULT_FILE_NAME_PATTERN = "{name}_{format}.json";

    public GenerationConfig(File outputDirectory, CollectionFormat format, String collectionName) {
        this(outputDirectory, format, collectionName, DEFAULT_FILE_NAME_PATTERN);
    }

}
