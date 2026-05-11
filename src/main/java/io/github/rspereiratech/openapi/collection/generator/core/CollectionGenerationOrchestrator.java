package io.github.rspereiratech.openapi.collection.generator.core;

import io.github.rspereiratech.openapi.collection.generator.core.config.GenerationConfig;
import io.github.rspereiratech.openapi.collection.generator.core.factory.CollectionGeneratorFactory;
import io.github.rspereiratech.openapi.collection.generator.core.generator.AdditionalFile;
import io.github.rspereiratech.openapi.collection.generator.core.generator.CollectionGenerationException;
import io.github.rspereiratech.openapi.collection.generator.core.generator.CollectionGenerator;
import io.github.rspereiratech.openapi.collection.generator.core.loader.SpecLoader;
import io.github.rspereiratech.openapi.collection.generator.core.loader.SpecValidationException;
import io.github.rspereiratech.openapi.collection.generator.core.model.CollectionFormat;
import io.github.rspereiratech.openapi.collection.generator.core.parser.OpenApiParseException;
import io.github.rspereiratech.openapi.collection.generator.core.parser.OpenApiParser;
import io.github.rspereiratech.openapi.collection.generator.core.writer.CollectionWriter;
import io.github.rspereiratech.openapi.collection.generator.core.writer.EnvironmentWriter;
import io.github.rspereiratech.openapi.collection.generator.core.writer.WriterException;
import io.swagger.v3.oas.models.OpenAPI;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Orchestrates the full collection generation lifecycle: validate, parse, generate and write.
 * This is the single entry point for all clients (Maven plugin, CLI, Gradle, etc.).
 */
public class CollectionGenerationOrchestrator {

    /** Loads and validates the OpenAPI specification file. */
    private final SpecLoader specLoader;
    /** Parses the OpenAPI specification into a structured model. */
    private final OpenApiParser parser;
    /** Creates the appropriate collection generator based on the target format. */
    private final CollectionGeneratorFactory factory;
    /** Writes the generated collection output to the file system. */
    private final CollectionWriter writer;
    /** Writes the generated environment file to the file system. */
    private final EnvironmentWriter environmentWriter;

    /**
     * Creates a new orchestrator with the given components.
     *
     * @param specLoader          loads and validates the OpenAPI specification file
     * @param parser              parses the specification into a structured model
     * @param factory             creates the appropriate generator for the target format
     * @param writer              writes the generated collection to the file system
     * @param environmentWriter   writes the generated environment file to the file system
     */
    public CollectionGenerationOrchestrator(SpecLoader specLoader, OpenApiParser parser,
                                           CollectionGeneratorFactory factory, CollectionWriter writer,
                                           EnvironmentWriter environmentWriter) {
        this.specLoader = specLoader;
        this.parser = parser;
        this.factory = factory;
        this.writer = writer;
        this.environmentWriter = environmentWriter;
    }

    /**
     * Executes the full generation pipeline for all formats in the request.
     *
     * @param request the generation request with spec file, formats, and output configuration
     * @return a list of results, one per format
     * @throws CollectionGenerationException if any step in the pipeline fails
     */
    public List<GenerationResult> generate(GenerationRequest request) throws CollectionGenerationException {
        try {
            specLoader.validate(request.specFile());
        } catch (SpecValidationException e) {
            throw new CollectionGenerationException("Spec validation failed", e);
        }

        OpenAPI openApi;
        try {
            openApi = parser.parse(request.specFile());
        } catch (OpenApiParseException e) {
            throw new CollectionGenerationException("Failed to parse OpenAPI spec", e);
        }

        List<GenerationResult> results = new ArrayList<>();
        for (CollectionFormat format : request.formats()) {

            GenerationConfig config = request.toGenerationConfig(format);
            CollectionGenerator generator = factory.create(format);

            String json = generator.generate(openApi, config);

            File collectionFile;
            try {
                collectionFile = writer.write(json, config);
            } catch (WriterException e) {
                throw new CollectionGenerationException("Failed to write collection", e);
            }

            List<File> additionalFiles = List.of();
            List<AdditionalFile> extras = generator.generateAdditionalFiles(openApi, config);
            if (!extras.isEmpty()) {
                try {
                    additionalFiles = environmentWriter.writeAll(extras, config);
                } catch (WriterException e) {
                    throw new CollectionGenerationException("Failed to write environment files", e);
                }
            }

            results.add(new GenerationResult(collectionFile, additionalFiles));
        }

        return results;
    }
}
