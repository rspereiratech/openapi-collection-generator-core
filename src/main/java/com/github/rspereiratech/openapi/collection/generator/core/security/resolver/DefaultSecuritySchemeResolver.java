package com.github.rspereiratech.openapi.collection.generator.core.security.resolver;

import com.github.rspereiratech.openapi.collection.generator.core.security.factory.SecurityInjectorFactory;
import com.github.rspereiratech.openapi.collection.generator.core.security.model.SecurityInjection;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.security.SecurityRequirement;

import java.util.List;
import java.util.Optional;

/**
 * Default implementation of {@link SecuritySchemeResolver} that resolves the first
 * security requirement alternative from an operation (or the global security list)
 * and delegates injection to a {@link SecurityInjectorFactory}.
 */
public class DefaultSecuritySchemeResolver implements SecuritySchemeResolver {

    /**
     * Factory used to obtain the appropriate injector for each security scheme.
     */
    private final SecurityInjectorFactory factory;

    /**
     * Creates a resolver backed by the given injector factory.
     *
     * @param factory the factory used to obtain injectors for each security scheme
     */
    public DefaultSecuritySchemeResolver(SecurityInjectorFactory factory) {
        this.factory = factory;
    }

    @Override
    public SecurityInjection resolve(Operation op, OpenAPI openApi) {
        List<SecurityRequirement> reqs = op.getSecurity() != null ? op.getSecurity() : Optional.ofNullable(openApi.getSecurity()).orElse(List.of());
        if (reqs.isEmpty()) {
            return new SecurityInjection();
        }

        List<SecurityInjection> injections = reqs.get(0).keySet().stream()
                .map(s -> resolveScheme(s, openApi))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
        return SecurityInjection.merge(injections);
    }

    /**
     * Looks up a named security scheme in the OpenAPI components and produces its injection.
     *
     * @param name    the security scheme name
     * @param openApi the OpenAPI document
     * @return the injection, or empty if the scheme is not defined
     */
    private Optional<SecurityInjection> resolveScheme(String name, OpenAPI openApi) {
        return Optional.ofNullable(openApi.getComponents())
                .map(Components::getSecuritySchemes)
                .map(m -> m.get(name))
                .map(s -> factory.resolve(s).inject(s, name));
    }
}
