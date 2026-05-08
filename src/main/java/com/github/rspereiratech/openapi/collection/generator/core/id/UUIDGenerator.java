package com.github.rspereiratech.openapi.collection.generator.core.id;

import java.util.UUID;

/**
 * {@link IdGenerator} that produces random identifiers using {@link UUID#randomUUID()}.
 *
 * <p>Each call returns a unique, non-reproducible identifier regardless of the context.</p>
 */
public class UUIDGenerator implements IdGenerator {

    @Override
    public String generate(String prefix, String context) {
        return prefix + "_" + UUID.randomUUID().toString().replace("-", "");
    }
}
