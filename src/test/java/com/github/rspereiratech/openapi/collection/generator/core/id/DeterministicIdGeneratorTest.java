package com.github.rspereiratech.openapi.collection.generator.core.id;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeterministicIdGeneratorTest {

    private final DeterministicIdGenerator generator = new DeterministicIdGenerator();

    @Test
    void generatesSameIdForSameContext() {
        String a = generator.generate("req", "hello");
        String b = generator.generate("req", "hello");

        assertEquals(a, b);
    }

    @Test
    void prefixIsPreserved() {
        String id = generator.generate("fld", "ctx");

        assertTrue(id.startsWith("fld_"));
    }

    @Test
    void differentContextProducesDifferentId() {
        String a = generator.generate("req", "one");
        String b = generator.generate("req", "two");

        assertNotEquals(a, b);
    }

    @Test
    void hexSuffixIsSixteenCharacters() {
        String id = generator.generate("p", "anything");

        // p + _ + 16 hex chars = 18
        assertEquals(18, id.length());
    }
}
