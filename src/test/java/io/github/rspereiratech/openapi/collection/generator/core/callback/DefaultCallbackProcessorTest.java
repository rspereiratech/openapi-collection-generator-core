package io.github.rspereiratech.openapi.collection.generator.core.callback;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.callbacks.Callback;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultCallbackProcessorTest {

    private final DefaultCallbackProcessor processor = new DefaultCallbackProcessor();

    @Test
    void returnsEmptyMapWhenCallbacksNull() {
        Map<String, PathItem> result = processor.extractCallbackPaths(null, "op", new OpenAPI());

        assertTrue(result.isEmpty());
    }

    @Test
    void returnsEmptyMapWhenCallbacksEmpty() {
        Map<String, PathItem> result = processor.extractCallbackPaths(Map.of(), "op", new OpenAPI());

        assertTrue(result.isEmpty());
    }

    @Test
    void rewritesRuntimeExpressionToCallbacksPath() {
        Operation op = new Operation();
        PathItem pi = new PathItem();
        pi.setPost(op);
        Callback cb = new Callback();
        cb.put("{$request.body#/url}", pi);

        Map<String, Callback> callbacks = new LinkedHashMap<>();
        callbacks.put("onEvent", cb);

        Map<String, PathItem> result = processor.extractCallbackPaths(callbacks, "createPet", new OpenAPI());

        assertEquals(1, result.size());
        assertTrue(result.containsKey("/callbacks/createPet/onEvent"));
        assertSame(pi, result.get("/callbacks/createPet/onEvent"));
    }

    @Test
    void preservesLiteralCallbackPath() {
        Operation op = new Operation();
        PathItem pi = new PathItem();
        pi.setPost(op);
        Callback cb = new Callback();
        cb.put("/literal", pi);

        Map<String, PathItem> result = processor.extractCallbackPaths(
                Map.of("k", cb), "opName", new OpenAPI());

        assertTrue(result.containsKey("/literal"));
    }

    @Test
    void enrichesOperationWithSummaryDescriptionAndTag() {
        Operation op = new Operation();
        PathItem pi = new PathItem();
        pi.setPost(op);
        Callback cb = new Callback();
        cb.put("{$request.body}", pi);

        Map<String, PathItem> result = processor.extractCallbackPaths(
                Map.of("onCreated", cb), "createPet", new OpenAPI());

        PathItem out = result.values().iterator().next();
        Operation gen = out.getPost();
        assertEquals("onCreated", gen.getSummary());
        assertNotNull(gen.getDescription());
        assertTrue(gen.getDescription().contains("[Callback: onCreated from createPet]"));
        assertEquals(List.of("Callbacks"), gen.getTags());
    }

    @Test
    void preservesExistingSummaryAndTags() {
        Operation op = new Operation();
        op.setSummary("predefined");
        op.setTags(List.of("Webhooks"));
        op.setDescription("original");
        PathItem pi = new PathItem();
        pi.setPost(op);
        Callback cb = new Callback();
        cb.put("{$ev}", pi);

        Map<String, PathItem> result = processor.extractCallbackPaths(
                Map.of("cbName", cb), "op", new OpenAPI());

        Operation out = result.values().iterator().next().getPost();
        assertEquals("predefined", out.getSummary());
        assertEquals(List.of("Webhooks"), out.getTags());
        assertTrue(out.getDescription().startsWith("[Callback: cbName from op]"));
        assertTrue(out.getDescription().contains("original"));
    }

    @Test
    void ignoresNullCallbackEntry() {
        Map<String, Callback> callbacks = new LinkedHashMap<>();
        callbacks.put("nullCb", null);
        Operation op = new Operation();
        PathItem pi = new PathItem();
        pi.setPost(op);
        Callback cb = new Callback();
        cb.put("/p", pi);
        callbacks.put("ok", cb);

        Map<String, PathItem> result = processor.extractCallbackPaths(callbacks, "opName", new OpenAPI());

        assertEquals(1, result.size());
        assertTrue(result.containsKey("/p"));
    }
}
