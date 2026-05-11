package io.github.rspereiratech.openapi.collection.generator.core.loader;

/**
 * Checked exception thrown when an OpenAPI specification file fails validation.
 */
public class SpecValidationException extends Exception {

    public SpecValidationException(String msg) {
        super(msg);
    }

    public SpecValidationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
