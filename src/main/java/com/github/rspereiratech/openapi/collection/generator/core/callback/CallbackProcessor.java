package com.github.rspereiratech.openapi.collection.generator.core.callback;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.callbacks.Callback;

import java.util.Map;

/**
 * Extracts callback definitions from an OpenAPI operation and converts them
 * into regular path items that can be included in a collection.
 */
public interface CallbackProcessor {

    /**
     * Extracts path items from the given callback definitions.
     *
     * @param callbacks     the callbacks map from an OpenAPI operation; may be {@code null}
     * @param operationName the name of the parent operation
     * @param openApi       the full OpenAPI specification
     * @return a map of path strings to {@link PathItem} instances; never {@code null}
     */
    Map<String, PathItem> extractCallbackPaths(Map<String, Callback> callbacks, String operationName, OpenAPI openApi);
}
