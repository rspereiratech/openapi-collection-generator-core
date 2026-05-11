package io.github.rspereiratech.openapi.collection.generator.core.extension.impl;

import io.github.rspereiratech.openapi.collection.generator.core.extension.ExtensionContext;
import io.github.rspereiratech.openapi.collection.generator.core.extension.ExtensionResult;
import io.swagger.v3.oas.models.Operation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class XInternalExtensionProcessorTest {

    private final Operation operation = mock(Operation.class);
    private final XInternalExtensionProcessor processor = new XInternalExtensionProcessor();

    @Test
    void supportsXInternalKeyOnly() {
        assertTrue(processor.supports("x-internal"));
        assertFalse(processor.supports("x-other"));
    }

    @Test
    void appendsInternalTagToNameWhenTrue() {
        ExtensionContext ctx = new ExtensionContext("/p", "GET", "Get", "d", operation);

        ExtensionResult r = processor.process("x-internal", true, ctx);

        assertEquals("Get (Internal)", r.nameOverride());
        assertNotNull(r.descriptionAppend());
        assertTrue(r.descriptionAppend().contains("Internal"));
    }

    @Test
    void noChangeWhenFalse() {
        ExtensionContext ctx = new ExtensionContext("/p", "GET", "Get", "d", operation);

        ExtensionResult r = processor.process("x-internal", false, ctx);

        assertNull(r.nameOverride());
        assertNull(r.descriptionAppend());
    }
}
