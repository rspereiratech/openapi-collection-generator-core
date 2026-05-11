package com.github.rspereiratech.openapi.collection.generator.core.security.resolver;

import com.github.rspereiratech.openapi.collection.generator.core.security.factory.SecurityInjectorFactory;
import com.github.rspereiratech.openapi.collection.generator.core.security.injector.SecurityInjector;
import com.github.rspereiratech.openapi.collection.generator.core.security.model.EnvironmentVariable;
import com.github.rspereiratech.openapi.collection.generator.core.security.model.HttpHeader;
import com.github.rspereiratech.openapi.collection.generator.core.security.model.SecurityInjection;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultSecuritySchemeResolverTest {

    private final SecurityInjectorFactory factory = mock(SecurityInjectorFactory.class);
    private final SecurityInjector injector = mock(SecurityInjector.class);

    @Test
    void returnsEmptyWhenNoSecurityRequirements() {
        DefaultSecuritySchemeResolver resolver = new DefaultSecuritySchemeResolver(factory);
        Operation op = new Operation();
        OpenAPI api = new OpenAPI();

        SecurityInjection inj = resolver.resolve(op, api);

        assertTrue(inj.headers().isEmpty());
        assertTrue(inj.queryParams().isEmpty());
        assertTrue(inj.variables().isEmpty());
    }

    @Test
    void usesGlobalSecurityWhenOperationSecurityNull() {
        DefaultSecuritySchemeResolver resolver = new DefaultSecuritySchemeResolver(factory);
        Operation op = new Operation();

        OpenAPI api = new OpenAPI();
        api.setSecurity(List.of(new SecurityRequirement().addList("bearer")));

        SecurityScheme scheme = new SecurityScheme();
        api.setComponents(new Components());
        api.getComponents().setSecuritySchemes(Map.of("bearer", scheme));

        when(factory.resolve(scheme)).thenReturn(injector);
        when(injector.inject(eq(scheme), eq("bearer"))).thenReturn(
                new SecurityInjection(List.of(new HttpHeader("Authorization", "Bearer x")),
                        List.of(), List.of(new EnvironmentVariable("v", "p"))));

        SecurityInjection inj = resolver.resolve(op, api);

        assertEquals(1, inj.headers().size());
        assertEquals("Authorization", inj.headers().get(0).name());
        assertEquals(1, inj.variables().size());
    }

    @Test
    void prefersOperationSecurityOverGlobal() {
        DefaultSecuritySchemeResolver resolver = new DefaultSecuritySchemeResolver(factory);

        OpenAPI api = new OpenAPI();
        api.setSecurity(List.of(new SecurityRequirement().addList("global")));

        Operation op = new Operation();
        op.setSecurity(List.of(new SecurityRequirement().addList("opLevel")));

        SecurityScheme scheme = new SecurityScheme();
        api.setComponents(new Components());
        api.getComponents().setSecuritySchemes(Map.of("opLevel", scheme));
        when(factory.resolve(scheme)).thenReturn(injector);
        when(injector.inject(scheme, "opLevel")).thenReturn(
                new SecurityInjection(List.of(new HttpHeader("h", "v")), List.of(), List.of()));

        SecurityInjection inj = resolver.resolve(op, api);

        assertEquals(1, inj.headers().size());
        assertEquals("h", inj.headers().get(0).name());
    }

    @Test
    void skipsUndefinedSecuritySchemes() {
        DefaultSecuritySchemeResolver resolver = new DefaultSecuritySchemeResolver(factory);
        Operation op = new Operation();
        op.setSecurity(List.of(new SecurityRequirement().addList("notDefined")));
        OpenAPI api = new OpenAPI();
        api.setComponents(new Components());
        api.getComponents().setSecuritySchemes(Map.of());

        SecurityInjection inj = resolver.resolve(op, api);

        assertTrue(inj.headers().isEmpty());
    }

    @Test
    void returnsEmptyWhenOperationSecurityEmptyList() {
        DefaultSecuritySchemeResolver resolver = new DefaultSecuritySchemeResolver(factory);
        Operation op = new Operation();
        op.setSecurity(List.of());
        OpenAPI api = new OpenAPI();

        SecurityInjection inj = resolver.resolve(op, api);

        assertTrue(inj.headers().isEmpty());
    }
}
