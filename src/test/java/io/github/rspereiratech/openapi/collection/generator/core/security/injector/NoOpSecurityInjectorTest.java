package io.github.rspereiratech.openapi.collection.generator.core.security.injector;

import io.github.rspereiratech.openapi.collection.generator.core.security.model.SecurityInjection;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class NoOpSecurityInjectorTest {

    private final NoOpSecurityInjector injector = new NoOpSecurityInjector();

    @Test
    void supportsAllSchemes() {
        assertTrue(injector.supports(mock(SecurityScheme.class)));
        assertTrue(injector.supports(new SecurityScheme()));
    }

    @Test
    void injectReturnsEmptyInjection() {
        SecurityInjection inj = injector.inject(new SecurityScheme(), "any");

        assertNotNull(inj);
        assertTrue(inj.headers().isEmpty());
        assertTrue(inj.queryParams().isEmpty());
        assertTrue(inj.variables().isEmpty());
    }
}
