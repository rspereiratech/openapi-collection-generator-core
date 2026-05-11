package com.github.rspereiratech.openapi.collection.generator.core.security.injector;

import com.github.rspereiratech.openapi.collection.generator.core.security.model.EnvironmentVariable;
import com.github.rspereiratech.openapi.collection.generator.core.security.model.SecurityInjection;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OAuth2SecurityInjectorTest {

    private final OAuth2SecurityInjector injector = new OAuth2SecurityInjector();

    @Test
    void supportsOAuth2() {
        SecurityScheme s = new SecurityScheme();
        s.setType(SecurityScheme.Type.OAUTH2);

        assertTrue(injector.supports(s));
    }

    @Test
    void doesNotSupportNonOAuth2() {
        SecurityScheme s = new SecurityScheme();
        s.setType(SecurityScheme.Type.HTTP);

        assertFalse(injector.supports(s));
    }

    @Test
    void injectsBearerHeaderAndAccessTokenVar() {
        SecurityScheme s = new SecurityScheme();
        s.setType(SecurityScheme.Type.OAUTH2);

        SecurityInjection inj = injector.inject(s, "oauth");

        assertEquals(1, inj.headers().size());
        assertEquals("Authorization", inj.headers().get(0).name());
        assertEquals("Bearer {{oauthAccessToken}}", inj.headers().get(0).value());
        assertEquals(1, inj.variables().size());
        EnvironmentVariable v = inj.variables().get(0);
        assertEquals("oauthAccessToken", v.name());
        assertEquals("<oauth2-access-token>", v.placeholder());
    }

    @Test
    void addsClientCredentialsFlowVars() {
        SecurityScheme s = new SecurityScheme();
        s.setType(SecurityScheme.Type.OAUTH2);
        OAuthFlows flows = new OAuthFlows();
        OAuthFlow cc = new OAuthFlow();
        cc.setTokenUrl("https://example.com/token");
        Scopes scopes = new Scopes();
        scopes.put("read", "read scope");
        scopes.put("write", "write scope");
        cc.setScopes(scopes);
        flows.setClientCredentials(cc);
        s.setFlows(flows);

        SecurityInjection inj = injector.inject(s, "api");

        List<EnvironmentVariable> vars = inj.variables();
        assertTrue(vars.stream().anyMatch(v -> v.name().equals("apiAccessToken")));
        assertTrue(vars.stream().anyMatch(v -> v.name().equals("apiClientCredentialsTokenUrl")
                && v.placeholder().equals("https://example.com/token")));
        assertTrue(vars.stream().anyMatch(v -> v.name().equals("apiScopes")
                && v.placeholder().contains("read") && v.placeholder().contains("write")));
    }

    @Test
    void usesPlaceholderWhenTokenUrlNull() {
        SecurityScheme s = new SecurityScheme();
        s.setType(SecurityScheme.Type.OAUTH2);
        OAuthFlows flows = new OAuthFlows();
        OAuthFlow ac = new OAuthFlow();
        // no tokenUrl, no scopes
        flows.setAuthorizationCode(ac);
        s.setFlows(flows);

        SecurityInjection inj = injector.inject(s, "n");

        assertTrue(inj.variables().stream().anyMatch(v -> v.name().equals("nAuthCodeTokenUrl")
                && v.placeholder().equals("<token-url>")));
        assertFalse(inj.variables().stream().anyMatch(v -> v.name().equals("nScopes")));
    }

    @Test
    void handlesPasswordFlow() {
        SecurityScheme s = new SecurityScheme();
        s.setType(SecurityScheme.Type.OAUTH2);
        OAuthFlows flows = new OAuthFlows();
        OAuthFlow pwd = new OAuthFlow();
        pwd.setTokenUrl("https://t");
        flows.setPassword(pwd);
        s.setFlows(flows);

        SecurityInjection inj = injector.inject(s, "x");

        assertTrue(inj.variables().stream().anyMatch(v -> v.name().equals("xPasswordTokenUrl")));
    }
}
