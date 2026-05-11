package io.github.rspereiratech.openapi.collection.generator.core.security.model;

import java.util.List;

/**
 * Aggregated result of security scheme injection containing headers, query parameters,
 * and environment variables to apply to a request or collection.
 *
 * @param headers     the HTTP headers to inject
 * @param queryParams the query parameters to inject
 * @param variables   the environment variables required by the injected placeholders
 */
public record SecurityInjection(List<HttpHeader> headers, List<HttpQueryParam> queryParams,
                                List<EnvironmentVariable> variables) {

    /**
     * Creates an empty injection with no headers, query parameters, or variables.
     */
    public SecurityInjection() {
        this(List.of(), List.of(), List.of());
    }

    /**
     * Merges multiple injections into a single one by concatenating all their
     * headers, query parameters, and variables.
     *
     * @param list the injections to merge
     * @return a merged {@link SecurityInjection}
     */
    public static SecurityInjection merge(List<SecurityInjection> list) {
        var headers = list.stream().flatMap(i -> i.headers().stream()).toList();
        var queryParams = list.stream().flatMap(i -> i.queryParams().stream()).toList();
        var variables = list.stream().flatMap(i -> i.variables().stream()).toList();
        return new SecurityInjection(headers, queryParams, variables);
    }
}
