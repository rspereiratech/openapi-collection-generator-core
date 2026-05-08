package com.github.rspereiratech.openapi.collection.generator.core.writer;

import com.github.rspereiratech.openapi.collection.generator.core.config.GenerationConfig;
import com.github.rspereiratech.openapi.collection.generator.core.generator.AdditionalFile;

import java.io.File;
import java.util.List;

/**
 * Writes additional environment files (e.g. server environment configurations)
 * to a persistent destination.
 */
public interface EnvironmentWriter {

    /**
     * Writes all additional files to the output directory specified in the configuration.
     *
     * @param files  the list of additional files to write
     * @param config the plugin configuration providing the output directory
     * @return the list of written files
     * @throws WriterException if any file cannot be created or written
     */
    List<File> writeAll(List<AdditionalFile> files, GenerationConfig config) throws WriterException;
}
