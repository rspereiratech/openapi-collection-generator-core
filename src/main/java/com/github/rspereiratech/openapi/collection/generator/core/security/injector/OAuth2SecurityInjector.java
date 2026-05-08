package com.github.rspereiratech.openapi.collection.generator.core.security.injector;

import com.github.rspereiratech.openapi.collection.generator.core.security.model.EnvironmentVariable;
import com.github.rspereiratech.openapi.collection.generator.core.security.model.HttpHeader;
import com.github.rspereiratech.openapi.collection.generator.core.security.model.SecurityInjection;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.SecurityScheme;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Injects an {@code Authorization: Bearer} header for OAuth2 security schemes.
 * Generates environment variables for access token, token URLs, and scopes
 * based on the configured OAuth2 flows.
 */
public class OAuth2SecurityInjector implements SecurityInjector {

    @Override
    public boolean supports(SecurityScheme s) {
        return SecurityScheme.Type.OAUTH2.equals(s.getType());
    }

    @Override
    public SecurityInjection inject(SecurityScheme s, String name) {
        String tokenVar = name + "AccessToken";
        List<EnvironmentVariable> vars = new ArrayList<>();
        vars.add(new EnvironmentVariable(tokenVar, "<oauth2-access-token>"));
        if (s.getFlows() != null) {
            addFlowVars(s.getFlows().getClientCredentials(), name, "clientCredentials", vars);
            addFlowVars(s.getFlows().getAuthorizationCode(), name, "authCode", vars);
            addFlowVars(s.getFlows().getPassword(),          name, "password",   vars);
        }
        return new SecurityInjection(
                List.of(new HttpHeader("Authorization", "Bearer {{%s}}".formatted(tokenVar))),
                List.of(), vars);
    }

    /**
     * Adds environment variables (token URL, scopes) for a specific OAuth2 flow.
     *
     * @param flow     the OAuth2 flow, or {@code null} if not defined
     * @param name     the security scheme name used as a variable prefix
     * @param flowName the flow type identifier (e.g. "clientCredentials")
     * @param vars     the list to append new variables to
     */
    private void addFlowVars(OAuthFlow flow, String name, String flowName, List<EnvironmentVariable> vars) {
        if (flow == null) return;
        vars.add(new EnvironmentVariable(name + cap(flowName) + "TokenUrl",
                Optional.ofNullable(flow.getTokenUrl()).orElse("<token-url>")));
        if (flow.getScopes() != null && !flow.getScopes().isEmpty()) {
            vars.add(new EnvironmentVariable(name + "Scopes", String.join(" ", flow.getScopes().keySet())));
        }
    }

    /**
     * Capitalizes the first character of the given string.
     *
     * @param s the string to capitalize
     * @return the capitalized string, or the original if empty
     */
    private String cap(String s) {
        return s.isEmpty() ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
