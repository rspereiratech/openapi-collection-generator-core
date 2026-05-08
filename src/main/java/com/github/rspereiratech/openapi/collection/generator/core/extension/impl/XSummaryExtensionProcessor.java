package com.github.rspereiratech.openapi.collection.generator.core.extension.impl;

import com.github.rspereiratech.openapi.collection.generator.core.extension.ExtensionContext;
import com.github.rspereiratech.openapi.collection.generator.core.extension.ExtensionProcessor;
import com.github.rspereiratech.openapi.collection.generator.core.extension.ExtensionResult;

/**
 * Handles the {@code x-summary} vendor extension by using its value
 * as a name override for the operation.
 */
public class XSummaryExtensionProcessor implements ExtensionProcessor {

    @Override
    public boolean supports(String k) {
        return "x-summary".equals(k);
    }

    @Override
    public ExtensionResult process(String k, Object v, ExtensionContext c) {
        return (v instanceof String s && !s.isBlank())
                ? new ExtensionResult(s, null)
                : ExtensionResult.noChange();
    }
}
