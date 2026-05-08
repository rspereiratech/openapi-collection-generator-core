package com.github.rspereiratech.openapi.collection.generator.core.writer;

import com.github.rspereiratech.openapi.collection.generator.core.config.GenerationConfig;

import java.io.File;

/**
 * Writes a serialized collection JSON string to a persistent destination.
 */
public interface CollectionWriter {

    /**
     * Writes the given JSON content to a file determined by the plugin configuration.
     *
     * @param json   the JSON string to write
     * @param config the plugin configuration providing output directory and naming
     * @return the written file
     * @throws WriterException if the file cannot be created or written
     */
    File write(String json, GenerationConfig config) throws WriterException;
}
