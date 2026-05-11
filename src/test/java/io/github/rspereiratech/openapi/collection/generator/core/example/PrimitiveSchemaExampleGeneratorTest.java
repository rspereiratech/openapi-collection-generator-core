package io.github.rspereiratech.openapi.collection.generator.core.example;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.StringSchema;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PrimitiveSchemaExampleGeneratorTest {

    private final SchemaExampleGenerator next = mock(SchemaExampleGenerator.class);
    private final PrimitiveSchemaExampleGenerator gen = new PrimitiveSchemaExampleGenerator(next);

    @Test
    void nullSchemaReturnsNull() {
        assertNull(gen.generate(null, new OpenAPI()));
    }

    @Test
    void usesExplicitExampleWhenPresent() {
        StringSchema s = new StringSchema();
        s.setExample("hello");

        assertEquals("hello", gen.generate(s, new OpenAPI()));
    }

    @Test
    void usesFirstEnumValueWhenNoExample() {
        StringSchema s = new StringSchema();
        s.setEnum(java.util.List.of("a", "b"));

        assertEquals("a", gen.generate(s, new OpenAPI()));
    }

    @Test
    void usesDefaultWhenNoExampleOrEnum() {
        StringSchema s = new StringSchema();
        s.setDefault("d");

        assertEquals("d", gen.generate(s, new OpenAPI()));
    }

    @Test
    void usesXExamplesExtensionWithValueWrapper() {
        StringSchema s = new StringSchema();
        Map<String, Object> ext = new LinkedHashMap<>();
        Map<String, Object> first = Map.of("value", "from-ext");
        ext.put("x-examples", Map.of("ex1", first));
        s.setExtensions(ext);

        assertEquals("from-ext", gen.generate(s, new OpenAPI()));
    }

    @Test
    void usesXExamplesExtensionRawWhenNoValueKey() {
        StringSchema s = new StringSchema();
        Map<String, Object> ext = new LinkedHashMap<>();
        Map<String, Object> firstExample = Map.of("ex1", "raw-string");
        ext.put("x-examples", firstExample);
        s.setExtensions(ext);

        // first instanceof Map but no value key, so the inner Map "value" lookup is null and returns first
        // Actually: first = "raw-string" (the value of "ex1") -> not a map -> returns first
        assertEquals("raw-string", gen.generate(s, new OpenAPI()));
    }

    @Test
    void ignoresEmptyXExamples() {
        StringSchema s = new StringSchema();
        Map<String, Object> ext = new LinkedHashMap<>();
        ext.put("x-examples", Map.of());
        s.setExtensions(ext);

        // No explicit example, type=string -> "string"
        assertEquals("string", gen.generate(s, new OpenAPI()));
    }

    @Test
    void stringDefaultValue() {
        StringSchema s = new StringSchema();

        assertEquals("string", gen.generate(s, new OpenAPI()));
    }

    @Test
    void stringWithFormatDate() {
        StringSchema s = new StringSchema();
        s.setFormat("date");

        assertEquals("2024-01-01", gen.generate(s, new OpenAPI()));
    }

    @Test
    void stringWithFormatDateTime() {
        StringSchema s = new StringSchema();
        s.setFormat("date-time");

        assertEquals("2024-01-01T00:00:00Z", gen.generate(s, new OpenAPI()));
    }

    @Test
    void stringWithFormatUuid() {
        StringSchema s = new StringSchema();
        s.setFormat("uuid");

        assertEquals("00000000-0000-0000-0000-000000000000", gen.generate(s, new OpenAPI()));
    }

    @Test
    void stringWithFormatEmail() {
        StringSchema s = new StringSchema();
        s.setFormat("email");

        assertEquals("user@example.com", gen.generate(s, new OpenAPI()));
    }

    @Test
    void stringWithFormatUri() {
        StringSchema s = new StringSchema();
        s.setFormat("uri");

        assertEquals("https://example.com", gen.generate(s, new OpenAPI()));
    }

    @Test
    void stringWithFormatByte() {
        StringSchema s = new StringSchema();
        s.setFormat("byte");

        assertEquals("dGVzdA==", gen.generate(s, new OpenAPI()));
    }

    @Test
    void stringWithFormatBinary() {
        StringSchema s = new StringSchema();
        s.setFormat("binary");

        assertEquals("<binary>", gen.generate(s, new OpenAPI()));
    }

    @Test
    void stringWithFormatPassword() {
        StringSchema s = new StringSchema();
        s.setFormat("password");

        assertEquals("********", gen.generate(s, new OpenAPI()));
    }

    @Test
    void stringWithUnknownFormatDefaultsToString() {
        StringSchema s = new StringSchema();
        s.setFormat("zzz-unknown");

        assertEquals("string", gen.generate(s, new OpenAPI()));
    }

    @Test
    void integerDefaultIsZero() {
        IntegerSchema s = new IntegerSchema();

        assertEquals(0, gen.generate(s, new OpenAPI()));
    }

    @Test
    void integerWithMinimumUsesMinimum() {
        IntegerSchema s = new IntegerSchema();
        s.setMinimum(BigDecimal.valueOf(42));

        assertEquals(42, gen.generate(s, new OpenAPI()));
    }

    @Test
    void numberDefaultIsZero() {
        NumberSchema s = new NumberSchema();

        assertEquals(0.0, gen.generate(s, new OpenAPI()));
    }

    @Test
    void numberWithMinimumUsesMinimumAsDouble() {
        NumberSchema s = new NumberSchema();
        s.setMinimum(BigDecimal.valueOf(3.5));

        assertEquals(3.5, gen.generate(s, new OpenAPI()));
    }

    @Test
    void booleanReturnsTrue() {
        io.swagger.v3.oas.models.media.BooleanSchema s = new io.swagger.v3.oas.models.media.BooleanSchema();

        assertEquals(true, gen.generate(s, new OpenAPI()));
    }

    @Test
    void unknownTypeDelegatesToNext() {
        io.swagger.v3.oas.models.media.Schema<Object> s = new io.swagger.v3.oas.models.media.Schema<>();
        s.setType("weird");
        OpenAPI api = new OpenAPI();
        when(next.generate(s, api)).thenReturn("from-next");

        assertEquals("from-next", gen.generate(s, api));
    }

    @Test
    void unknownTypeWithoutNextReturnsNull() {
        PrimitiveSchemaExampleGenerator g = new PrimitiveSchemaExampleGenerator(null);
        io.swagger.v3.oas.models.media.Schema<Object> s = new io.swagger.v3.oas.models.media.Schema<>();
        s.setType("weird");

        assertNull(g.generate(s, new OpenAPI()));
    }
}
