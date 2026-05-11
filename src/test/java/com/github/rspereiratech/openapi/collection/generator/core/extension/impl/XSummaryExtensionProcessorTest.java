package com.github.rspereiratech.openapi.collection.generator.core.extension.impl;

import com.github.rspereiratech.openapi.collection.generator.core.extension.ExtensionContext;
import com.github.rspereiratech.openapi.collection.generator.core.extension.ExtensionResult;
import io.swagger.v3.oas.models.Operation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class XSummaryExtensionProcessorTest {

    private final Operation operation = mock(Operation.class);
    private final XSummaryExtensionProcessor processor = new XSummaryExtensionProcessor();

    @Test
    void supportsXSummaryOnly() {
        assertTrue(processor.supports("x-summary"));
        assertFalse(processor.supports("summary"));
    }

    @Test
    void replacesNameWithSummary() {
        ExtensionContext ctx = new ExtensionContext("/p", "GET", "old", "d", operation);

        ExtensionResult r = processor.process("x-summary", "New Summary", ctx);

        assertEquals("New Summary", r.nameOverride());
        assertNull(r.descriptionAppend());
    }

    @Test
    void noChangeForBlankString() {
        ExtensionContext ctx = new ExtensionContext("/p", "GET", "old", "d", operation);

        ExtensionResult r = processor.process("x-summary", "   ", ctx);

        assertNull(r.nameOverride());
        assertNull(r.descriptionAppend());
    }

    @Test
    void noChangeForNonString() {
        ExtensionContext ctx = new ExtensionContext("/p", "GET", "old", "d", operation);

        ExtensionResult r = processor.process("x-summary", 42, ctx);

        assertNull(r.nameOverride());
        assertNull(r.descriptionAppend());
    }

    @Test
    void noChangeForNull() {
        ExtensionContext ctx = new ExtensionContext("/p", "GET", "old", "d", operation);

        ExtensionResult r = processor.process("x-summary", null, ctx);

        assertNull(r.nameOverride());
        assertNull(r.descriptionAppend());
    }
}
