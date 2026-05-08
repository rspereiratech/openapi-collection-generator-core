package com.github.rspereiratech.openapi.collection.generator.core.serializer;

/**
 * Checked exception thrown when a collection object cannot be serialized to its target format.
 */
public class SerializationException extends Exception {

    /**
     * Creates a new serialization exception.
     *
     * @param msg   a description of the serialization failure
     * @param cause the underlying cause
     */
    public SerializationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
