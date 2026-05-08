package com.github.rspereiratech.openapi.collection.generator.core.writer;

import com.github.rspereiratech.openapi.collection.generator.core.config.GenerationConfig;
import com.github.rspereiratech.openapi.collection.generator.core.generator.AdditionalFile;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link EnvironmentWriter} implementation that writes environment files
 * to the local filesystem, creating directories as needed.
 */
public class FileEnvironmentWriter implements EnvironmentWriter {

    @Override
    public List<File> writeAll(List<AdditionalFile> files, GenerationConfig config) throws WriterException {
        File dir = ensureDirectory(config.outputDirectory());
        List<File> written = new ArrayList<>();
        for (var af : files) {
            written.add(writeFile(dir, af));
        }
        return written;
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
     * Writes a single additional file to the given directory.
     *
     * @param dir  the output directory
     * @param file the file to write
     * @return the written file reference
     * @throws WriterException if writing fails
     */
    private File writeFile(File dir, AdditionalFile file) throws WriterException {
        try {
            File out = new File(dir, file.fileName());
            Files.writeString(out.toPath(), file.content(), StandardCharsets.UTF_8);
            return out;
        } catch (Exception e) {
            throw new WriterException("Failed to write " + file.fileName(), e);
        }
    }
}
