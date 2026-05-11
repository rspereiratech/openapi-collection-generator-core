package com.github.rspereiratech.openapi.collection.generator.core.security.injector;

import com.github.rspereiratech.openapi.collection.generator.core.security.model.SecurityInjection;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApiKeyQuerySecurityInjectorTest {

    private final ApiKeyQuerySecurityInjector injector = new ApiKeyQuerySecurityInjector();

    @Test
    void supportsApiKeyQuery() {
        SecurityScheme s = new SecurityScheme();
        s.setType(SecurityScheme.Type.APIKEY);
        s.setIn(SecurityScheme.In.QUERY);

        assertTrue(injector.supports(s));
    }

    @Test
    void doesNotSupportHeader() {
        SecurityScheme s = new SecurityScheme();
        s.setType(SecurityScheme.Type.APIKEY);
        s.setIn(SecurityScheme.In.HEADER);

        assertFalse(injector.supports(s));
    }

    @Test
    void injectsQueryParamAndEnvVar() {
        SecurityScheme s = new SecurityScheme();
        s.setType(SecurityScheme.Type.APIKEY);
        s.setIn(SecurityScheme.In.QUERY);
        s.setName("apiKey");

        SecurityInjection inj = injector.inject(s, "auth");

        assertTrue(inj.headers().isEmpty());
        assertEquals(1, inj.queryParams().size());
        assertEquals("apiKey", inj.queryParams().get(0).name());
        assertEquals("{{authValue}}", inj.queryParams().get(0).value());
        assertEquals(1, inj.variables().size());
        assertEquals("authValue", inj.variables().get(0).name());
    }
}
