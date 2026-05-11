package com.github.rspereiratech.openapi.collection.generator.core.link;

import io.swagger.v3.oas.models.links.Link;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultLinkDescriptionEnricherTest {

    private final DefaultLinkDescriptionEnricher enricher = new DefaultLinkDescriptionEnricher();

    @Test
    void returnsOriginalWhenLinksNull() {
        assertEquals("hello", enricher.enrich("hello", null));
    }

    @Test
    void returnsOriginalWhenLinksEmpty() {
        assertEquals("hello", enricher.enrich("hello", Map.of()));
    }

    @Test
    void appendsRelatedOperationsSectionWithOperationIdAndDescription() {
        Link link = new Link();
        link.setOperationId("getPet");
        link.setDescription("Fetch related pet");

        Map<String, Link> links = new LinkedHashMap<>();
        links.put("petLink", link);

        String result = enricher.enrich("base", links);

        assertTrue(result.startsWith("base\n\n"));
        assertTrue(result.contains("**Related Operations:**"));
        assertTrue(result.contains("**petLink**"));
        assertTrue(result.contains("`getPet`"));
        assertTrue(result.contains("Fetch related pet"));
    }

    @Test
    void omitsOperationIdSegmentWhenAbsent() {
        Link link = new Link();
        link.setDescription("desc");
        Map<String, Link> links = Map.of("l", link);

        String result = enricher.enrich("", links);

        assertTrue(result.startsWith("**Related Operations:**"));
        assertTrue(result.contains("**l**"));
        assertTrue(result.contains("desc"));
    }

    @Test
    void returnsSectionAloneWhenDescriptionBlank() {
        Link link = new Link();
        link.setOperationId("op");
        String result = enricher.enrich("  ", Map.of("k", link));

        assertTrue(result.startsWith("**Related Operations:**"));
    }
}
