package com.github.rspereiratech.openapi.collection.generator.core.callback;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.callbacks.Callback;

import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Default {@link CallbackProcessor} that flattens callback definitions into path items,
 * enriches their operations with callback metadata, and assigns a "Callbacks" tag
 * when no tags are present.
 */
public class DefaultCallbackProcessor implements CallbackProcessor {

    @Override
    public Map<String, PathItem> extractCallbackPaths(Map<String, Callback> callbacks, String opName, OpenAPI openApi) {
        if (callbacks == null || callbacks.isEmpty()) {
            return Map.of();
        }

        return callbacks.entrySet().stream()
                .filter(e -> Objects.nonNull(e.getValue()))
                .flatMap(e -> e.getValue().entrySet().stream()
                        .map(cb -> toEnrichedEntry(e.getKey(), cb.getKey(), cb.getValue(), opName)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
    }

    /**
     * Converts a callback entry into an enriched path entry with a resolved path key.
     *
     * @param cbName the callback name
     * @param expr   the callback expression or runtime URL
     * @param pi     the path item containing the callback operations
     * @param opName the parent operation name
     * @return a map entry with the resolved path as key and the enriched path item as value
     */
    private Map.Entry<String, PathItem> toEnrichedEntry(String cbName, String expr, PathItem pi, String opName) {
        pi.readOperations().forEach(op -> enrichOperation(op, cbName, opName));
        String path = expr.startsWith("{$") ? "/callbacks/" + opName + "/" + cbName : expr;
        return new AbstractMap.SimpleEntry<>(path, pi);
    }

    /**
     * Enriches a callback operation with summary, description metadata, and a default tag.
     *
     * @param op     the operation to enrich
     * @param cbName the callback name
     * @param opName the parent operation name
     */
    private void enrichOperation(Operation op, String cbName, String opName) {
        if (op.getSummary() == null) {
            op.setSummary(cbName);
        }

        String note = "[Callback: %s from %s]".formatted(cbName, opName);
        op.setDescription(op.getDescription() == null ? note : note + "\n\n" + op.getDescription());

        if (op.getTags() == null || op.getTags().isEmpty()) {
            op.setTags(List.of("Callbacks"));
        }
    }
}
