package io.github.rspereiratech.openapi.collection.generator.core.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * {@link CollectionSerializer} implementation backed by Jackson, producing
 * pretty-printed JSON output.
 */
public class JacksonCollectionSerializer implements CollectionSerializer {

    /**
     * Jackson mapper configured for pretty-printed JSON output.
     */
    private final ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    @Override
    public String serialize(Object o) throws SerializationException {
        try {
            return mapper.writeValueAsString(o);
        } catch (Exception e) {
            throw new SerializationException("Serialization failed", e);
        }
    }
}
