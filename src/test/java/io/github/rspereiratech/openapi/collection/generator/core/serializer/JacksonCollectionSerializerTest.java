package io.github.rspereiratech.openapi.collection.generator.core.serializer;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JacksonCollectionSerializerTest {

    private final JacksonCollectionSerializer serializer = new JacksonCollectionSerializer();

    @Test
    void serializesMapAsPrettyJson() throws Exception {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", "test");
        map.put("count", 5);

        String json = serializer.serialize(map);

        assertTrue(json.contains("\"name\""));
        assertTrue(json.contains("\"test\""));
        assertTrue(json.contains("\"count\""));
        assertTrue(json.contains("5"));
        // pretty-printed should contain a newline
        assertTrue(json.contains("\n"));
    }

    @Test
    void serializesNullAsLiteralNull() throws Exception {
        String json = serializer.serialize(null);

        assertEquals("null", json);
    }

    @Test
    void wrapsErrorsInSerializationException() {
        Object cyclic = new Object() {
            @SuppressWarnings("unused")
            public Object getSelf() {
                return this;
            }
        };

        assertThrows(SerializationException.class, () -> serializer.serialize(cyclic));
    }
}
