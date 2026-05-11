package io.github.rspereiratech.openapi.collection.generator.core.id;

/**
 * Contract for generating unique identifiers used in collection items.
 */
public interface IdGenerator {

    /**
     * Generates an identifier string.
     *
     * @param prefix  a short prefix prepended to the identifier (e.g. "req", "fld")
     * @param context contextual information that may influence the generated identifier
     * @return a prefixed identifier string
     */
    String generate(String prefix, String context);
}
