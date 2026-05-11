package com.github.rspereiratech.openapi.collection.generator.core.schema.discriminator;

import com.github.rspereiratech.openapi.collection.generator.core.schema.ref.SchemaRefResolver;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Discriminator;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class DefaultDiscriminatorResolverTest {

    private final SchemaRefResolver refResolver = mock(SchemaRefResolver.class);
    private final DefaultDiscriminatorResolver resolver = new DefaultDiscriminatorResolver(refResolver);

    @Test
    void resolvesUsingFirstDiscriminatorMappingWhenPresent() {
        Schema<?> chosen = new StringSchema();
        Discriminator d = new Discriminator();
        Map<String, String> map = new LinkedHashMap<>();
        map.put("dog", "#/components/schemas/Dog");
        map.put("cat", "#/components/schemas/Cat");
        d.setMapping(map);

        OpenAPI api = new OpenAPI();
        doReturn(chosen).when(refResolver).resolveByName("Dog", api);

        Schema<?> result = resolver.resolve(List.of(), d, api);

        assertSame(chosen, result);
    }

    @Test
    void fallsBackToFirstSchemaWhenMappingResolutionFails() {
        Discriminator d = new Discriminator();
        d.setMapping(Map.of("foo", "#/components/schemas/Foo"));

        OpenAPI api = new OpenAPI();
        doReturn(null).when(refResolver).resolveByName("Foo", api);

        Schema<?> firstFallback = new StringSchema();
        Schema<?> resolvedFallback = new StringSchema();
        doReturn(resolvedFallback).when(refResolver).resolve(firstFallback, api);

        Schema<?> result = resolver.resolve(List.of(firstFallback), d, api);

        assertSame(resolvedFallback, result);
    }

    @Test
    void returnsFirstSchemaWhenDiscriminatorNull() {
        Schema<?> first = new StringSchema();
        Schema<?> resolved = new StringSchema();
        OpenAPI api = new OpenAPI();
        doReturn(resolved).when(refResolver).resolve(first, api);

        Schema<?> result = resolver.resolve(List.of(first), null, api);

        assertSame(resolved, result);
    }

    @Test
    void returnsFirstSchemaWhenDiscriminatorMappingEmpty() {
        Discriminator d = new Discriminator();
        d.setMapping(Map.of());
        Schema<?> first = new StringSchema();
        OpenAPI api = new OpenAPI();
        doReturn(first).when(refResolver).resolve(first, api);

        Schema<?> result = resolver.resolve(List.of(first), d, api);

        assertSame(first, result);
    }

    @Test
    void returnsNullWhenSchemasNullAndNoMapping() {
        Schema<?> result = resolver.resolve(null, null, new OpenAPI());

        assertNull(result);
    }

    @Test
    void returnsNullWhenSchemasEmptyAndNoMapping() {
        Schema<?> result = resolver.resolve(List.of(), null, new OpenAPI());

        assertNull(result);
    }

    @Test
    void mappingReplacesComponentsPrefix() {
        Discriminator d = new Discriminator();
        d.setMapping(Map.of("k", "#/components/schemas/Bar"));
        Schema<?> chosen = new StringSchema();
        OpenAPI api = new OpenAPI();
        doReturn(chosen).when(refResolver).resolveByName(eq("Bar"), any());

        Schema<?> result = resolver.resolve(List.of(), d, api);

        assertSame(chosen, result);
    }
}
