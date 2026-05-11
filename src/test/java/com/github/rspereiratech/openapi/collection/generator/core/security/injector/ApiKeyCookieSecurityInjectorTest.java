package com.github.rspereiratech.openapi.collection.generator.core.security.injector;

import com.github.rspereiratech.openapi.collection.generator.core.security.model.SecurityInjection;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApiKeyCookieSecurityInjectorTest {

    private final ApiKeyCookieSecurityInjector injector = new ApiKeyCookieSecurityInjector();

    @Test
    void supportsApiKeyCookie() {
        SecurityScheme s = new SecurityScheme();
        s.setType(SecurityScheme.Type.APIKEY);
        s.setIn(SecurityScheme.In.COOKIE);

        assertTrue(injector.supports(s));
    }

    @Test
    void doesNotSupportHeaderApiKey() {
        SecurityScheme s = new SecurityScheme();
        s.setType(SecurityScheme.Type.APIKEY);
        s.setIn(SecurityScheme.In.HEADER);

        assertFalse(injector.supports(s));
    }

    @Test
    void doesNotSupportNonApiKey() {
        SecurityScheme s = new SecurityScheme();
        s.setType(SecurityScheme.Type.HTTP);

        assertFalse(injector.supports(s));
    }

    @Test
    void injectsCookieHeaderAndEnvVar() {
        SecurityScheme s = new SecurityScheme();
        s.setType(SecurityScheme.Type.APIKEY);
        s.setIn(SecurityScheme.In.COOKIE);
        s.setName("session");

        SecurityInjection inj = injector.inject(s, "myAuth");

        assertEquals(1, inj.headers().size());
        assertEquals("Cookie", inj.headers().get(0).name());
        assertEquals("session={{myAuthCookie}}", inj.headers().get(0).value());
        assertTrue(inj.queryParams().isEmpty());
        assertEquals(1, inj.variables().size());
        assertEquals("myAuthCookie", inj.variables().get(0).name());
        assertEquals("<your-cookie-value>", inj.variables().get(0).placeholder());
    }
}
