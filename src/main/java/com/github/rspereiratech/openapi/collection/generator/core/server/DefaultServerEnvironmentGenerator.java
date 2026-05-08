package com.github.rspereiratech.openapi.collection.generator.core.server;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Default {@link ServerEnvironmentGenerator} that derives environment names from
 * server descriptions (falling back to positional defaults such as "Production", "Staging")
 * and creates a localhost environment when no servers are defined.
 */
public class DefaultServerEnvironmentGenerator implements ServerEnvironmentGenerator {

    @Override
    public List<ServerEnvironment> generate(OpenAPI openApi, String name) {
        List<Server> servers = Optional.ofNullable(openApi.getServers())
                .filter(s -> !s.isEmpty())
                .orElse(List.of(defaultServer()));
        List<ServerEnvironment> envs = new ArrayList<>();
        for (int i = 0; i < servers.size(); i++) {
            String envName = resolveServerName(servers.get(i), i);
            String safe = envName.replaceAll("[^a-zA-Z0-9_\\-]", "_");
            envs.add(new ServerEnvironment(
                    envName,
                    servers.get(i).getUrl(),
                    name.replaceAll("[^a-zA-Z0-9_\\-]", "_") + "." + safe + ".environment.json"));
        }
        return envs;
    }

    private String resolveServerName(Server s, int i) {
        if (s.getDescription() != null && !s.getDescription().isBlank()) return s.getDescription();
        return switch (i) {
            case 0 -> "Production";
            case 1 -> "Staging";
            case 2 -> "Development";
            default -> "Environment " + (i + 1);
        };
    }

    private Server defaultServer() {
        var s = new Server();
        s.setUrl("http://localhost:8080");
        s.setDescription("Local");
        return s;
    }
}
