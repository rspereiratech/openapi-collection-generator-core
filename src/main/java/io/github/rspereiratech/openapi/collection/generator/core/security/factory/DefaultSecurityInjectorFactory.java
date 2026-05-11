package io.github.rspereiratech.openapi.collection.generator.core.security.factory;

import io.github.rspereiratech.openapi.collection.generator.core.security.injector.ApiKeyCookieSecurityInjector;
import io.github.rspereiratech.openapi.collection.generator.core.security.injector.ApiKeyHeaderSecurityInjector;
import io.github.rspereiratech.openapi.collection.generator.core.security.injector.ApiKeyQuerySecurityInjector;
import io.github.rspereiratech.openapi.collection.generator.core.security.injector.BasicAuthSecurityInjector;
import io.github.rspereiratech.openapi.collection.generator.core.security.injector.BearerSecurityInjector;
import io.github.rspereiratech.openapi.collection.generator.core.security.injector.NoOpSecurityInjector;
import io.github.rspereiratech.openapi.collection.generator.core.security.injector.OAuth2SecurityInjector;
import io.github.rspereiratech.openapi.collection.generator.core.security.injector.SecurityInjector;
import io.swagger.v3.oas.models.security.SecurityScheme;

import java.util.List;

/**
 * Default implementation of {@link SecurityInjectorFactory} that iterates over a list
 * of registered {@link SecurityInjector} instances and returns the first one that supports
 * the given scheme, falling back to {@link NoOpSecurityInjector}.
 */
public class DefaultSecurityInjectorFactory implements SecurityInjectorFactory {

    /**
     * Registered injectors evaluated in order to find a match for each security scheme.
     */
    private final List<SecurityInjector> injectors;

    /**
     * Creates a factory with the default set of injectors covering Bearer, Basic,
     * API key (header/query/cookie), and OAuth2 schemes.
     */
    public DefaultSecurityInjectorFactory() {
        this.injectors = List.of(
                new BearerSecurityInjector(),
                new BasicAuthSecurityInjector(),
                new ApiKeyHeaderSecurityInjector(),
                new ApiKeyQuerySecurityInjector(),
                new ApiKeyCookieSecurityInjector(),
                new OAuth2SecurityInjector(),
                new NoOpSecurityInjector());
    }

    /**
     * Creates a factory with a custom list of injectors.
     *
     * @param injectors the ordered list of injectors to evaluate
     */
    public DefaultSecurityInjectorFactory(List<SecurityInjector> injectors) {
        this.injectors = injectors;
    }

    @Override
    public SecurityInjector resolve(SecurityScheme scheme) {
        return injectors.stream()
                .filter(i -> i.supports(scheme))
                .findFirst()
                .orElseGet(NoOpSecurityInjector::new);
    }
}
