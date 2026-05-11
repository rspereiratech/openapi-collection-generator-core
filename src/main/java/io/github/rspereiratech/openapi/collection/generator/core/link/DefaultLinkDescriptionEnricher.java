package io.github.rspereiratech.openapi.collection.generator.core.link;

import io.swagger.v3.oas.models.links.Link;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Default {@link LinkDescriptionEnricher} that appends a "Related Operations" Markdown
 * section listing each link's name, target operation ID, and description.
 */
public class DefaultLinkDescriptionEnricher implements LinkDescriptionEnricher {

    @Override
    public String enrich(String desc, Map<String, Link> links) {
        if (links == null || links.isEmpty()) {
            return desc;
        }

        String section = "**Related Operations:**\n" + links.entrySet().stream()
                .map(e -> formatLink(e.getKey(), e.getValue()))
                .collect(Collectors.joining("\n"));

        return desc.isBlank() ? section : desc + "\n\n" + section;
    }

    /**
     * Formats a single link entry as a Markdown list item.
     *
     * @param name the link name
     * @param link the link definition
     * @return a formatted Markdown line
     */
    private String formatLink(String name, Link link) {
        var sb = new StringBuilder("- **").append(name).append("**");
        if (link.getOperationId() != null) {
            sb.append(" → `").append(link.getOperationId()).append("`");
        }

        if (link.getDescription() != null) {
            sb.append(": ").append(link.getDescription());
        }

        return sb.toString();
    }
}
