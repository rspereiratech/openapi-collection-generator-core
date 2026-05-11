package com.github.rspereiratech.openapi.collection.generator.core.id;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UUIDGeneratorTest {

    private final UUIDGenerator generator = new UUIDGenerator();

    @Test
    void prefixIsPreserved() {
        String id = generator.generate("req", "ignored");

        assertTrue(id.startsWith("req_"));
    }

    @Test
    void successiveCallsProduceDifferentIds() {
        String a = generator.generate("req", "x");
        String b = generator.generate("req", "x");

        assertNotEquals(a, b);
    }

    @Test
    void uuidPartHasNoDashes() {
        String id = generator.generate("p", "ctx");
        String suffix = id.substring(2); // strip 'p_'

        assertFalse(suffix.contains("-"));
        assertEquals(32, suffix.length());
    }
}
