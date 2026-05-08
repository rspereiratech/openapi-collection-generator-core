package com.github.rspereiratech.openapi.collection.generator.core.extension;

/**
 * Result returned by an {@link ExtensionProcessor}, containing optional overrides
 * for the operation name and description.
 *
 * @param nameOverride      a replacement name, or {@code null} to keep the current name
 * @param descriptionAppend text to append to the description, or {@code null} to leave it unchanged
 */
public record ExtensionResult(String nameOverride, String descriptionAppend) {

    /**
     * Returns a result that indicates no changes should be applied.
     *
     * @return an {@code ExtensionResult} with all fields set to {@code null}
     */
    public static ExtensionResult noChange() {
        return new ExtensionResult(null, null);
    }
}
