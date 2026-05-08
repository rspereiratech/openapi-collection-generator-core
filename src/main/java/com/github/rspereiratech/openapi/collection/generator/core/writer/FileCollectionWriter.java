package com.github.rspereiratech.openapi.collection.generator.core.writer;

import com.github.rspereiratech.openapi.collection.generator.core.config.GenerationConfig;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * {@link CollectionWriter} implementation that writes the collection JSON
 * to a file on the local filesystem, creating directories as needed.
 */
public class FileCollectionWriter implements CollectionWriter {

    @Override
    public File write(String json, GenerationConfig config) throws WriterException {
        try {
            File dir = ensureDirectory(config.outputDirectory());
            File out = new File(dir, buildFileName(config));
            Files.writeString(out.toPath(), json, StandardCharsets.UTF_8);
            return out;
        } catch (IOException e) {
            throw new WriterException("Failed to write collection", e);
        }
    }

    /**
     * Ensures the output directory exists, creating it if necessary.
     *
     * @param dir the target directory
     * @return the same directory reference
     * @throws WriterException if the directory cannot be created
     */
    private File ensureDirectory(File dir) throws WriterException {
        if (!dir.exists() && !dir.mkdirs()) {
            throw new WriterException("Failed to create directory: " + dir);
        }
        return dir;
    }

    /**
     * Builds the output file name from the plugin configuration.
     *
     * @param config the plugin configuration
     * @return the sanitized file name
     */
    private String buildFileName(GenerationConfig config) {
        String name = config.collectionName() != null ? config.collectionName() : "collection";
        String format = config.format().name().toLowerCase();
        String sanitizedName = name.replaceAll("[^a-zA-Z0-9_\\-]", "_");
        return config.fileNamePattern()
                .replace("{name}", sanitizedName)
                .replace("{format}", format);
    }
}
