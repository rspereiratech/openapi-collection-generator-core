# Configuration

All inputs to the pipeline flow through two records: `GenerationRequest` (the public input) and `GenerationConfig` (the per-format view used internally).

## `GenerationRequest`

```java
public record GenerationRequest(
        File specFile,
        File outputDirectory,
        List<CollectionFormat> formats,
        String collectionName,
        String fileNamePattern) { ... }
```

| Field | Required | Notes |
|-------|----------|-------|
| `specFile` | yes | Path to an OpenAPI 3.x file (YAML or JSON). Validated by `SpecLoader`. |
| `outputDirectory` | yes | Directory where the collection and any environment files are written. Created by the writer if missing. |
| `formats` | yes | At least one `CollectionFormat`. The compact constructor throws `IllegalArgumentException` if empty. |
| `collectionName` | optional | Human-readable name of the generated collection. Used in the `{name}` placeholder. |
| `fileNamePattern` | optional | Output file name template. Defaults to `{name}_{format}.json`. |

A convenience constructor lets you omit the file name pattern:

```java
new GenerationRequest(spec, outDir, List.of(POSTMAN, INSOMNIA), "My API");
// equivalent to
new GenerationRequest(spec, outDir, List.of(POSTMAN, INSOMNIA), "My API",
        GenerationConfig.DEFAULT_FILE_NAME_PATTERN);
```

### Multi-format invariant

When `formats.size() > 1`, the pattern **must** include `{format}`. Otherwise the second format's output would overwrite the first. The compact constructor enforces this:

```java
// Throws IllegalArgumentException
new GenerationRequest(spec, outDir,
        List.of(POSTMAN, INSOMNIA), "My API", "{name}.json");
```

## `GenerationConfig`

`GenerationConfig` is the per-format slice of a request, produced by `request.toGenerationConfig(format)`:

```java
public record GenerationConfig(
        File outputDirectory,
        CollectionFormat format,
        String collectionName,
        String fileNamePattern) {

    public static final String DEFAULT_FILE_NAME_PATTERN = "{name}_{format}.json";
}
```

Generators and writers consume `GenerationConfig`, never `GenerationRequest`. This keeps each stage agnostic to the multi-format orchestration above it.

## File name pattern

Two placeholders are supported in `fileNamePattern`:

| Placeholder | Replaced with |
|-------------|----------------|
| `{name}`    | `collectionName` (sanitised) |
| `{format}`  | the format's lowercase name (e.g. `postman`, `insomnia`) |

Examples:

| Pattern | Format | Result |
|---------|--------|--------|
| `{name}_{format}.json` (default) | `POSTMAN` | `My_API_postman.json` |
| `{name}.{format}.collection.json` | `INSOMNIA` | `My_API.insomnia.collection.json` |
| `collection.json` | single format | `collection.json` |

Environment files have their own naming, derived from the collection name and server name (see [architecture.md](architecture.md#servers-and-environments-server)).

## Supported formats

`CollectionFormat`:

- `POSTMAN` — Postman Collection v2.1
- `INSOMNIA` — Insomnia Export v4

`CollectionFormat.fromString(String)` accepts case-insensitive input and throws `IllegalArgumentException` with a friendly message for unknown values.

## Wiring an orchestrator

`CollectionGenerationOrchestrator` requires every stage to be supplied via constructor injection:

```java
CollectionGenerationOrchestrator orchestrator = new CollectionGenerationOrchestrator(
        new FileSpecLoader(),
        new SwaggerOpenApiParser(),
        myCollectionGeneratorFactory,
        new FileCollectionWriter(new JacksonCollectionSerializer()),
        new FileEnvironmentWriter(new JacksonCollectionSerializer())
);
```

`myCollectionGeneratorFactory` is an implementation of `CollectionGeneratorFactory` that returns a configured `CollectionGenerator` for each `CollectionFormat`. Format-specific generators live in their own modules (e.g. `openapi-collection-generator-postman`, `openapi-collection-generator-insomnia`); this core module only defines the contract.

## Java and dependencies

- Requires **Java 17+** (`record`, `switch` expressions, `HexFormat`).
- Pulls in `swagger-parser` (parsing) and `jackson-databind` (serialisation).
- Test scope: JUnit 5 and Mockito.

Versions are managed in the parent POM `openapi-collection-generator-parent`.
