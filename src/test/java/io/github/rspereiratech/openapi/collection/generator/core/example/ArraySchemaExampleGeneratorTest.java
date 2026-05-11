package io.github.rspereiratech.openapi.collection.generator.core.example;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ArraySchemaExampleGeneratorTest {

    private final SchemaExampleGenerator next = mock(SchemaExampleGenerator.class);
    private final SchemaExampleGenerator recursive = mock(SchemaExampleGenerator.class);

    @Test
    void delegatesToNextForNonArraySchema() {
        Schema<?> s = new StringSchema();
        OpenAPI api = new OpenAPI();
        when(next.generate(s, api)).thenReturn("delegated");
        ArraySchemaExampleGenerator gen = new ArraySchemaExampleGenerator(next, recursive);

        Object result = gen.generate(s, api);

        assertEquals("delegated", result);
    }

    @Test
    void returnsNullForNonArrayWithoutNext() {
        ArraySchemaExampleGenerator gen = new ArraySchemaExampleGenerator(null, recursive);

        assertNull(gen.generate(new StringSchema(), new OpenAPI()));
    }

    @Test
    void wrapsRecursiveItemInSingleElementList() {
        ArraySchema arr = new ArraySchema();
        StringSchema item = new StringSchema();
        arr.setItems(item);

        OpenAPI api = new OpenAPI();
        when(recursive.generate(item, api)).thenReturn("hi");

        ArraySchemaExampleGenerator gen = new ArraySchemaExampleGenerator(next, recursive);

        Object result = gen.generate(arr, api);

        assertInstanceOf(List.class, result);
        List<?> list = (List<?>) result;
        assertEquals(1, list.size());
        assertEquals("hi", list.get(0));
    }

    @Test
    void usesPlainObjectWhenItemsMissing() {
        ArraySchema arr = new ArraySchema();
        arr.setItems(null);
        OpenAPI api = new OpenAPI();

        ArraySchemaExampleGenerator gen = new ArraySchemaExampleGenerator(next, recursive);

        Object result = gen.generate(arr, api);

        assertInstanceOf(List.class, result);
        List<?> list = (List<?>) result;
        assertEquals(1, list.size());
        assertNotNull(list.get(0));
    }

    @Test
    void usesPlainObjectWhenRecursiveReturnsNull() {
        ArraySchema arr = new ArraySchema();
        StringSchema item = new StringSchema();
        arr.setItems(item);
        OpenAPI api = new OpenAPI();
        when(recursive.generate(item, api)).thenReturn(null);

        ArraySchemaExampleGenerator gen = new ArraySchemaExampleGenerator(next, recursive);

        Object result = gen.generate(arr, api);

        assertInstanceOf(List.class, result);
        List<?> list = (List<?>) result;
        assertEquals(1, list.size());
        assertNotNull(list.get(0));
    }
}
