package io.github.rspereiratech.openapi.collection.generator.core.model;

/**
 * Supported output collection formats.
 */
public enum CollectionFormat {
    /** Postman v2.1 collection format. */
    POSTMAN,
    /** Insomnia v4 export format. */
    INSOMNIA;

    /**
     * Converts a case-insensitive string to the corresponding {@link CollectionFormat}.
     *
     * @param value the format name (e.g. "postman", "INSOMNIA")
     * @return the matching {@link CollectionFormat}
     * @throws IllegalArgumentException if the value does not match any known format
     */
    public static CollectionFormat fromString(String value) {
        try {
            return valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "Invalid format: '%s'. Use: POSTMAN or INSOMNIA".formatted(value));
        }
    }
}
