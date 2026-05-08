package com.github.rspereiratech.openapi.collection.generator.core.writer;

/**
 * Checked exception thrown when a file write operation fails.
 */
public class WriterException extends Exception {

    /**
     * Creates a new writer exception with a message.
     *
     * @param msg a description of the write failure
     */
    public WriterException(String msg) {
        super(msg);
    }

    /**
     * Creates a new writer exception with a message and cause.
     *
     * @param msg   a description of the write failure
     * @param cause the underlying cause
     */
    public WriterException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
