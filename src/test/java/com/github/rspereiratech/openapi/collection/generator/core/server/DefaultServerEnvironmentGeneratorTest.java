package com.github.rspereiratech.openapi.collection.generator.core.server;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultServerEnvironmentGeneratorTest {

    private final DefaultServerEnvironmentGenerator generator = new DefaultServerEnvironmentGenerator();

    @Test
    void generatesLocalhostEnvWhenNoServersDefined() {
        OpenAPI api = new OpenAPI();

        List<ServerEnvironment> envs = generator.generate(api, "MyApi");

        assertEquals(1, envs.size());
        ServerEnvironment e = envs.get(0);
        assertEquals("Local", e.name());
        assertEquals("http://localhost:8080", e.baseUrl());
        assertTrue(e.fileName().contains("MyApi"));
        assertTrue(e.fileName().endsWith(".environment.json"));
    }

    @Test
    void generatesLocalhostEnvWhenServersListEmpty() {
        OpenAPI api = new OpenAPI();
        api.setServers(List.of());

        List<ServerEnvironment> envs = generator.generate(api, "Api");

        assertEquals(1, envs.size());
        assertEquals("Local", envs.get(0).name());
    }

    @Test
    void usesServerDescriptionAsEnvName() {
        Server s = new Server();
        s.setUrl("https://api.example.com");
        s.setDescription("Production API");

        OpenAPI api = new OpenAPI();
        api.setServers(List.of(s));

        List<ServerEnvironment> envs = generator.generate(api, "X");

        assertEquals(1, envs.size());
        assertEquals("Production API", envs.get(0).name());
        assertEquals("https://api.example.com", envs.get(0).baseUrl());
    }

    @Test
    void usesPositionalDefaultsWhenNoDescription() {
        Server a = new Server();
        a.setUrl("u1");
        Server b = new Server();
        b.setUrl("u2");
        Server c = new Server();
        c.setUrl("u3");
        Server d = new Server();
        d.setUrl("u4");

        OpenAPI api = new OpenAPI();
        api.setServers(List.of(a, b, c, d));

        List<ServerEnvironment> envs = generator.generate(api, "n");

        assertEquals("Production", envs.get(0).name());
        assertEquals("Staging", envs.get(1).name());
        assertEquals("Development", envs.get(2).name());
        assertEquals("Environment 4", envs.get(3).name());
    }

    @Test
    void usesPositionalDefaultsWhenDescriptionBlank() {
        Server a = new Server();
        a.setUrl("u1");
        a.setDescription("  ");

        OpenAPI api = new OpenAPI();
        api.setServers(List.of(a));

        List<ServerEnvironment> envs = generator.generate(api, "n");

        assertEquals("Production", envs.get(0).name());
    }

    @Test
    void sanitizesFileNameSpecialCharacters() {
        Server s = new Server();
        s.setUrl("u");
        s.setDescription("My/Server!");

        OpenAPI api = new OpenAPI();
        api.setServers(List.of(s));

        List<ServerEnvironment> envs = generator.generate(api, "Hello World");

        String fn = envs.get(0).fileName();
        assertTrue(fn.contains("Hello_World"));
        assertTrue(fn.contains("My_Server_"));
        assertFalse(fn.contains("/"));
        assertFalse(fn.contains("!"));
        assertFalse(fn.contains(" "));
    }
}
