package com.github.rspereiratech.openapi.collection.generator.core.generator;

/**
 * Represents an extra file produced during collection generation (e.g. an environment file).
 *
 * @param fileName the target file name (without directory path)
 * @param content  the full textual content of the file
 */
public record AdditionalFile(String fileName, String content) {}
