package io.github.rspereiratech.openapi.collection.generator.core.security.injector;

import io.github.rspereiratech.openapi.collection.generator.core.security.model.EnvironmentVariable;
import io.github.rspereiratech.openapi.collection.generator.core.security.model.HttpHeader;
import io.github.rspereiratech.openapi.collection.generator.core.security.model.SecurityInjection;
import io.swagger.v3.oas.models.security.SecurityScheme;

import java.util.List;

/**
 * Injects an API key as an HTTP header for {@code apiKey/header} security schemes.
 * The header name is taken from the scheme definition.
 */
public class ApiKeyHeaderSecurityInjector implements SecurityInjector {

    @Override
    public boolean supports(SecurityScheme s) {
        return SecurityScheme.Type.APIKEY.equals(s.getType())
                && SecurityScheme.In.HEADER.equals(s.getIn());
    }

    @Override
    public SecurityInjection inject(SecurityScheme s, String name) {
        String var = name + "Value";
        return new SecurityInjection(
                List.of(new HttpHeader(s.getName(), "{{%s}}".formatted(var))),
                List.of(),
                List.of(new EnvironmentVariable(var, "<your-api-key>")));
    }
}
