package io.github.rspereiratech.openapi.collection.generator.core.id;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

/**
 * {@link IdGenerator} that produces deterministic identifiers by hashing the context with SHA-256.
 *
 * <p>Given the same prefix and context, this generator always returns the same identifier,
 * which is useful for reproducible builds.</p>
 */
public class DeterministicIdGenerator implements IdGenerator {

    @Override
    public String generate(String prefix, String context) {
        try {
            var digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(context.getBytes(StandardCharsets.UTF_8));
            return prefix + "_" + HexFormat.of().formatHex(hash).substring(0, 16);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate ID", e);
        }
    }
}
