package com.github.rspereiratech.openapi.collection.generator.core.extension;

/**
 * Processes a single OpenAPI vendor extension (e.g. {@code x-summary}, {@code x-beta})
 * and produces an {@link ExtensionResult} that may modify the operation name or description.
 */
public interface ExtensionProcessor {

    /**
     * Determines whether this processor handles the given extension key.
     *
     * @param extensionKey the vendor extension key (e.g. {@code x-internal})
     * @return {@code true} if this processor supports the key
     */
    boolean supports(String extensionKey);

    /**
     * Processes the extension and returns any name or description modifications.
     *
     * @param key     the vendor extension key
     * @param value   the extension value
     * @param context the current operation context
     * @return an {@link ExtensionResult} with optional overrides
     */
    ExtensionResult process(String key, Object value, ExtensionContext context);
}
