package io.github.rspereiratech.openapi.collection.generator.core.security.factory;

import io.github.rspereiratech.openapi.collection.generator.core.security.injector.SecurityInjector;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * Factory that selects the appropriate {@link SecurityInjector} for a given
 * OpenAPI {@link SecurityScheme}.
 */
public interface SecurityInjectorFactory {

    /**
     * Returns a {@link SecurityInjector} capable of handling the given security scheme.
     *
     * @param scheme the OpenAPI security scheme
     * @return a matching injector
     */
    SecurityInjector resolve(SecurityScheme scheme);
}
