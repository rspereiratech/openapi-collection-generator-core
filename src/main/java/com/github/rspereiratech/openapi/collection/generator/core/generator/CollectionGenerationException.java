package com.github.rspereiratech.openapi.collection.generator.core.generator;

/**
 * Checked exception thrown when collection generation fails (e.g. serialization errors,
 * missing required fields in the OpenAPI model).
 */
public class CollectionGenerationException extends Exception {

    /**
     * Creates a new generation exception with the given message.
     *
     * @param msg description of the generation failure
     */
    public CollectionGenerationException(String msg) {
        super(msg);
    }

    /**
     * Creates a new generation exception with the given message and cause.
     *
     * @param msg   description of the generation failure
     * @param cause the underlying cause
     */
    public CollectionGenerationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
