package io.github.rspereiratech.openapi.collection.generator.core.security.injector;

import io.github.rspereiratech.openapi.collection.generator.core.security.model.EnvironmentVariable;
import io.github.rspereiratech.openapi.collection.generator.core.security.model.HttpHeader;
import io.github.rspereiratech.openapi.collection.generator.core.security.model.SecurityInjection;
import io.swagger.v3.oas.models.security.SecurityScheme;

import java.util.List;

/**
 * Injects an API key via a {@code Cookie} header for {@code apiKey/cookie} security schemes.
 * The cookie name is taken from the scheme definition.
 */
public class ApiKeyCookieSecurityInjector implements SecurityInjector {

    @Override
    public boolean supports(SecurityScheme s) {
        return SecurityScheme.Type.APIKEY.equals(s.getType())
                && SecurityScheme.In.COOKIE.equals(s.getIn());
    }

    @Override
    public SecurityInjection inject(SecurityScheme s, String name) {
        String var = name + "Cookie";
        return new SecurityInjection(
                List.of(new HttpHeader("Cookie", "%s={{%s}}".formatted(s.getName(), var))),
                List.of(),
                List.of(new EnvironmentVariable(var, "<your-cookie-value>")));
    }
}
