package com.github.rspereiratech.openapi.collection.generator.core.parser;

/**
 * Checked exception thrown when an OpenAPI specification file cannot be parsed or is invalid.
 */
public class OpenApiParseException extends Exception {

    /**
     * Creates a new parse exception with the given message.
     *
     * @param msg description of the parse failure
     */
    public OpenApiParseException(String msg) {
        super(msg);
    }

    /**
     * Creates a new parse exception with the given message and cause.
     *
     * @param msg   description of the parse failure
     * @param cause the underlying cause
     */
    public OpenApiParseException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
