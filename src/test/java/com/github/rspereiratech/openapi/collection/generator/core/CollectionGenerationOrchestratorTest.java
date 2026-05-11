package com.github.rspereiratech.openapi.collection.generator.core;

import com.github.rspereiratech.openapi.collection.generator.core.config.GenerationConfig;
import com.github.rspereiratech.openapi.collection.generator.core.factory.CollectionGeneratorFactory;
import com.github.rspereiratech.openapi.collection.generator.core.generator.AdditionalFile;
import com.github.rspereiratech.openapi.collection.generator.core.generator.CollectionGenerationException;
import com.github.rspereiratech.openapi.collection.generator.core.generator.CollectionGenerator;
import com.github.rspereiratech.openapi.collection.generator.core.loader.SpecLoader;
import com.github.rspereiratech.openapi.collection.generator.core.loader.SpecValidationException;
import com.github.rspereiratech.openapi.collection.generator.core.model.CollectionFormat;
import com.github.rspereiratech.openapi.collection.generator.core.parser.OpenApiParseException;
import com.github.rspereiratech.openapi.collection.generator.core.parser.OpenApiParser;
import com.github.rspereiratech.openapi.collection.generator.core.writer.CollectionWriter;
import com.github.rspereiratech.openapi.collection.generator.core.writer.EnvironmentWriter;
import com.github.rspereiratech.openapi.collection.generator.core.writer.WriterException;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CollectionGenerationOrchestratorTest {

    private final SpecLoader specLoader = mock(SpecLoader.class);
    private final OpenApiParser parser = mock(OpenApiParser.class);
    private final CollectionGeneratorFactory factory = mock(CollectionGeneratorFactory.class);
    private final CollectionWriter writer = mock(CollectionWriter.class);
    private final EnvironmentWriter environmentWriter = mock(EnvironmentWriter.class);
    private final CollectionGenerator generator = mock(CollectionGenerator.class);

    private CollectionGenerationOrchestrator orchestrator() {
        return new CollectionGenerationOrchestrator(specLoader, parser, factory, writer, environmentWriter);
    }

    @Test
    void generatesAcrossAllFormats(@TempDir Path tmp) throws Exception {
        File spec = tmp.resolve("spec.yaml").toFile();
        spec.createNewFile();
        OpenAPI parsedApi = new OpenAPI();

        when(parser.parse(spec)).thenReturn(parsedApi);
        when(factory.create(any())).thenReturn(generator);
        when(generator.generate(eq(parsedApi), any())).thenReturn("{}");

        File outFile = new File(tmp.toFile(), "collection.json");
        when(writer.write(eq("{}"), any())).thenReturn(outFile);
        when(generator.generateAdditionalFiles(eq(parsedApi), any())).thenReturn(List.of());

        GenerationRequest req = new GenerationRequest(spec, tmp.toFile(),
                List.of(CollectionFormat.POSTMAN, CollectionFormat.INSOMNIA),
                "api", "{name}_{format}.json");

        List<GenerationResult> results = orchestrator().generate(req);

        assertEquals(2, results.size());
        assertSame(outFile, results.get(0).collectionFile());
        verify(specLoader).validate(spec);
        verify(parser).parse(spec);
    }

    @Test
    void writesAdditionalFilesWhenGeneratorProducesThem(@TempDir Path tmp) throws Exception {
        File spec = tmp.resolve("spec.yaml").toFile();
        spec.createNewFile();
        OpenAPI parsedApi = new OpenAPI();
        when(parser.parse(spec)).thenReturn(parsedApi);
        when(factory.create(any())).thenReturn(generator);
        when(generator.generate(any(), any())).thenReturn("{}");
        File coll = new File(tmp.toFile(), "c.json");
        when(writer.write(any(), any())).thenReturn(coll);

        List<AdditionalFile> extras = List.of(new AdditionalFile("env.json", "data"));
        when(generator.generateAdditionalFiles(any(), any())).thenReturn(extras);

        File envOut = new File(tmp.toFile(), "env.json");
        when(environmentWriter.writeAll(eq(extras), any())).thenReturn(List.of(envOut));

        GenerationRequest req = new GenerationRequest(spec, tmp.toFile(),
                List.of(CollectionFormat.POSTMAN), "api");

        List<GenerationResult> results = orchestrator().generate(req);

        assertEquals(1, results.size());
        assertEquals(List.of(envOut), results.get(0).additionalFiles());
    }

    @Test
    void wrapsSpecValidationFailures(@TempDir Path tmp) throws Exception {
        File spec = tmp.resolve("spec.yaml").toFile();
        spec.createNewFile();
        doThrow(new SpecValidationException("bad")).when(specLoader).validate(spec);

        GenerationRequest req = new GenerationRequest(spec, tmp.toFile(),
                List.of(CollectionFormat.POSTMAN), "api");

        CollectionGenerationException ex = assertThrows(CollectionGenerationException.class,
                () -> orchestrator().generate(req));
        assertTrue(ex.getMessage().contains("Spec validation failed"));
    }

    @Test
    void wrapsParseFailures(@TempDir Path tmp) throws Exception {
        File spec = tmp.resolve("spec.yaml").toFile();
        spec.createNewFile();
        when(parser.parse(spec)).thenThrow(new OpenApiParseException("bad"));

        GenerationRequest req = new GenerationRequest(spec, tmp.toFile(),
                List.of(CollectionFormat.POSTMAN), "api");

        CollectionGenerationException ex = assertThrows(CollectionGenerationException.class,
                () -> orchestrator().generate(req));
        assertTrue(ex.getMessage().contains("parse"));
    }

    @Test
    void wrapsCollectionWriterFailures(@TempDir Path tmp) throws Exception {
        File spec = tmp.resolve("spec.yaml").toFile();
        spec.createNewFile();
        when(parser.parse(spec)).thenReturn(new OpenAPI());
        when(factory.create(any())).thenReturn(generator);
        when(generator.generate(any(), any())).thenReturn("{}");
        when(writer.write(any(), any(GenerationConfig.class))).thenThrow(new WriterException("boom"));

        GenerationRequest req = new GenerationRequest(spec, tmp.toFile(),
                List.of(CollectionFormat.POSTMAN), "api");

        CollectionGenerationException ex = assertThrows(CollectionGenerationException.class,
                () -> orchestrator().generate(req));
        assertTrue(ex.getMessage().contains("write collection"));
    }

    @Test
    void wrapsEnvironmentWriterFailures(@TempDir Path tmp) throws Exception {
        File spec = tmp.resolve("spec.yaml").toFile();
        spec.createNewFile();
        when(parser.parse(spec)).thenReturn(new OpenAPI());
        when(factory.create(any())).thenReturn(generator);
        when(generator.generate(any(), any())).thenReturn("{}");
        when(writer.write(any(), any())).thenReturn(new File(tmp.toFile(), "c.json"));
        when(generator.generateAdditionalFiles(any(), any()))
                .thenReturn(List.of(new AdditionalFile("e.json", "v")));
        when(environmentWriter.writeAll(any(), any())).thenThrow(new WriterException("nope"));

        GenerationRequest req = new GenerationRequest(spec, tmp.toFile(),
                List.of(CollectionFormat.POSTMAN), "api");

        CollectionGenerationException ex = assertThrows(CollectionGenerationException.class,
                () -> orchestrator().generate(req));
        assertTrue(ex.getMessage().contains("environment"));
    }
}
