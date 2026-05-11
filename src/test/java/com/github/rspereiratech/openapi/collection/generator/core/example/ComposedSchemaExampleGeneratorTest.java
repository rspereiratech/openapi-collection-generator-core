package com.github.rspereiratech.openapi.collection.generator.core.example;

import com.github.rspereiratech.openapi.collection.generator.core.schema.discriminator.DiscriminatorResolver;
import com.github.rspereiratech.openapi.collection.generator.core.schema.ref.SchemaRefResolver;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.Discriminator;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ComposedSchemaExampleGeneratorTest {

    private final SchemaExampleGenerator next = mock(SchemaExampleGenerator.class);
    private final SchemaRefResolver refResolver = mock(SchemaRefResolver.class);
    private final DiscriminatorResolver discriminator = mock(DiscriminatorResolver.class);
    private final SchemaExampleGenerator recursive = mock(SchemaExampleGenerator.class);

    private final ComposedSchemaExampleGenerator gen =
            new ComposedSchemaExampleGenerator(next, refResolver, discriminator, recursive);

    @Test
    void nullSchemaReturnsNull() {
        assertNull(gen.generate(null, new OpenAPI()));
    }

    @Test
    void delegatesToNextWhenNoCompositionPresent() {
        StringSchema s = new StringSchema();
        OpenAPI api = new OpenAPI();
        doReturn(s).when(refResolver).resolve(s, api);
        when(next.generate(s, api)).thenReturn("delegated");

        assertEquals("delegated", gen.generate(s, api));
    }

    @Test
    void returnsNullWhenNoCompositionAndNoNext() {
        ComposedSchemaExampleGenerator g =
                new ComposedSchemaExampleGenerator(null, refResolver, discriminator, recursive);
        StringSchema s = new StringSchema();
        OpenAPI api = new OpenAPI();
        doReturn(s).when(refResolver).resolve(s, api);

        assertNull(g.generate(s, api));
    }

    @Test
    void allOfMergesPropertyMaps() {
        ComposedSchema c = new ComposedSchema();
        StringSchema sub1 = new StringSchema();
        sub1.setDescription("sub1");
        StringSchema sub2 = new StringSchema();
        sub2.setDescription("sub2");
        c.setAllOf(List.of(sub1, sub2));

        OpenAPI api = new OpenAPI();
        doReturn(c).when(refResolver).resolve(c, api);
        doReturn(sub1).when(refResolver).resolve(sub1, api);
        doReturn(sub2).when(refResolver).resolve(sub2, api);

        Map<String, Object> m1 = new LinkedHashMap<>();
        m1.put("a", 1);
        Map<String, Object> m2 = new LinkedHashMap<>();
        m2.put("b", 2);

        when(recursive.generate(sub1, api)).thenReturn(m1);
        when(recursive.generate(sub2, api)).thenReturn(m2);

        Object out = gen.generate(c, api);

        assertInstanceOf(Map.class, out);
        Map<?, ?> merged = (Map<?, ?>) out;
        assertEquals(1, merged.get("a"));
        assertEquals(2, merged.get("b"));
    }

    @Test
    void allOfIncludesTopLevelProperties() {
        ComposedSchema c = new ComposedSchema();
        StringSchema sub = new StringSchema();
        sub.setDescription("sub");
        c.setAllOf(List.of(sub));
        StringSchema topProp = new StringSchema();
        topProp.setDescription("topProp");
        Map<String, Schema> props = new LinkedHashMap<>();
        props.put("top", topProp);
        c.setProperties(props);

        OpenAPI api = new OpenAPI();
        doReturn(c).when(refResolver).resolve(c, api);
        doReturn(sub).when(refResolver).resolve(sub, api);
        when(recursive.generate(sub, api)).thenReturn(Map.of("nested", "v"));
        when(recursive.generate(topProp, api)).thenReturn("top-value");

        Object out = gen.generate(c, api);

        assertInstanceOf(Map.class, out);
        Map<?, ?> merged = (Map<?, ?>) out;
        assertEquals("v", merged.get("nested"));
        assertEquals("top-value", merged.get("top"));
    }

    @Test
    void allOfReturnsNullWhenEmpty() {
        ComposedSchema c = new ComposedSchema();
        StringSchema sub = new StringSchema();
        c.setAllOf(List.of(sub));
        OpenAPI api = new OpenAPI();
        doReturn(c).when(refResolver).resolve(c, api);
        doReturn(sub).when(refResolver).resolve(sub, api);
        when(recursive.generate(sub, api)).thenReturn("not-a-map");

        Object out = gen.generate(c, api);

        assertNull(out);
    }

    @Test
    void oneOfDelegatesToDiscriminator() {
        ComposedSchema c = new ComposedSchema();
        StringSchema option = new StringSchema();
        c.setOneOf(List.of(option));
        Discriminator disc = new Discriminator();
        c.setDiscriminator(disc);

        OpenAPI api = new OpenAPI();
        doReturn(c).when(refResolver).resolve(c, api);

        ObjectSchema chosen = new ObjectSchema();
        doReturn(chosen).when(discriminator).resolve(anyList(), any(Discriminator.class), any());
        when(recursive.generate(chosen, api)).thenReturn(Map.of("k", "v"));

        Object out = gen.generate(c, api);

        assertEquals(Map.of("k", "v"), out);
    }

    @Test
    void oneOfReturnsNullWhenDiscriminatorCantChoose() {
        ComposedSchema c = new ComposedSchema();
        c.setOneOf(List.of(new StringSchema()));

        OpenAPI api = new OpenAPI();
        doReturn(c).when(refResolver).resolve(c, api);
        doReturn(null).when(discriminator).resolve(anyList(), any(), any());

        assertNull(gen.generate(c, api));
    }

    @Test
    void anyOfDelegatesToDiscriminatorWithoutDiscriminatorArg() {
        ComposedSchema c = new ComposedSchema();
        StringSchema option = new StringSchema();
        c.setAnyOf(List.of(option));

        OpenAPI api = new OpenAPI();
        doReturn(c).when(refResolver).resolve(c, api);
        ObjectSchema chosen = new ObjectSchema();
        doReturn(chosen).when(discriminator).resolve(anyList(), any(), any());
        when(recursive.generate(chosen, api)).thenReturn("v");

        assertEquals("v", gen.generate(c, api));
    }

    @Test
    void resolverReturningNullUsesOriginalSchema() {
        StringSchema s = new StringSchema();
        OpenAPI api = new OpenAPI();
        doReturn(null).when(refResolver).resolve(s, api);
        when(next.generate(s, api)).thenReturn("fallback");

        assertEquals("fallback", gen.generate(s, api));
    }
}
