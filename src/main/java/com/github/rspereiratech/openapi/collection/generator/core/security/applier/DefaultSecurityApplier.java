package com.github.rspereiratech.openapi.collection.generator.core.security.applier;

import com.github.rspereiratech.openapi.collection.generator.core.security.model.EnvironmentVariable;
import com.github.rspereiratech.openapi.collection.generator.core.security.model.SecurityInjection;
import com.github.rspereiratech.openapi.collection.generator.core.security.resolver.SecuritySchemeResolver;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Default implementation of {@link SecurityApplier} that delegates operation-level
 * resolution to a {@link SecuritySchemeResolver} and aggregates unique environment
 * variables across all operations for the global scope.
 */
public class DefaultSecurityApplier implements SecurityApplier {

    /**
     * Resolver used to look up and inject security schemes from the OpenAPI spec.
     */
    private final SecuritySchemeResolver resolver;

    /**
     * Creates an applier backed by the given resolver.
     *
     * @param resolver the resolver used to process individual operations
     */
    public DefaultSecurityApplier(SecuritySchemeResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public SecurityInjection apply(Operation op, OpenAPI openApi) {
        return resolver.resolve(op, openApi);
    }

    @Override
    public SecurityInjection applyGlobal(OpenAPI openApi) {
        if (openApi.getPaths() == null) {
            return new SecurityInjection();
        }

        List<SecurityInjection> all = openApi.getPaths().values().stream()
                .flatMap(pi -> pi.readOperations().stream())
                .map(op -> resolver.resolve(op, openApi))
                .toList();

        List<EnvironmentVariable> unique = all.stream()
                .flatMap(i -> i.variables().stream())
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(EnvironmentVariable::name, v -> v, (a, b) -> a, LinkedHashMap::new),
                        m -> new ArrayList<>(m.values())));

        return new SecurityInjection(List.of(), List.of(), unique);
    }
}
