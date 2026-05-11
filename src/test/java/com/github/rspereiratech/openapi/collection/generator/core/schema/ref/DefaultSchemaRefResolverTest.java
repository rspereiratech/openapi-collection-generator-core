package com.github.rspereiratech.openapi.collection.generator.core.schema.ref;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class DefaultSchemaRefResolverTest {

    private final DefaultSchemaRefResolver resolver = new DefaultSchemaRefResolver();

    @Test
    void resolveReturnsSchemaWhenNoRef() {
        Schema<?> s = new StringSchema();
        OpenAPI openApi = new OpenAPI();

        assertSame(s, resolver.resolve(s, openApi));
    }

    @Test
    void resolveReturnsNullWhenSchemaNull() {
        assertNull(resolver.resolve(null, new OpenAPI()));
    }

    @Test
    void resolveFollowsRefIntoComponents() {
        Schema<?> target = new ObjectSchema();
        target.setType("object");
        OpenAPI api = new OpenAPI();
        api.setComponents(new Components());
        api.getComponents().setSchemas(Map.of("Foo", target));

        Schema<Object> withRef = new Schema<>();
        withRef.set$ref("#/components/schemas/Foo");

        Schema<?> result = resolver.resolve(withRef, api);

        assertSame(target, result);
    }

    @Test
    void resolveReturnsOriginalWhenRefTargetMissing() {
        OpenAPI api = new OpenAPI();
        api.setComponents(new Components());
        api.getComponents().setSchemas(Map.of());

        Schema<Object> withRef = new Schema<>();
        withRef.set$ref("#/components/schemas/Missing");

        Schema<?> result = resolver.resolve(withRef, api);

        assertSame(withRef, result);
    }

    @Test
    void resolveByNameReturnsNullWhenComponentsMissing() {
        OpenAPI api = new OpenAPI();

        assertNull(resolver.resolveByName("X", api));
    }

    @Test
    void resolveByNameReturnsNullWhenSchemasMapMissing() {
        OpenAPI api = new OpenAPI();
        api.setComponents(new Components());

        assertNull(resolver.resolveByName("X", api));
    }

    @Test
    void resolveByNameFindsSchema() {
        Schema<?> s = new StringSchema();
        OpenAPI api = new OpenAPI();
        api.setComponents(new Components());
        api.getComponents().setSchemas(Map.of("X", s));

        assertSame(s, resolver.resolveByName("X", api));
    }
}
