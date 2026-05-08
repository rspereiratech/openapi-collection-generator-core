package com.github.rspereiratech.openapi.collection.generator.core.security.injector;

import com.github.rspereiratech.openapi.collection.generator.core.security.model.SecurityInjection;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * Contract for generating a {@link SecurityInjection} from an OpenAPI {@link SecurityScheme}.
 * Implementations handle a specific authentication type (e.g. Bearer, API key, OAuth2).
 */
public interface SecurityInjector {

    /**
     * Produces headers, query parameters, and environment variables for the given security scheme.
     *
     * @param scheme     the OpenAPI security scheme definition
     * @param schemeName the logical name of the security scheme
     * @return the resulting {@link SecurityInjection}
     */
    SecurityInjection inject(SecurityScheme scheme, String schemeName);

    /**
     * Returns {@code true} if this injector can handle the given security scheme.
     *
     * @param scheme the OpenAPI security scheme to test
     * @return {@code true} when this injector supports the scheme
     */
    boolean supports(SecurityScheme scheme);
}
