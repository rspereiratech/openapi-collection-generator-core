package io.github.rspereiratech.openapi.collection.generator.core.extension;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

/**
 * Aggregates multiple {@link ExtensionProcessor} instances and applies them
 * sequentially to all vendor extensions found on an OpenAPI operation.
 * Name overrides from later processors win; description fragments are concatenated.
 */
public class ExtensionProcessorChain {

    /**
     * Ordered list of processors evaluated for each operation's vendor extensions.
     */
    private final List<ExtensionProcessor> processors;

    /**
     * Creates a new chain with the given processors.
     *
     * @param processors the ordered list of extension processors to apply
     */
    public ExtensionProcessorChain(List<ExtensionProcessor> processors) {
        this.processors = processors;
    }

    /**
     * Processes all vendor extensions on the operation in the given context.
     *
     * @param ctx the extension context containing the operation and its metadata
     * @return a combined {@link ExtensionResult} with all accumulated modifications
     */
    public ExtensionResult process(ExtensionContext ctx) {
        Map<String, Object> exts = Optional.ofNullable(ctx.operation().getExtensions()).orElse(Map.of());

        List<ExtensionResult> results = exts.entrySet().stream()
                .flatMap(e -> processors.stream()
                        .filter(p -> p.supports(e.getKey()))
                        .map(p -> p.process(e.getKey(), e.getValue(), ctx)))
                .toList();

        String nameOverride = results.stream()
                .map(ExtensionResult::nameOverride)
                .filter(Objects::nonNull)
                .reduce((first, last) -> last)
                .orElse(null);

        StringJoiner desc = new StringJoiner("\n");
        results.stream().map(ExtensionResult::descriptionAppend).filter(Objects::nonNull).forEach(desc::add);

        return new ExtensionResult(nameOverride, desc.length() == 0 ? null : desc.toString());
    }
}
