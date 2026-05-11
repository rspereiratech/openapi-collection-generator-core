package io.github.rspereiratech.openapi.collection.generator.core.serializer;

/**
 * Serializes a collection object into its JSON string representation.
 */
public interface CollectionSerializer {

    /**
     * Serializes the given collection object to a JSON string.
     *
     * @param collection the collection object to serialize
     * @return the JSON string representation
     * @throws SerializationException if serialization fails
     */
    String serialize(Object collection) throws SerializationException;
}
