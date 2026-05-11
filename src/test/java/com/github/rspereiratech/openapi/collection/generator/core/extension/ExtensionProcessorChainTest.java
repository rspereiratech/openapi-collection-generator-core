package com.github.rspereiratech.openapi.collection.generator.core.extension;

import io.swagger.v3.oas.models.Operation;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExtensionProcessorChainTest {

    private final ExtensionProcessor processorA = mock(ExtensionProcessor.class);
    private final ExtensionProcessor processorB = mock(ExtensionProcessor.class);
    private final Operation operation = mock(Operation.class);

    @Test
    void returnsEmptyResultWhenOperationHasNoExtensions() {
        when(operation.getExtensions()).thenReturn(null);
        ExtensionProcessorChain chain = new ExtensionProcessorChain(List.of(processorA));
        ExtensionContext ctx = new ExtensionContext("/p", "GET", "n", "d", operation);

        ExtensionResult r = chain.process(ctx);

        assertNull(r.nameOverride());
        assertNull(r.descriptionAppend());
    }

    @Test
    void appliesMatchingProcessorAndCollectsResults() {
        Map<String, Object> exts = new LinkedHashMap<>();
        exts.put("x-foo", "value");
        when(operation.getExtensions()).thenReturn(exts);
        when(processorA.supports("x-foo")).thenReturn(true);
        when(processorB.supports("x-foo")).thenReturn(false);

        ExtensionContext ctx = new ExtensionContext("/p", "GET", "old", "d", operation);
        when(processorA.process(eq("x-foo"), eq("value"), any()))
                .thenReturn(new ExtensionResult("newName", "extra"));

        ExtensionProcessorChain chain = new ExtensionProcessorChain(List.of(processorA, processorB));

        ExtensionResult r = chain.process(ctx);

        assertEquals("newName", r.nameOverride());
        assertEquals("extra", r.descriptionAppend());
    }

    @Test
    void lastNameOverrideWins() {
        Map<String, Object> exts = new LinkedHashMap<>();
        exts.put("x-a", "1");
        exts.put("x-b", "2");
        when(operation.getExtensions()).thenReturn(exts);
        when(processorA.supports("x-a")).thenReturn(true);
        when(processorA.supports("x-b")).thenReturn(false);
        when(processorB.supports("x-a")).thenReturn(false);
        when(processorB.supports("x-b")).thenReturn(true);

        ExtensionContext ctx = new ExtensionContext("/p", "GET", "orig", null, operation);
        when(processorA.process(eq("x-a"), eq("1"), any()))
                .thenReturn(new ExtensionResult("first", null));
        when(processorB.process(eq("x-b"), eq("2"), any()))
                .thenReturn(new ExtensionResult("second", null));

        ExtensionProcessorChain chain = new ExtensionProcessorChain(List.of(processorA, processorB));

        ExtensionResult r = chain.process(ctx);

        assertEquals("second", r.nameOverride());
    }

    @Test
    void descriptionAppendsAreJoinedWithNewline() {
        Map<String, Object> exts = new LinkedHashMap<>();
        exts.put("x-a", "v1");
        exts.put("x-b", "v2");
        when(operation.getExtensions()).thenReturn(exts);
        when(processorA.supports("x-a")).thenReturn(true);
        when(processorA.supports("x-b")).thenReturn(false);
        when(processorB.supports("x-a")).thenReturn(false);
        when(processorB.supports("x-b")).thenReturn(true);

        ExtensionContext ctx = new ExtensionContext("/p", "GET", "n", "d", operation);
        when(processorA.process(eq("x-a"), eq("v1"), any()))
                .thenReturn(new ExtensionResult(null, "lineA"));
        when(processorB.process(eq("x-b"), eq("v2"), any()))
                .thenReturn(new ExtensionResult(null, "lineB"));

        ExtensionProcessorChain chain = new ExtensionProcessorChain(List.of(processorA, processorB));

        ExtensionResult r = chain.process(ctx);

        assertTrue(r.descriptionAppend().contains("lineA"));
        assertTrue(r.descriptionAppend().contains("lineB"));
        assertTrue(r.descriptionAppend().contains("\n"));
    }
}
