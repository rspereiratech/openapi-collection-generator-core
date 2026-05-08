package com.github.rspereiratech.openapi.collection.generator.core.loader;

import java.io.File;

/**
 * Filesystem-based implementation of {@link SpecLoader} that checks whether the spec file
 * exists on disk and is a regular file.
 */
public class FileSpecLoader implements SpecLoader {

    @Override
    public void validate(File f) throws SpecValidationException {
        if (f == null || !f.exists()) {
            throw new SpecValidationException("Spec not found: " + f);
        }

        if (!f.isFile()) {
            throw new SpecValidationException("Spec is not a file: " + f);
        }
    }
}
