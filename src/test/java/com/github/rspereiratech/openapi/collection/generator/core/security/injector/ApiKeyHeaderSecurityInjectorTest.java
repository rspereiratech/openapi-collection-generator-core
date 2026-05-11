package com.github.rspereiratech.openapi.collection.generator.core.security.injector;

import com.github.rspereiratech.openapi.collection.generator.core.security.model.SecurityInjection;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApiKeyHeaderSecurityInjectorTest {

    private final ApiKeyHeaderSecurityInjector injector = new ApiKeyHeaderSecurityInjector();

    @Test
    void supportsApiKeyHeader() {
        SecurityScheme s = new SecurityScheme();
        s.setType(SecurityScheme.Type.APIKEY);
        s.setIn(SecurityScheme.In.HEADER);

        assertTrue(injector.supports(s));
    }

    @Test
    void doesNotSupportCookieApiKey() {
        SecurityScheme s = new SecurityScheme();
        s.setType(SecurityScheme.Type.APIKEY);
        s.setIn(SecurityScheme.In.COOKIE);

        assertFalse(injector.supports(s));
    }

    @Test
    void injectsHeaderAndEnvVar() {
        SecurityScheme s = new SecurityScheme();
        s.setType(SecurityScheme.Type.APIKEY);
        s.setIn(SecurityScheme.In.HEADER);
        s.setName("X-API-Key");

        SecurityInjection inj = injector.inject(s, "api");

        assertEquals(1, inj.headers().size());
        assertEquals("X-API-Key", inj.headers().get(0).name());
        assertEquals("{{apiValue}}", inj.headers().get(0).value());
        assertTrue(inj.queryParams().isEmpty());
        assertEquals(1, inj.variables().size());
        assertEquals("apiValue", inj.variables().get(0).name());
        assertEquals("<your-api-key>", inj.variables().get(0).placeholder());
    }
}
