package io.github.rspereiratech.openapi.collection.generator.core.loader;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileSpecLoaderTest {

    private final FileSpecLoader loader = new FileSpecLoader();

    @Test
    void acceptsExistingFile(@TempDir Path tmp) throws Exception {
        Path f = tmp.resolve("spec.yaml");
        Files.writeString(f, "openapi: 3.0.0");

        assertDoesNotThrow(() -> loader.validate(f.toFile()));
    }

    @Test
    void rejectsNullFile() {
        SpecValidationException ex = assertThrows(SpecValidationException.class, () -> loader.validate(null));

        assertTrue(ex.getMessage().contains("Spec not found"));
    }

    @Test
    void rejectsMissingFile(@TempDir Path tmp) {
        File f = tmp.resolve("nope.yaml").toFile();

        SpecValidationException ex = assertThrows(SpecValidationException.class, () -> loader.validate(f));

        assertTrue(ex.getMessage().contains("Spec not found"));
    }

    @Test
    void rejectsDirectory(@TempDir Path tmp) {
        SpecValidationException ex = assertThrows(SpecValidationException.class, () -> loader.validate(tmp.toFile()));

        assertTrue(ex.getMessage().contains("not a file"));
    }
}
