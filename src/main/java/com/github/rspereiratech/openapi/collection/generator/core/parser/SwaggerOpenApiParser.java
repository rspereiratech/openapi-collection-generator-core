package com.github.rspereiratech.openapi.collection.generator.core.parser;

import java.io.File;
import java.util.List;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.ParseOptions;

/**
 * {@link OpenApiParser} implementation backed by the Swagger Parser library.
 *
 * <p>Fully resolves all {@code $ref} references during parsing.</p>
 */
public class SwaggerOpenApiParser implements OpenApiParser {

    @Override
    public OpenAPI parse(File specFile) throws OpenApiParseException {
        var options = new ParseOptions();
        options.setResolve(true);
        options.setResolveFully(true);

        var result = new OpenAPIParser().readLocation(specFile.toURI().toString(), null, options);

        List<String> messages = result.getMessages();
        if (messages != null && !messages.isEmpty()) {
            throw new OpenApiParseException("Spec errors: " + String.join(", ", messages));
        }

        OpenAPI api = result.getOpenAPI();
        if (api == null) {
            throw new OpenApiParseException("Invalid spec: " + specFile);
        }

        return api;
    }
}
