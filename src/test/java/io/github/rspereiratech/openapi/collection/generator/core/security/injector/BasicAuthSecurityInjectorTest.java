package io.github.rspereiratech.openapi.collection.generator.core.security.injector;

import io.github.rspereiratech.openapi.collection.generator.core.security.model.SecurityInjection;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BasicAuthSecurityInjectorTest {

    private final BasicAuthSecurityInjector injector = new BasicAuthSecurityInjector();

    @Test
    void supportsHttpBasic() {
        SecurityScheme s = new SecurityScheme();
        s.setType(SecurityScheme.Type.HTTP);
        s.setScheme("basic");

        assertTrue(injector.supports(s));
    }

    @Test
    void supportsCaseInsensitive() {
        SecurityScheme s = new SecurityScheme();
        s.setType(SecurityScheme.Type.HTTP);
        s.setScheme("BASIC");

        assertTrue(injector.supports(s));
    }

    @Test
    void doesNotSupportBearer() {
        SecurityScheme s = new SecurityScheme();
        s.setType(SecurityScheme.Type.HTTP);
        s.setScheme("bearer");

        assertFalse(injector.supports(s));
    }

    @Test
    void doesNotSupportOAuth2() {
        SecurityScheme s = new SecurityScheme();
        s.setType(SecurityScheme.Type.OAUTH2);

        assertFalse(injector.supports(s));
    }

    @Test
    void injectsAuthHeaderAndTwoEnvVars() {
        SecurityScheme s = new SecurityScheme();
        s.setType(SecurityScheme.Type.HTTP);
        s.setScheme("basic");

        SecurityInjection inj = injector.inject(s, "auth");

        assertEquals(1, inj.headers().size());
        assertEquals("Authorization", inj.headers().get(0).name());
        assertEquals("Basic {{authUsername}}:{{authPassword}}", inj.headers().get(0).value());
        assertTrue(inj.queryParams().isEmpty());
        assertEquals(2, inj.variables().size());
        assertEquals("authUsername", inj.variables().get(0).name());
        assertEquals("<username>", inj.variables().get(0).placeholder());
        assertEquals("authPassword", inj.variables().get(1).name());
        assertEquals("<password>", inj.variables().get(1).placeholder());
    }
}
