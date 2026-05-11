package io.github.rspereiratech.openapi.collection.generator.core.security.factory;

import io.github.rspereiratech.openapi.collection.generator.core.security.injector.ApiKeyHeaderSecurityInjector;
import io.github.rspereiratech.openapi.collection.generator.core.security.injector.BearerSecurityInjector;
import io.github.rspereiratech.openapi.collection.generator.core.security.injector.NoOpSecurityInjector;
import io.github.rspereiratech.openapi.collection.generator.core.security.injector.OAuth2SecurityInjector;
import io.github.rspereiratech.openapi.collection.generator.core.security.injector.SecurityInjector;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultSecurityInjectorFactoryTest {

    @Test
    void defaultFactoryResolvesBearer() {
        DefaultSecurityInjectorFactory factory = new DefaultSecurityInjectorFactory();
        SecurityScheme s = new SecurityScheme();
        s.setType(SecurityScheme.Type.HTTP);
        s.setScheme("bearer");

        SecurityInjector inj = factory.resolve(s);

        assertInstanceOf(BearerSecurityInjector.class, inj);
    }

    @Test
    void defaultFactoryResolvesApiKeyHeader() {
        DefaultSecurityInjectorFactory factory = new DefaultSecurityInjectorFactory();
        SecurityScheme s = new SecurityScheme();
        s.setType(SecurityScheme.Type.APIKEY);
        s.setIn(SecurityScheme.In.HEADER);

        SecurityInjector inj = factory.resolve(s);

        assertInstanceOf(ApiKeyHeaderSecurityInjector.class, inj);
    }

    @Test
    void defaultFactoryFallsBackToNoOpWhenNoMatchSchemeMismatch() {
        DefaultSecurityInjectorFactory factory = new DefaultSecurityInjectorFactory();
        SecurityScheme s = new SecurityScheme();
        s.setType(SecurityScheme.Type.HTTP);
        s.setScheme("digest"); // not bearer/basic

        SecurityInjector inj = factory.resolve(s);

        // Should still match NoOpSecurityInjector (last in list, supports==true) or be a NoOp instance
        assertInstanceOf(NoOpSecurityInjector.class, inj);
    }

    @Test
    void defaultFactoryResolvesOAuth2() {
        DefaultSecurityInjectorFactory factory = new DefaultSecurityInjectorFactory();
        SecurityScheme s = new SecurityScheme();
        s.setType(SecurityScheme.Type.OAUTH2);

        assertInstanceOf(OAuth2SecurityInjector.class, factory.resolve(s));
    }

    @Test
    void customFactoryReturnsFirstSupportingInjector() {
        SecurityInjector a = mock(SecurityInjector.class);
        SecurityInjector b = mock(SecurityInjector.class);
        SecurityScheme s = new SecurityScheme();
        when(a.supports(s)).thenReturn(false);
        when(b.supports(s)).thenReturn(true);

        DefaultSecurityInjectorFactory factory = new DefaultSecurityInjectorFactory(List.of(a, b));

        assertSame(b, factory.resolve(s));
    }

    @Test
    void customFactoryFallsBackToNoOpWhenEmpty() {
        DefaultSecurityInjectorFactory factory = new DefaultSecurityInjectorFactory(List.of());
        SecurityScheme s = new SecurityScheme();

        assertInstanceOf(NoOpSecurityInjector.class, factory.resolve(s));
    }
}
