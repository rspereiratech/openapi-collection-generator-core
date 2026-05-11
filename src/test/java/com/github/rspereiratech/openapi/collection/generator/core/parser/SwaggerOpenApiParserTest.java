package com.github.rspereiratech.openapi.collection.generator.core.parser;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SwaggerOpenApiParserTest {

    private final SwaggerOpenApiParser parser = new SwaggerOpenApiParser();

    @Test
    void parsesValidYamlSpec(@TempDir Path tmp) throws Exception {
        Path spec = tmp.resolve("spec.yaml");
        Files.writeString(spec, """
                openapi: 3.0.3
                info:
                  title: Sample
                  version: 1.0.0
                paths:
                  /ping:
                    get:
                      summary: ping
                      responses:
                        '200':
                          description: ok
                """);

        OpenAPI api = parser.parse(spec.toFile());

        assertNotNull(api);
        assertEquals("Sample", api.getInfo().getTitle());
    }

    @Test
    void throwsForMissingFile(@TempDir Path tmp) {
        File missing = tmp.resolve("nothere.yaml").toFile();

        assertThrows(OpenApiParseException.class, () -> parser.parse(missing));
    }

    @Test
    void throwsForInvalidSpec(@TempDir Path tmp) throws Exception {
        Path spec = tmp.resolve("bad.yaml");
        Files.writeString(spec, "this is: not\n  an: openapi spec");

        assertThrows(OpenApiParseException.class, () -> parser.parse(spec.toFile()));
    }
}
