package io.github.rspereiratech.openapi.collection.generator.core.security.injector;

import io.github.rspereiratech.openapi.collection.generator.core.security.model.SecurityInjection;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * A no-operation injector that always returns an empty {@link SecurityInjection}.
 * Used as a fallback when no specific injector matches a security scheme.
 */
public class NoOpSecurityInjector implements SecurityInjector {

    @Override
    public SecurityInjection inject(SecurityScheme s, String n) {
        return new SecurityInjection();
    }

    @Override
    public boolean supports(SecurityScheme s) {
        return true;
    }
}
