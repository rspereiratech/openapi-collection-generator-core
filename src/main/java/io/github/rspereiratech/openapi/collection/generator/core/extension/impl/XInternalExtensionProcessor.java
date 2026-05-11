package io.github.rspereiratech.openapi.collection.generator.core.extension.impl;

import io.github.rspereiratech.openapi.collection.generator.core.extension.ExtensionContext;
import io.github.rspereiratech.openapi.collection.generator.core.extension.ExtensionProcessor;
import io.github.rspereiratech.openapi.collection.generator.core.extension.ExtensionResult;

/**
 * Handles the {@code x-internal} vendor extension by appending "(Internal)" to
 * the operation name and adding an internal-endpoint note to the description.
 */
public class XInternalExtensionProcessor implements ExtensionProcessor {

    @Override
    public boolean supports(String k) {
        return "x-internal".equals(k);
    }

    @Override
    public ExtensionResult process(String k, Object v, ExtensionContext c) {
        if (!Boolean.TRUE.equals(v)) return ExtensionResult.noChange();
        return new ExtensionResult(c.currentName() + " (Internal)", "🔒 Internal endpoint.");
    }
}
