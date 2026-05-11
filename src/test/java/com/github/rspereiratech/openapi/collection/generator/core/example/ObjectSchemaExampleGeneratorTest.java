package com.github.rspereiratech.openapi.collection.generator.core.example;

import com.github.rspereiratech.openapi.collection.generator.core.schema.ref.SchemaRefResolver;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ObjectSchemaExampleGeneratorTest {

    private final SchemaExampleGenerator next = mock(SchemaExampleGenerator.class);
    private final SchemaRefResolver refResolver = mock(SchemaRefResolver.class);
    private final SchemaExampleGenerator recursive = mock(SchemaExampleGenerator.class);

    private final ObjectSchemaExampleGenerator gen =
            new ObjectSchemaExampleGenerator(next, refResolver, recursive);

    @Test
    void nullSchemaReturnsNull() {
        assertNull(gen.generate(null, new OpenAPI()));
    }

    @Test
    void delegatesToNextForNonObjectType() {
        StringSchema s = new StringSchema();
        OpenAPI api = new OpenAPI();
        doReturn(s).when(refResolver).resolve(s, api);
        when(next.generate(s, api)).thenReturn("delegated");

        assertEquals("delegated", gen.generate(s, api));
    }

    @Test
    void delegatesToNextWhenObjectHasNoProperties() {
        ObjectSchema obj = new ObjectSchema();
        obj.setType("object");
        OpenAPI api = new OpenAPI();
        doReturn(obj).when(refResolver).resolve(obj, api);
        when(next.generate(obj, api)).thenReturn("delegated");

        assertEquals("delegated", gen.generate(obj, api));
    }

    @Test
    void returnsNullWhenNonObjectAndNoNext() {
        ObjectSchemaExampleGenerator g = new ObjectSchemaExampleGenerator(null, refResolver, recursive);
        StringSchema s = new StringSchema();
        OpenAPI api = new OpenAPI();
        doReturn(s).when(refResolver).resolve(s, api);

        assertNull(g.generate(s, api));
    }

    @Test
    void buildsMapOfPropertyExamples() {
        ObjectSchema obj = new ObjectSchema();
        obj.setType("object");
        Map<String, Schema> props = new LinkedHashMap<>();
        StringSchema nameSchema = new StringSchema();
        nameSchema.setDescription("name-field");
        StringSchema cityProp = new StringSchema();
        cityProp.setDescription("city-field");
        props.put("name", nameSchema);
        props.put("city", cityProp);
        obj.setProperties(props);

        OpenAPI api = new OpenAPI();
        doReturn(obj).when(refResolver).resolve(obj, api);
        when(recursive.generate(nameSchema, api)).thenReturn("Alice");
        when(recursive.generate(cityProp, api)).thenReturn("Paris");

        Object out = gen.generate(obj, api);

        assertInstanceOf(Map.class, out);
        Map<?, ?> m = (Map<?, ?>) out;
        assertEquals("Alice", m.get("name"));
        assertEquals("Paris", m.get("city"));
    }

    @Test
    void usesOriginalWhenResolverReturnsNull() {
        ObjectSchema obj = new ObjectSchema();
        obj.setType("object");
        OpenAPI api = new OpenAPI();
        doReturn(null).when(refResolver).resolve(any(), any());
        when(next.generate(obj, api)).thenReturn("fallback");

        assertEquals("fallback", gen.generate(obj, api));
    }
}
