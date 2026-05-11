package io.github.rspereiratech.openapi.collection.generator.core.security.injector;

import io.github.rspereiratech.openapi.collection.generator.core.security.model.EnvironmentVariable;
import io.github.rspereiratech.openapi.collection.generator.core.security.model.HttpHeader;
import io.github.rspereiratech.openapi.collection.generator.core.security.model.SecurityInjection;
import io.swagger.v3.oas.models.security.SecurityScheme;

import java.util.List;

/**
 * Injects an {@code Authorization: Basic} header for HTTP Basic authentication schemes.
 * Creates environment variables for username and password.
 */
public class BasicAuthSecurityInjector implements SecurityInjector {

    @Override
    public boolean supports(SecurityScheme s) {
        return SecurityScheme.Type.HTTP.equals(s.getType())
                && "basic".equalsIgnoreCase(s.getScheme());
    }

    @Override
    public SecurityInjection inject(SecurityScheme s, String name) {
        String u = name + "Username", p = name + "Password";
        return new SecurityInjection(
                List.of(new HttpHeader("Authorization", "Basic {{%s}}:{{%s}}".formatted(u, p))),
                List.of(),
                List.of(new EnvironmentVariable(u, "<username>"), new EnvironmentVariable(p, "<password>")));
    }
}
