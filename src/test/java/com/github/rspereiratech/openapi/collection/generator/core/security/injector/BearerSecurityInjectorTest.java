package com.github.rspereiratech.openapi.collection.generator.core.security.injector;

import com.github.rspereiratech.openapi.collection.generator.core.security.model.SecurityInjection;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BearerSecurityInjectorTest {

    private final BearerSecurityInjector injector = new BearerSecurityInjector();

    @Test
    void supportsHttpBearer() {
        SecurityScheme s = new SecurityScheme();
        s.setType(SecurityScheme.Type.HTTP);
        s.setScheme("bearer");

        assertTrue(injector.supports(s));
    }

    @Test
    void supportsBearerCaseInsensitive() {
        SecurityScheme s = new SecurityScheme();
        s.setType(SecurityScheme.Type.HTTP);
        s.setScheme("Bearer");

        assertTrue(injector.supports(s));
    }

    @Test
    void doesNotSupportBasic() {
        SecurityScheme s = new SecurityScheme();
        s.setType(SecurityScheme.Type.HTTP);
        s.setScheme("basic");

        assertFalse(injector.supports(s));
    }

    @Test
    void injectsAuthHeaderAndTokenVar() {
        SecurityScheme s = new SecurityScheme();
        s.setType(SecurityScheme.Type.HTTP);
        s.setScheme("bearer");

        SecurityInjection inj = injector.inject(s, "myAuth");

        assertEquals(1, inj.headers().size());
        assertEquals("Authorization", inj.headers().get(0).name());
        assertEquals("Bearer {{myAuthToken}}", inj.headers().get(0).value());
        assertTrue(inj.queryParams().isEmpty());
        assertEquals(1, inj.variables().size());
        assertEquals("myAuthToken", inj.variables().get(0).name());
        assertEquals("<your-bearer-token>", inj.variables().get(0).placeholder());
    }
}
