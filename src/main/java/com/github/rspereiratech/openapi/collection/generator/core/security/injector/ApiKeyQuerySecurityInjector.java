package com.github.rspereiratech.openapi.collection.generator.core.security.injector;

import com.github.rspereiratech.openapi.collection.generator.core.security.model.EnvironmentVariable;
import com.github.rspereiratech.openapi.collection.generator.core.security.model.HttpQueryParam;
import com.github.rspereiratech.openapi.collection.generator.core.security.model.SecurityInjection;
import io.swagger.v3.oas.models.security.SecurityScheme;

import java.util.List;

/**
 * Injects an API key as a query parameter for {@code apiKey/query} security schemes.
 * The parameter name is taken from the scheme definition.
 */
public class ApiKeyQuerySecurityInjector implements SecurityInjector {

    @Override
    public boolean supports(SecurityScheme s) {
        return SecurityScheme.Type.APIKEY.equals(s.getType())
                && SecurityScheme.In.QUERY.equals(s.getIn());
    }

    @Override
    public SecurityInjection inject(SecurityScheme s, String name) {
        String var = name + "Value";
        return new SecurityInjection(
                List.of(),
                List.of(new HttpQueryParam(s.getName(), "{{%s}}".formatted(var))),
                List.of(new EnvironmentVariable(var, "<your-api-key>")));
    }
}
