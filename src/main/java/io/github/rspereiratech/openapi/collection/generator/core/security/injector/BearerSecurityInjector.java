package io.github.rspereiratech.openapi.collection.generator.core.security.injector;

import io.github.rspereiratech.openapi.collection.generator.core.security.model.EnvironmentVariable;
import io.github.rspereiratech.openapi.collection.generator.core.security.model.HttpHeader;
import io.github.rspereiratech.openapi.collection.generator.core.security.model.SecurityInjection;
import io.swagger.v3.oas.models.security.SecurityScheme;

import java.util.List;

/**
 * Injects an {@code Authorization: Bearer} header for HTTP Bearer security schemes.
 * Creates an environment variable for the bearer token value.
 */
public class BearerSecurityInjector implements SecurityInjector {

    @Override
    public boolean supports(SecurityScheme s) {
        return SecurityScheme.Type.HTTP.equals(s.getType())
                && "bearer".equalsIgnoreCase(s.getScheme());
    }

    @Override
    public SecurityInjection inject(SecurityScheme s, String name) {
        String var = name + "Token";
        return new SecurityInjection(
                List.of(new HttpHeader("Authorization", "Bearer {{%s}}".formatted(var))),
                List.of(),
                List.of(new EnvironmentVariable(var, "<your-bearer-token>")));
    }
}
