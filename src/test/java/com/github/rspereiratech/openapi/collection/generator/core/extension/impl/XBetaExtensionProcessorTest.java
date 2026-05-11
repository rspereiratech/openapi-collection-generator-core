package com.github.rspereiratech.openapi.collection.generator.core.extension.impl;

import com.github.rspereiratech.openapi.collection.generator.core.extension.ExtensionContext;
import com.github.rspereiratech.openapi.collection.generator.core.extension.ExtensionResult;
import io.swagger.v3.oas.models.Operation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class XBetaExtensionProcessorTest {

    private final Operation operation = mock(Operation.class);
    private final XBetaExtensionProcessor processor = new XBetaExtensionProcessor();

    @Test
    void supportsXBetaKeyOnly() {
        assertTrue(processor.supports("x-beta"));
        assertFalse(processor.supports("x-other"));
        assertFalse(processor.supports("x-Beta"));
    }

    @Test
    void appendsBetaTagToNameWhenValueTrue() {
        ExtensionContext ctx = new ExtensionContext("/p", "GET", "Get Pet", "desc", operation);

        ExtensionResult r = processor.process("x-beta", Boolean.TRUE, ctx);

        assertEquals("Get Pet (Beta)", r.nameOverride());
        assertNotNull(r.descriptionAppend());
        assertTrue(r.descriptionAppend().contains("Beta"));
    }

    @Test
    void returnsNoChangeWhenValueFalse() {
        ExtensionContext ctx = new ExtensionContext("/p", "GET", "Get Pet", "desc", operation);

        ExtensionResult r = processor.process("x-beta", Boolean.FALSE, ctx);

        assertNull(r.nameOverride());
        assertNull(r.descriptionAppend());
    }

    @Test
    void returnsNoChangeWhenValueNull() {
        ExtensionContext ctx = new ExtensionContext("/p", "GET", "Get Pet", "desc", operation);

        ExtensionResult r = processor.process("x-beta", null, ctx);

        assertNull(r.nameOverride());
        assertNull(r.descriptionAppend());
    }
}
