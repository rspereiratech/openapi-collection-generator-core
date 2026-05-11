package io.github.rspereiratech.openapi.collection.generator.core.extension.impl;

import io.github.rspereiratech.openapi.collection.generator.core.extension.ExtensionContext;
import io.github.rspereiratech.openapi.collection.generator.core.extension.ExtensionProcessor;
import io.github.rspereiratech.openapi.collection.generator.core.extension.ExtensionResult;

/**
 * Handles the {@code x-deprecated-since} vendor extension by appending a
 * "Deprecated since" note with the version to the description.
 */
public class XDeprecatedSinceExtensionProcessor implements ExtensionProcessor {

    @Override
    public boolean supports(String k) {
        return "x-deprecated-since".equals(k);
    }

    @Override
    public ExtensionResult process(String k, Object v, ExtensionContext c) {
        return v == null
                ? ExtensionResult.noChange()
                : new ExtensionResult(null, "Deprecated since: **" + v + "**");
    }
}
