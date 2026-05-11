package io.github.rspereiratech.openapi.collection.generator.core.writer;

import io.github.rspereiratech.openapi.collection.generator.core.config.GenerationConfig;
import io.github.rspereiratech.openapi.collection.generator.core.generator.AdditionalFile;
import io.github.rspereiratech.openapi.collection.generator.core.model.CollectionFormat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileEnvironmentWriterTest {

    private final FileEnvironmentWriter writer = new FileEnvironmentWriter();

    @Test
    void writesAllAdditionalFiles(@TempDir Path tmp) throws Exception {
        GenerationConfig cfg = new GenerationConfig(tmp.toFile(), CollectionFormat.POSTMAN, "X");

        List<File> out = writer.writeAll(List.of(
                new AdditionalFile("a.json", "A"),
                new AdditionalFile("b.json", "B")), cfg);

        assertEquals(2, out.size());
        assertEquals("A", Files.readString(out.get(0).toPath(), StandardCharsets.UTF_8));
        assertEquals("B", Files.readString(out.get(1).toPath(), StandardCharsets.UTF_8));
    }

    @Test
    void emptyListProducesEmptyResult(@TempDir Path tmp) throws Exception {
        GenerationConfig cfg = new GenerationConfig(tmp.toFile(), CollectionFormat.POSTMAN, "X");

        List<File> out = writer.writeAll(List.of(), cfg);

        assertTrue(out.isEmpty());
    }

    @Test
    void createsDirectoryIfMissing(@TempDir Path tmp) throws Exception {
        File dir = tmp.resolve("new/sub").toFile();
        GenerationConfig cfg = new GenerationConfig(dir, CollectionFormat.POSTMAN, "X");

        writer.writeAll(List.of(new AdditionalFile("x.json", "y")), cfg);

        assertTrue(dir.exists());
    }

    @Test
    void throwsWhenDirectoryCannotBeCreated(@TempDir Path tmp) throws Exception {
        File blocker = tmp.resolve("blocker").toFile();
        Files.writeString(blocker.toPath(), "stuff");
        File dirInsideFile = new File(blocker, "sub");

        GenerationConfig cfg = new GenerationConfig(dirInsideFile, CollectionFormat.POSTMAN, "X");

        assertThrows(WriterException.class,
                () -> writer.writeAll(List.of(new AdditionalFile("x", "y")), cfg));
    }
}
