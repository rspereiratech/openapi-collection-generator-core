package io.github.rspereiratech.openapi.collection.generator.core.writer;

import io.github.rspereiratech.openapi.collection.generator.core.config.GenerationConfig;
import io.github.rspereiratech.openapi.collection.generator.core.model.CollectionFormat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileCollectionWriterTest {

    private final FileCollectionWriter writer = new FileCollectionWriter();

    @Test
    void writesFileWithSubstitutedPattern(@TempDir Path tmp) throws Exception {
        GenerationConfig cfg = new GenerationConfig(tmp.toFile(), CollectionFormat.POSTMAN,
                "MyApi", "{name}_{format}.json");

        File out = writer.write("{\"hello\":1}", cfg);

        assertEquals("MyApi_postman.json", out.getName());
        String content = Files.readString(out.toPath(), StandardCharsets.UTF_8);
        assertEquals("{\"hello\":1}", content);
    }

    @Test
    void defaultsCollectionNameWhenNull(@TempDir Path tmp) throws Exception {
        GenerationConfig cfg = new GenerationConfig(tmp.toFile(), CollectionFormat.INSOMNIA, null);

        File out = writer.write("[]", cfg);

        assertTrue(out.getName().startsWith("collection_insomnia"));
    }

    @Test
    void sanitizesIllegalNameCharacters(@TempDir Path tmp) throws Exception {
        GenerationConfig cfg = new GenerationConfig(tmp.toFile(), CollectionFormat.POSTMAN,
                "My/Bad Name!", "{name}_{format}.json");

        File out = writer.write("{}", cfg);

        assertTrue(out.getName().startsWith("My_Bad_Name_"));
    }

    @Test
    void createsOutputDirectoryWhenMissing(@TempDir Path tmp) throws Exception {
        File dir = tmp.resolve("nested/deeper").toFile();
        GenerationConfig cfg = new GenerationConfig(dir, CollectionFormat.POSTMAN, "x");

        File out = writer.write("data", cfg);

        assertTrue(dir.exists());
        assertTrue(out.exists());
    }

    @Test
    void throwsWriterExceptionWhenDirectoryCannotBeCreated(@TempDir Path tmp) throws Exception {
        // Create a regular file where a directory is expected - mkdirs will fail
        File blocker = tmp.resolve("blocker").toFile();
        Files.writeString(blocker.toPath(), "stuff");
        File dirInsideFile = new File(blocker, "sub");

        GenerationConfig cfg = new GenerationConfig(dirInsideFile, CollectionFormat.POSTMAN, "x");

        assertThrows(WriterException.class, () -> writer.write("data", cfg));
    }
}
