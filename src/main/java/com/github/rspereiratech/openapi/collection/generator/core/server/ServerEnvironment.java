package com.github.rspereiratech.openapi.collection.generator.core.server;

/**
 * Represents a server environment derived from an OpenAPI server definition.
 *
 * @param name     the human-readable environment name (e.g. "Production")
 * @param baseUrl  the server base URL
 * @param fileName the generated environment file name
 */
public record ServerEnvironment(String name, String baseUrl, String fileName) {
}
