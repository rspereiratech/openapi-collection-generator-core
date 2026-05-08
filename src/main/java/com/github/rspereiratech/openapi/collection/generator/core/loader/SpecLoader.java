package com.github.rspereiratech.openapi.collection.generator.core.loader;

import java.io.File;

/**
 * Contract for validating that an OpenAPI specification file is accessible and suitable for parsing.
 */
public interface SpecLoader {

    /**
     * Validates that the given spec file exists and is a regular file.
     *
     * @param specFile the OpenAPI specification file to validate
     * @throws SpecValidationException if the file is null, does not exist, or is not a regular file
     */
    void validate(File specFile) throws SpecValidationException;
}
