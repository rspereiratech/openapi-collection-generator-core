package io.github.rspereiratech.openapi.collection.generator.core.security.applier;

import io.github.rspereiratech.openapi.collection.generator.core.security.model.EnvironmentVariable;
import io.github.rspereiratech.openapi.collection.generator.core.security.model.SecurityInjection;
import io.github.rspereiratech.openapi.collection.generator.core.security.resolver.SecuritySchemeResolver;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultSecurityApplierTest {

    private final SecuritySchemeResolver resolver = mock(SecuritySchemeResolver.class);
    private final DefaultSecurityApplier applier = new DefaultSecurityApplier(resolver);

    @Test
    void applyDelegatesToResolver() {
        Operation op = new Operation();
        OpenAPI api = new OpenAPI();
        SecurityInjection expected = new SecurityInjection();
        when(resolver.resolve(op, api)).thenReturn(expected);

        assertSame(expected, applier.apply(op, api));
    }

    @Test
    void applyGlobalReturnsEmptyWhenPathsNull() {
        OpenAPI api = new OpenAPI();

        SecurityInjection inj = applier.applyGlobal(api);

        assertTrue(inj.headers().isEmpty());
        assertTrue(inj.variables().isEmpty());
    }

    @Test
    void applyGlobalAggregatesUniqueVariables() {
        OpenAPI api = new OpenAPI();
        Paths paths = new Paths();
        PathItem p1 = new PathItem();
        p1.setGet(new Operation());
        PathItem p2 = new PathItem();
        p2.setPost(new Operation());
        paths.addPathItem("/a", p1);
        paths.addPathItem("/b", p2);
        api.setPaths(paths);

        SecurityInjection i1 = new SecurityInjection(List.of(), List.of(),
                List.of(new EnvironmentVariable("token", "<v>")));
        SecurityInjection i2 = new SecurityInjection(List.of(), List.of(),
                List.of(new EnvironmentVariable("token", "<v>"),
                        new EnvironmentVariable("other", "<o>")));

        when(resolver.resolve(any(), any())).thenReturn(i1, i2);

        SecurityInjection inj = applier.applyGlobal(api);

        // headers/queryParams should be empty for global
        assertTrue(inj.headers().isEmpty());
        assertTrue(inj.queryParams().isEmpty());
        // unique variable names: token + other
        assertEquals(2, inj.variables().size());
        List<String> names = inj.variables().stream().map(EnvironmentVariable::name).toList();
        assertTrue(names.contains("token"));
        assertTrue(names.contains("other"));
    }
}
