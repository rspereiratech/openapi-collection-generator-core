package io.github.rspereiratech.openapi.collection.generator.core.example;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NullableSchemaExampleGeneratorTest {

    private final SchemaExampleGenerator next = mock(SchemaExampleGenerator.class);

    @Test
    void delegatesToNext() {
        Schema<?> schema = new StringSchema();
        OpenAPI api = new OpenAPI();
        when(next.generate(schema, api)).thenReturn("hello");

        NullableSchemaExampleGenerator gen = new NullableSchemaExampleGenerator(next);

        assertEquals("hello", gen.generate(schema, api));
        verify(next).generate(schema, api);
    }

    @Test
    void returnsNullWhenNoNextProvided() {
        NullableSchemaExampleGenerator gen = new NullableSchemaExampleGenerator(null);

        assertNull(gen.generate(new StringSchema(), new OpenAPI()));
    }
}
