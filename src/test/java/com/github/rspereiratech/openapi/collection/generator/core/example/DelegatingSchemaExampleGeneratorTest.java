package com.github.rspereiratech.openapi.collection.generator.core.example;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DelegatingSchemaExampleGeneratorTest {

    private final SchemaExampleGenerator delegate = mock(SchemaExampleGenerator.class);

    @Test
    void throwsWhenDelegateNotConfigured() {
        DelegatingSchemaExampleGenerator gen = new DelegatingSchemaExampleGenerator();

        assertThrows(IllegalStateException.class,
                () -> gen.generate(new StringSchema(), new OpenAPI()));
    }

    @Test
    void forwardsToDelegate() {
        DelegatingSchemaExampleGenerator gen = new DelegatingSchemaExampleGenerator();
        gen.setDelegate(delegate);
        Schema<?> s = new StringSchema();
        OpenAPI api = new OpenAPI();
        when(delegate.generate(s, api)).thenReturn("v");

        assertEquals("v", gen.generate(s, api));
    }
}
