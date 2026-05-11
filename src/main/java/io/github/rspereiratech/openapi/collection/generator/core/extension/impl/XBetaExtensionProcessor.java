package io.github.rspereiratech.openapi.collection.generator.core.extension.impl;

import io.github.rspereiratech.openapi.collection.generator.core.extension.ExtensionContext;
import io.github.rspereiratech.openapi.collection.generator.core.extension.ExtensionProcessor;
import io.github.rspereiratech.openapi.collection.generator.core.extension.ExtensionResult;

/**
 * Handles the {@code x-beta} vendor extension by appending "(Beta)" to the
 * operation name and adding a beta-warning note to the description.
 */
public class XBetaExtensionProcessor implements ExtensionProcessor {

    @Override
    public boolean supports(String k) {
        return "x-beta".equals(k);
    }

    @Override
    public ExtensionResult process(String k, Object v, ExtensionContext c) {
        if (!Boolean.TRUE.equals(v)) return ExtensionResult.noChange();
        return new ExtensionResult(c.currentName() + " (Beta)", "🧪 Beta — may change without notice.");
    }
}
