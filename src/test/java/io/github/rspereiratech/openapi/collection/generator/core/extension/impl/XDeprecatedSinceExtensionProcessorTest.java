package io.github.rspereiratech.openapi.collection.generator.core.extension.impl;

import io.github.rspereiratech.openapi.collection.generator.core.extension.ExtensionContext;
import io.github.rspereiratech.openapi.collection.generator.core.extension.ExtensionResult;
import io.swagger.v3.oas.models.Operation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class XDeprecatedSinceExtensionProcessorTest {

    private final Operation operation = mock(Operation.class);
    private final XDeprecatedSinceExtensionProcessor processor = new XDeprecatedSinceExtensionProcessor();

    @Test
    void supportsXDeprecatedSinceKey() {
        assertTrue(processor.supports("x-deprecated-since"));
        assertFalse(processor.supports("x-deprecated"));
    }

    @Test
    void appendsDeprecatedSinceVersionToDescription() {
        ExtensionContext ctx = new ExtensionContext("/p", "GET", "n", "d", operation);

        ExtensionResult r = processor.process("x-deprecated-since", "2.0", ctx);

        assertNull(r.nameOverride());
        assertEquals("Deprecated since: **2.0**", r.descriptionAppend());
    }

    @Test
    void noChangeWhenValueNull() {
        ExtensionContext ctx = new ExtensionContext("/p", "GET", "n", "d", operation);

        ExtensionResult r = processor.process("x-deprecated-since", null, ctx);

        assertNull(r.nameOverride());
        assertNull(r.descriptionAppend());
    }
}
